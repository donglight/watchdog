package org.donglight.watchdog.client.web.proxy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.donglight.watchdog.common.bean.*;
import org.donglight.watchdog.common.util.HttpClientUtil;
import org.donglight.watchdog.common.util.Response;
import org.donglight.watchdog.common.util.ResponseEnum;

import java.util.List;

/**
 * 访问监控服务端的http代理
 *
 * @author donglight
 * @date 2019/4/20
 * @since 1.0.0
 */
@Slf4j
public class HttpCommunicationProxy extends AbstractCommunicationProxy {
    /**
     * 默认监控服务端的rest url是http://127.0.0.1:6666/api
     */
    private static final String DEFAULT_SERVER_URL = "http://127.0.0.1:10000/api";
    private String serverUrl;
    /**
     * 序列化工具
     */
    private ObjectMapper objectMapper = new ObjectMapper();

    private String appNameAndInstanceIdPath;


    public HttpCommunicationProxy() {
        super();
    }

    public HttpCommunicationProxy(String appName) {
        super(appName, DEFAULT_SERVER_URL);
    }

    public HttpCommunicationProxy(String appName, String serverUrl) {
        super(appName,serverUrl);
    }

    @Override
    public boolean remoteRegister(WatchDogClientConfigBean clientConfigBean) {
        this.watchDogClientConfigBean = clientConfigBean;
        try {
            String registerPath = "/register";
            String json = HttpClientUtil.doPostJson(serverUrl + registerPath, objectMapper.writeValueAsString(watchDogClientConfigBean));
            if (json != null) {
                Response<InstanceInfo> response = objectMapper.readValue(json, new TypeReference<Response<InstanceInfo>>() {
                });
                if (response.getCode() == ResponseEnum.SUCCESS.getCode()) {
                    instanceInfo = response.getData();
                    // 开启当前应用实例的续约任务
                    if (instanceInfo == null) {
                        log.warn("fail register to watch dog remote server,please check client name,remote server address,config file and so on.");
                        return false;
                    }
                    log.info("successful register to remote server");
                    appNameAndInstanceIdPath = "/" + appName + "/" + instanceInfo.getInstanceId();
                    retryTime = watchDogClientConfigBean.getRetryTime();
                    // 开启定时任务
                    startTimerTask();
                    return true;
                } else {
                    log.warn("fail register to watch dog remote server,please check client name,remote server address,config file and so on.");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("fail register to remote watch dog server,please check client name,remote server address,config file and so on.");
            return false;
        }
        return false;
    }

    @Override
    public boolean renew() {
        String renewPath = appNameAndInstanceIdPath + "/renew";
        String responseJson = HttpClientUtil.doPostJson(serverUrl + renewPath, PING);
        if (PONG.equals(responseJson)) {
            log.info("successful renew instance");
            return true;
        } else {
            // 心跳检测失败，可能是服务端挂了,尝试重新注册
            log.warn("fail renew instance,try to re-register again");
            boolean b = remoteRegister(watchDogClientConfigBean);
            if (b) {
                pushUrls(watchingURLInfos);
            }
            return b;
        }
    }

    @Override
    public void pushUrls(List<URLInfo> urlInfos) {
        watchingURLInfos = urlInfos;
        String remotePath = appNameAndInstanceIdPath + "/urls";
        try {
            HttpClientUtil.doPostJson(serverUrl + remotePath, objectMapper.writeValueAsString(urlInfos));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pushUrlState(URLState urlState) {
        try {
            String remotePath = appNameAndInstanceIdPath + "/urlState";
            HttpClientUtil.doPostJson(serverUrl + remotePath, objectMapper.writeValueAsString(urlState));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pushOneUrl(URLInfo urlInfo) {
        try {
            String remotePath = appNameAndInstanceIdPath + "/url";
            HttpClientUtil.doPostJson(serverUrl + remotePath, objectMapper.writeValueAsString(urlInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean pushMetrics(Metrics metrics) {
        if (retryTime > 0) {
            try {
                String respJson = HttpClientUtil.doPostJson(serverUrl + appNameAndInstanceIdPath + "/metrics", objectMapper.writeValueAsString(metricsCollector.getMetrics()));
                Response response = objectMapper.readValue(respJson, Response.class);
                if ("ok".equals(response.getData())) {
                    return true;
                } else {
                    retryTime--;
                }
            } catch (Exception e) {
                e.printStackTrace();
                retryTime--;
                return false;
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


}
