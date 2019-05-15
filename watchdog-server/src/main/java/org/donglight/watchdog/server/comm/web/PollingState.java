package org.donglight.watchdog.server.comm.web;

import org.donglight.watchdog.common.bean.URLState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 长轮询,用来判断数据是否改变的状态
 *
 * @author donglight
 * @date 2019/4/23
 * @since 1.0.0
 */
public class PollingState {

    private static volatile boolean applicationsChanged = false;
    private static volatile boolean URLInfosChanged = false;
    /**
     * key urlId(唯一), value最近改变的列表
     */
    private static Map<Long, List<URLState>> recentChangedURLStates = new ConcurrentHashMap<>();

    private static final int MAX_URL_STATE = 64;


    public static boolean isApplicationsChanged() {
        return applicationsChanged;
    }

    public static void setApplicationsChanged(boolean applicationsChanged) {
        PollingState.applicationsChanged = applicationsChanged;
    }

    public static boolean isURLInfosChanged() {
        return URLInfosChanged;
    }

    public static void setURLInfosChanged(boolean URLInfosChanged) {
        PollingState.URLInfosChanged = URLInfosChanged;
    }

    public static List<URLState> getLatestChangedURLStates(long urlId) {
        recentChangedURLStates.computeIfAbsent(urlId, k -> new ArrayList<>());
        return recentChangedURLStates.get(urlId);
    }

    public static void addChangedURLState(URLState urlState) {
        List<URLState> urlStates = recentChangedURLStates.get(urlState.getUrlId());
        if(urlStates == null){
            urlStates = new ArrayList<>();
        }
        if (urlStates.size() >= MAX_URL_STATE) {
            urlStates.remove(0);
        }
        urlStates.add(urlState);
    }
}
