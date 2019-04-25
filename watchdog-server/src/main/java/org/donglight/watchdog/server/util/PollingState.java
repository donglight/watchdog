package org.donglight.watchdog.server.util;

/**
 * 长轮询用来判断数据是否改变的状态位
 *
 * @author donglight
 * @date 2019/4/23
 * @since 1.0.0
 */
public class PollingState {

    private static volatile boolean changeApplications = false;
    private static volatile boolean changeURLStates = false;

    public static boolean isChangeApplications() {
        return changeApplications;
    }

    public static void setChangeApplications(boolean changeApplications) {
        PollingState.changeApplications = changeApplications;
    }

    public static boolean isChangeURLStates() {
        return changeURLStates;
    }

    public static void setChangeURLStates(boolean changeURLStates) {
        PollingState.changeURLStates = changeURLStates;
    }
}
