package org.donglight.watchdog.server.web;

import lombok.extern.slf4j.Slf4j;
import org.donglight.watchdog.common.domain.WebApplication;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 外观者类
 *
 * @author donglight
 * @date 2019/4/18
 * @since 1.0.0
 */
public class WebApplicationManagerFacade implements WebApplicationManager{

    private final WebApplicationManager webApplicationManager;

    @Autowired
    public WebApplicationManagerFacade(WebApplicationManager webApplicationManager) {
        this.webApplicationManager = webApplicationManager;
    }

    @Override
    public boolean registerApplication(WebApplication application) {
        try {
            return webApplicationManager.registerApplication(application);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean unregisterApplication(Long appId) {
        return webApplicationManager.unregisterApplication(appId);
    }

    @Override
    public boolean renewApplication(WebApplication application) {
        return false;
    }

    @Override
    public int getApplicationCount() {
        return webApplicationManager.getApplicationCount();
    }

    @Override
    public List<WebApplication> getAllApplications() {
        return webApplicationManager.getAllApplications();
    }
}
