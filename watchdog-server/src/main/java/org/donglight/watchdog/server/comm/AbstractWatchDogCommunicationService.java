package org.donglight.watchdog.server.comm;

import lombok.extern.slf4j.Slf4j;
import org.donglight.watchdog.common.bean.*;
import org.donglight.watchdog.common.rmi.WatchDogCommunicationService;
import org.donglight.watchdog.common.util.Response;
import org.donglight.watchdog.common.util.ResponseEnum;
import org.donglight.watchdog.server.comm.web.PollingState;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AbstractWatchDogCommunicationService 抽象类，把子类的相同逻辑放到这里
 *
 * @author donglight
 * @date 2019/5/12
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractWatchDogCommunicationService implements WatchDogCommunicationService {

    protected final WebApplicationManager webApplicationManager;

    protected static final String PING = "ping";
    protected static final String PONG = "pong";
    protected static final String WRONG_HEART_BEAT = "wrong heartbeat";

    protected AbstractWatchDogCommunicationService(WebApplicationManager webApplicationManager) {
        this.webApplicationManager = webApplicationManager;
    }

    @Override
    public Response registerApplication(WatchDogClientConfigBean watchDogClientConfigBean) {
        Optional<InstanceInfo> instanceInfoOptional = webApplicationManager.registerApplication(watchDogClientConfigBean);
        PollingState.setApplicationsChanged(true);
        return instanceInfoOptional
                .map(instanceInfo -> Response.ok("registration was successful", instanceInfo))
                .orElseGet(() -> Response.exception(ResponseEnum.REGISTER_FAIL));
    }

    @Override
    public String renewApplication(String appName,
                                   String instanceId,
                                   String ping) {
        if (webApplicationManager.getInstance(appName, instanceId) != null) {
            if (PING.equals(ping)) {
                webApplicationManager.renewApplication(appName, instanceId);
                log.debug("instance '{}' of app '{}' renew success", instanceId, appName);
                return PONG;
            }
        }
        return WRONG_HEART_BEAT;
    }

    @Override
    public Response setUrls(String appName,
                            String instanceId,
                            List<URLInfo> urlInfoList) {
        InstanceInfo instance = webApplicationManager.getInstance(appName, instanceId);
        if (instance != null) {
            instance.setUrlInfoList(urlInfoList);
            log.debug("successful set urls into instance '{}' of application '{}' ", instanceId, appName);
            return Response.ok("successful set urls into application", appName);
        } else {
            return Response.exception(ResponseEnum.NOELEMENT, instanceId);
        }
    }


    @Override
    public Response addNewURLInfo(String appName,
                                  String instanceId,
                                  URLInfo urlInfo) {
        InstanceInfo instance = webApplicationManager.getInstance(appName, instanceId);
        if (instance != null) {
            urlInfo.getRequestTimes().incrementAndGet();
            urlInfo.getMaxConcurrency().incrementAndGet();
            urlInfo.setSuccessRate(1);
            instance.getUrlInfoList().add(urlInfo);
            //数据更新，修改相应的轮询状态
            PollingState.setURLInfosChanged(true);
            log.debug("successful add url '{}' into url list", urlInfo.getUrl());
            return Response.ok("successful add url into url list", "");
        }
        return Response.exception(ResponseEnum.NOELEMENT, "");
    }


    @Override
    public Response addURLState(String appName, String instanceId, URLState urlState) {
        InstanceInfo instance = webApplicationManager.getInstance(appName, instanceId);
        WebApplication application = webApplicationManager.getApplication(appName);
        if (instance == null) {
            return Response.exception(ResponseEnum.NOELEMENT);
        }
        long urlId = urlState.getUrlId();
        URLInfo urlInfo = instance.getUrlInfoList().stream().filter(url -> url.getId() == urlId).findAny().orElse(null);

        if (urlInfo == null) {
            return Response.exception(ResponseEnum.NOELEMENT, "");
        } else {
            urlInfo.getUrlStatList().add(urlState);
            AtomicInteger maxConcurrency = urlInfo.getMaxConcurrency();

            int oldConcurrency = maxConcurrency.get();
            int currentConcurrency = urlState.getCurrentConcurrency();
            if (oldConcurrency < currentConcurrency) {
                maxConcurrency.compareAndSet(oldConcurrency, urlState.getCurrentConcurrency());
            }

            int oldFailTimes = urlInfo.getFailTimes().get();
            urlInfo.getFailTimes().compareAndSet(oldFailTimes, urlState.getCurrentFailTimes());

            long requestTimes = urlInfo.getRequestTimes().incrementAndGet();
            urlInfo.setSuccessRate((requestTimes - urlInfo.getFailTimes().get()) * 1.0 / requestTimes);
            urlInfo.setTotalTime(urlInfo.getTotalTime() + urlState.getDuration());

            if (urlInfo.getMaxDuration() < urlState.getDuration()) {
                urlInfo.setMaxDuration(urlState.getDuration());
            }

            // 访问次数加一
            instance.getTotalAccessCount().incrementAndGet();
            application.getAppTotalAccessCount().incrementAndGet();
            //数据更新，修改相应轮询状态
            PollingState.setURLInfosChanged(true);
            PollingState.addChangedURLState(urlState);

            log.debug("successful add URLState {} into URLInfo list", urlState);
            return Response.ok("successful add URLState into URLInfo", "ok");
        }
    }


    @Override
    public Response setMetrics(String appName,
                               String instanceId,
                               Metrics metrics) {
        webApplicationManager.getInstance(appName, instanceId).setMetrics(metrics);
        log.debug("successful set metrics into instance '{}'", instanceId);
        return Response.ok("successful receive metrics", "ok");
    }
}
