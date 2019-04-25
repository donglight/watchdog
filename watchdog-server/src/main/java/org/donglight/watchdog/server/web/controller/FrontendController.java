package org.donglight.watchdog.server.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.donglight.watchdog.common.domain.WebApplication;
import org.donglight.watchdog.common.util.Response;
import org.donglight.watchdog.server.util.PollingState;
import org.donglight.watchdog.server.web.AsyncPooling;
import org.donglight.watchdog.server.web.WebApplicationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    private final AsyncPooling asyncPooling;

    @Autowired
    public FrontendController(WebApplicationManager webApplicationManager, AsyncPooling asyncPooling) {
        this.webApplicationManager = webApplicationManager;
        this.asyncPooling = asyncPooling;
    }

    @GetMapping(value = "/application", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response getAllApp() {
        PollingState.setChangeApplications(false);
        return Response.ok(webApplicationManager.getAllApplications());
    }

    @GetMapping(value = "/application/{appId}/urls",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response getAppUrls(@PathVariable String appId){
        PollingState.setChangeApplications(false);
        WebApplication application = webApplicationManager.getApplicationById(appId);
        return Response.ok(application.getUrlInfoList());
    }

    @GetMapping(value = "/application/polling", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response getAllAppByLongPolling() throws InterruptedException {
        Future<Response> result = asyncPooling.pollingForApps(webApplicationManager);
        try {
            result.get(15, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            log.info("pooling ExecutionException");
        } catch (TimeoutException e) {
            log.info("pooling TimeoutException");
            result.cancel(true);
        }finally {
            if (result != null){
                result.cancel(true);
            }
        }
        return Response.ok(webApplicationManager.getAllApplications());
    }

}
