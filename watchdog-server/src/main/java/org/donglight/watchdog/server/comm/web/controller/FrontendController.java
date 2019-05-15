package org.donglight.watchdog.server.comm.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.donglight.watchdog.common.bean.InstanceInfo;
import org.donglight.watchdog.common.bean.Metrics;
import org.donglight.watchdog.common.bean.URLInfo;
import org.donglight.watchdog.common.bean.URLState;
import org.donglight.watchdog.common.util.Response;
import org.donglight.watchdog.common.util.ResponseEnum;
import org.donglight.watchdog.server.comm.web.AsyncTask;
import org.donglight.watchdog.server.comm.WebApplicationManager;
import org.donglight.watchdog.server.comm.web.PollingState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * 前端数据接口
 *
 * @author donglight
 * @date 2019/4/20
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class FrontendController {

    private final WebApplicationManager webApplicationManager;

    private final AsyncTask asyncPooling;

    @Autowired
    public FrontendController(WebApplicationManager webApplicationManager, AsyncTask asyncPooling) {
        this.webApplicationManager = webApplicationManager;
        this.asyncPooling = asyncPooling;
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response getAllApp() {
        PollingState.setApplicationsChanged(false);
        return Response.ok(webApplicationManager.getAllApplications());
    }

    @GetMapping(value = "/{appName}/{instanceId}/urls", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response getAppUrls(@PathVariable("appName") String appName, @PathVariable("instanceId") String instanceId) {
        InstanceInfo instance = webApplicationManager.getInstance(appName, instanceId);
        if (instance != null) {
            PollingState.setURLInfosChanged(false);
            return Response.ok(instance.getUrlInfoList());
        }
        return Response.ok(ResponseEnum.NOELEMENT, "");
    }

    @GetMapping(value = "/{appName}/{instanceId}/osInfo", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response osInfo(@PathVariable("appName") String appName, @PathVariable("instanceId") String instanceId) {
        InstanceInfo instance = webApplicationManager.getInstance(appName, instanceId);
        Metrics metrics = null;
        if (instance != null) {
            metrics = instance.getMetrics();
        }
        return Response.ok(metrics != null ? metrics.getOsInfo() : "");
    }

    @GetMapping(value = "/{appName}/{instanceId}/jvmInfo", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response jvmInfo(@PathVariable("appName") String appName, @PathVariable("instanceId") String instanceId) {
        InstanceInfo instance = webApplicationManager.getInstance(appName, instanceId);
        Metrics metrics = null;
        if (instance != null) {
            metrics = instance.getMetrics();
        }
        return Response.ok(metrics != null ? metrics.getJvmInfo() : "");
    }

    @GetMapping(value = "/{appName}/{instanceId}/threadInfo", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response threadInfo(@PathVariable("appName") String appName, @PathVariable("instanceId") String instanceId) {
        InstanceInfo instance = webApplicationManager.getInstance(appName, instanceId);
        Metrics metrics = null;
        if (instance != null) {
            metrics = instance.getMetrics();
        }
        return Response.ok(metrics != null ? metrics.getThreadInfo() : "");
    }

    /**
     * 长轮询获得最新的app
     *
     * @return Response Response
     * @throws InterruptedException InterruptedException
     */
    @GetMapping(value = "/changed_apps", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response getLatestAllAppByLongPolling() throws InterruptedException {
        Future<Response> result = asyncPooling.pollingForApps(webApplicationManager);
        try {
            return result.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            log.debug("changed_app pooling ExecutionException");
            return Response.exception(ResponseEnum.SERVER_INTERNAL_ERROR, "");
        } catch (TimeoutException e) {
            log.debug("changed_app pooling TimeoutException");
            // 轮询超时，因为没有数据更新，返回响应
            return Response.exception(ResponseEnum.NO_DATA_UPDATE, "");
        } finally {
            if (result != null) {
                result.cancel(true);
            }
        }
    }

    @GetMapping(value = "/{appName}/{instanceId}/changed_urls", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response getLatestUrlByLongPolling(@PathVariable("appName") String appName, @PathVariable("instanceId") String instanceId) throws InterruptedException {
        Future<Response<List<URLInfo>>> result = asyncPooling.pollingForUrl(webApplicationManager, appName, instanceId);
        try {
            // get 设置超时时间
            return result.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            return Response.exception(ResponseEnum.SERVER_INTERNAL_ERROR, "");
        } catch (TimeoutException e) {
            log.debug("changed_urls pooling TimeoutException");
            // 轮询超时，因为没有数据更新，返回响应
            return Response.exception(ResponseEnum.NO_DATA_UPDATE, "");
        } finally {
            if (result != null) {
                result.cancel(true);
            }
        }
    }

    @GetMapping(value = "/latest_url_state/{urlId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response getLatestUrlStates(@PathVariable("urlId") Long urlId) {
        List<URLState> urlStateList = PollingState.getLatestChangedURLStates(urlId);
        long now = System.currentTimeMillis();
        if (urlStateList.size() > 0) {
            List<URLState> temp = urlStateList.stream().filter(urlState -> now - urlState.getStart() < 1000).limit(100).collect(Collectors.toList());
            urlStateList.clear();
            return Response.ok(temp);
        }
        return Response.ok("There are not latest url states", "");
    }

    @GetMapping(value = "/{appName}/{instanceId}/recent_url_state/{urlId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response getRecentUrlStates(@PathVariable("appName") String appName,
                                       @PathVariable("instanceId") String instanceId,
                                       @PathVariable("urlId") Long urlId) {
        List<URLState> urlStateList = findURLStateList(appName, instanceId, urlId);
        long now = System.currentTimeMillis();
        List<URLState> urlStates = urlStateList.stream().filter(urlState -> now - urlState.getStart() < 1000 * 60).limit(100).collect(Collectors.toList());
        return Response.ok(urlStates);
    }

    private List<URLState> findURLStateList(String appName, String instanceId, long urlId) {
        List<URLInfo> urlInfoList = webApplicationManager.getInstance(appName, instanceId).getUrlInfoList();
        List<URLState> urlStatList = null;
        if (urlInfoList != null) {
            for (URLInfo urlInfo : urlInfoList) {
                if (urlInfo.getId() == urlId) {
                    urlStatList = urlInfo.getUrlStatList();
                    break;
                }
            }
        }
        return urlStatList == null ? Collections.emptyList() : urlStatList;
    }

}
