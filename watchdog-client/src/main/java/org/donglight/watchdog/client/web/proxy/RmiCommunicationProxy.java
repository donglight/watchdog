package org.donglight.watchdog.client.web.proxy;

import lombok.extern.slf4j.Slf4j;
import org.donglight.watchdog.common.bean.*;
import org.donglight.watchdog.common.rmi.WatchDogCommunicationService;
import org.donglight.watchdog.common.util.Response;
import org.donglight.watchdog.common.util.ResponseEnum;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * CommunicationProxy RMI实现
 *
 * @author donglight
 * @date 2019/5/4
 * @since 1.0.0
 */
@Slf4j
public class RmiCommunicationProxy extends AbstractCommunicationProxy {

    private WatchDogCommunicationService watchDogRMIService;

    public RmiCommunicationProxy() {
        super();
    }

    public RmiCommunicationProxy(String appName) {
        super(appName);
    }

    public RmiCommunicationProxy(String appName, String serverUrl) {
        super(appName, serverUrl);
    }

    private void lookupRmiService() {
        try {
            watchDogRMIService = (WatchDogCommunicationService) Naming.lookup(serverUrl);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean remoteRegister(WatchDogClientConfigBean configBean) {
        if (watchDogRMIService == null) {
            lookupRmiService();
        }
        watchDogClientConfigBean = configBean;
        if ("".equals(watchDogClientConfigBean.getAppName().trim())) {
            watchDogClientConfigBean.setAppName(appName);
        } else {
            appName = watchDogClientConfigBean.getAppName();
        }
        try {
            Response response = watchDogRMIService.registerApplication(watchDogClientConfigBean);
            if (response.getCode() == ResponseEnum.SUCCESS.getCode()) {
                instanceInfo = (InstanceInfo) response.getData();
                // 开启当前应用实例的续约任务
                if (instanceInfo == null) {
                    log.warn("fail register to remote watch dog server,please check client name,remote server address,config file and so on.");
                    return false;
                }
                log.info("successful register to remote server");
                retryTime = watchDogClientConfigBean.getRetryTime();
                // 开启定时任务
                startTimerTask();
                return true;
            } else {
                log.warn("fail register to remote watch dog server,please check client name,remote server address,config file and so on.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("fail register to remote watch dog server,please check client name,remote server address,config file and so on.");
            return false;
        }
    }

    @Override
    public boolean renew() {
        String response = null;
        try {
            response = watchDogRMIService.renewApplication(appName, instanceInfo.getInstanceId(), PING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (PONG.equals(response)) {
            log.info("successful renew instance");
            return true;
        } else {
            // 心跳检测失败，可能是服务端挂了,尝试重新注册
            log.warn("fail renew instance,try to re-register again");
            lookupRmiService();
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
        try {
            watchDogRMIService.setUrls(appName, instanceInfo.getInstanceId(), urlInfos);
        } catch (Exception e) {
            log.warn("fail push url to watchdog server");
            e.printStackTrace();
        }
    }

    @Override
    public void pushUrlState(URLState urlState) {
        try {
            watchDogRMIService.addURLState(appName, instanceInfo.getInstanceId(), urlState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pushOneUrl(URLInfo urlInfo) {
        try {
            watchDogRMIService.addNewURLInfo(appName, instanceInfo.getInstanceId(), urlInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean pushMetrics(Metrics metrics) {
        if (retryTime > 0) {
            try {
                Response response = watchDogRMIService.setMetrics(appName, instanceInfo.getInstanceId(), metrics);
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
