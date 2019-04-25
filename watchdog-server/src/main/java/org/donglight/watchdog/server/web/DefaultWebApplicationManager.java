package org.donglight.watchdog.server.web;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.donglight.watchdog.common.domain.WebApplication;
import org.donglight.watchdog.server.util.PollingState;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.*;
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

    private final Map<String, WebApplication> applications = new ConcurrentHashMap<>();

    @Override
    public Optional<WebApplication> registerApplication(Map<String, String> fromClient) {
        String token = fromClient.get("token");
        String appName = fromClient.get("appName");
        String addr = fromClient.get("addr");
        String port = fromClient.get("port");
        if (StringUtils.isEmpty(appName)) {
            throw new NullPointerException("application name could not be empty or null");
        }
        String appId = appName + "-" + DigestUtils.md5DigestAsHex((addr + ":" + port).getBytes()).substring(20);
        if (applications.containsKey(appId)) {
            //已经注册过了,直接返回token
            return Optional.of(applications.get(appId));
        }
        if (StringUtils.isEmpty(token)) {
            // 第一次注册,生成客户端验证的token
            token = UUID.randomUUID().toString();
            WebApplication webapplication = new WebApplication(appId, appName, addr, port, token, new ArrayList<>());
            applications.put(appId, webapplication);
            // applications改变了
            log.info("successful client '{} : {}' registry ", appName, appId);
            return Optional.of(webapplication);
        }
        return Optional.empty();
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

    @Override
    public WebApplication getApplicationById(String appId) {
        return applications.get(appId);
    }

}
