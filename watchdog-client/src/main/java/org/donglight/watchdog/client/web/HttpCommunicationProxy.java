package org.donglight.watchdog.client.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.donglight.watchdog.common.domain.URLInfo;
import org.donglight.watchdog.common.domain.URLState;
import org.donglight.watchdog.common.dto.RegistrationDTO;
import org.donglight.watchdog.common.util.HttpClientUtil;
import org.donglight.watchdog.common.util.Response;
import org.donglight.watchdog.common.util.ResponseEnum;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 访问监控服务端的http代理
 *
 * @author donglight
 * @date 2019/4/20
 * @since 1.0.0
 */
public class HttpCommunicationProxy implements CommunicationProxy {

    /**
     * 默认监控服务端的rest url是http://127.0.0.1:6666/api
     */
    private static final String DEFAULT_SERVER_URL = "http://127.0.0.1:6666/api";

    /**
     * 默认本地服务器启动的监听端口是8080
     */
    private static final int DEFAULT_LOCAL_SERVER_PORT = 8080;

    private String serverUrl = DEFAULT_SERVER_URL;

    /**
     * 如果一个应用在同一个主机ip下不同端口启动多个实例，localServerPort起到区别实例的作用
     */
    private int localServerPort = DEFAULT_LOCAL_SERVER_PORT;

    /**
     * 序列化工具
     */
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 服务端返回的token
     */
    private String token;

    private String appId;

    /**
     * 保存已经受监控的URL列表
     */
    private Map<String, URLInfo> watchingURLInfos = new ConcurrentHashMap<>();

    /**
     * 不记录请求静态资源的URL
     */
    String[] excludeUrls = {".js", ".css", ".ico", ".jpg", ".png", ".gif", ".jsp", ".html", ".xml", "/json", ".txt", ".swf", ".zip"};

    /**
     * 应用名称,默认是系统的用户名
     */
    private String appName = System.getProperty("user.name");

    public HttpCommunicationProxy() {
    }

    public HttpCommunicationProxy(String appName) {
        this(appName, DEFAULT_SERVER_URL, DEFAULT_LOCAL_SERVER_PORT);
    }

    public HttpCommunicationProxy(String appName, int localServerPort) {
        this(appName, DEFAULT_SERVER_URL, localServerPort);
    }

    public HttpCommunicationProxy(String appName, String serverUrl, int localServerPort) {
        this.appName = appName;
        this.serverUrl = serverUrl;
        this.localServerPort = localServerPort;
    }

    @Override
    public boolean remoteRegister() {
        String registerPath = "/register";
        try {
            Map<String, Object> map = new HashMap<>(1);
            map.put("appName", appName);
            map.put("port", localServerPort);
            map.put("token", token);
            String json = HttpClientUtil.doPostJson(serverUrl + registerPath, objectMapper.writeValueAsString(map));
            if (json != null) {
                Response<RegistrationDTO> response = objectMapper.readValue(json, new TypeReference<Response<RegistrationDTO>>() {
                });
                if (response.getCode() == ResponseEnum.SUCCESS.getCode()) {
                    RegistrationDTO registrationDTO = response.getData();
                    token = registrationDTO.getToken();
                    appId = registrationDTO.getAppId();
                    return !StringUtils.isEmpty(token);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void pushUrlsToWatchServer(List<URLInfo> urlInfos) {
        for (URLInfo urlInfo : urlInfos) {
            watchingURLInfos.put(urlInfo.getUrl(),urlInfo);
        }
        String remotePath = "/application/" + appId + "/urls";
        try {
            HttpClientUtil.doPostJson(serverUrl + remotePath, objectMapper.writeValueAsString(urlInfos));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recordUrlState(URLState urlState) {
        String remotePath = "/application/" + appId + "/url/" + urlState.getUrlId();
        try {
            HttpClientUtil.doPostJson(serverUrl + remotePath, objectMapper.writeValueAsString(urlState));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pushOneUrl(URLInfo urlInfo) {
        String remotePath = "/application/" + appId + "/url";
        try {
            HttpClientUtil.doPostJson(serverUrl + remotePath, objectMapper.writeValueAsString(urlInfo));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, URLInfo> getWatchingURLInfos() {
        return this.watchingURLInfos;
    }

    @Override
    public boolean isFilterExcludeUrl(String url) {
        for (String excludeUrl : excludeUrls) {
            if (url.endsWith(excludeUrl)) {
                return true;
            }
        }
        return false;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void setLocalServerPort(int localServerPort) {
        this.localServerPort = localServerPort;
    }
}
