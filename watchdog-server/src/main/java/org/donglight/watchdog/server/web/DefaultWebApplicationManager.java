package org.donglight.watchdog.server.web;

import lombok.extern.slf4j.Slf4j;
import org.donglight.watchdog.common.domain.WebApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 〈一句话功能简述〉<br>
 * 功能
 *
 * @author donglight
 * @date 2019/4/17
 * @since 1.0.0
 */
@Slf4j
public class DefaultWebApplicationManager implements WebApplicationManager {

    private final Map<Long,WebApplication> applications = new ConcurrentHashMap<>();

    @Override
    public boolean registerApplication(WebApplication application) throws Exception {
        if(application == null){
            throw new NullPointerException("application could not be null");
        }
        log.info("application {} register ",application);
        return applications.put(application.getAppId(),application) != null;
    }

    @Override
    public boolean unregisterApplication(Long appId) {
        return applications.remove(appId) != null;
    }

    @Override
    public boolean renewApplication(WebApplication application) {
        return false;
    }

    @Override
    public int getApplicationCount() {
        return applications.size();
    }

    @Override
    public List<WebApplication> getAllApplications() {
        return new ArrayList<>(applications.values());
    }
}
