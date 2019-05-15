package org.donglight.watchdog.server.timer;

import org.donglight.watchdog.server.comm.WebApplicationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 检查所有注册的客户端的状态
 *
 * @author donglight
 * @date 2019/4/22
 * @since 1.0.0
 */
@Component
public class CheckApplicationStateTimer {

    private final WebApplicationManager webApplicationManager;

    @Autowired
    public CheckApplicationStateTimer(WebApplicationManager webApplicationManager) {
        this.webApplicationManager = webApplicationManager;
    }


    @Scheduled(fixedDelayString = "${watchdog.server.evictionIntervalTimerInMs}")
    private void checkState() {
        webApplicationManager.checkAndUnregisterApplication();
    }
}
