package org.donglight.watchdog.server.comm.web;

import org.donglight.watchdog.common.bean.URLInfo;
import org.donglight.watchdog.common.util.Response;
import org.donglight.watchdog.server.comm.WebApplicationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Future;

/**
 * 〈一句话功能简述〉<br>
 * 异步长轮询
 *
 * @author donglight
 * @date 2019/4/23
 * @since 1.0.0
 */
@Component
public class AsyncTask {

    /**
     * 异步轮询任务，轮询apps列表和其中的url列表有没有变化，有则返回新的app列表
     *
     * @param webApplicationManager webApplicationManager
     * @return Future Future
     * @throws InterruptedException InterruptedException
     */
    @Async
    public Future<Response> pollingForApps(WebApplicationManager webApplicationManager) throws InterruptedException {
        while (true) {
            if (PollingState.isApplicationsChanged()) {
                PollingState.setApplicationsChanged(false);
                break;
            }
            Thread.sleep(1000);
        }
        return new AsyncResult<>(Response.ok(webApplicationManager.getAllApplications()));
    }

    /**
     * 异步轮询任务，轮询某条URL状态有没有变化，有则返回新的url
     *
     * @param webApplicationManager
     * @return
     * @throws InterruptedException
     */
    @Async
    public Future<Response<List<URLInfo>>> pollingForUrl(WebApplicationManager webApplicationManager, String appName, String instanceId) throws InterruptedException {

        while (true) {
            if (PollingState.isURLInfosChanged()) {
                PollingState.setURLInfosChanged(false);
                break;
            }
            Thread.sleep(1000);
        }
        return new AsyncResult<>(Response.ok(webApplicationManager.getInstance(appName, instanceId).getUrlInfoList()));
    }

}
