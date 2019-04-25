package org.donglight.watchdog.server.web;

import org.donglight.watchdog.common.util.Response;
import org.donglight.watchdog.server.util.PollingState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

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
public class AsyncPooling {

    @Async
    public Future<Response> pollingForApps(WebApplicationManager webApplicationManager) throws InterruptedException {
        while(true){
            if(PollingState.isChangeApplications()){
                PollingState.setChangeApplications(false);
                break;
            }
            Thread.sleep(1000);
        }
        return new AsyncResult<>(Response.ok(webApplicationManager.getAllApplications()));
    }
}
