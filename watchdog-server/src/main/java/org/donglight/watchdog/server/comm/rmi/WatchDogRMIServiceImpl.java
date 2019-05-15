package org.donglight.watchdog.server.comm.rmi;

import lombok.extern.slf4j.Slf4j;
import org.donglight.watchdog.common.bean.Metrics;
import org.donglight.watchdog.common.bean.URLInfo;
import org.donglight.watchdog.common.bean.URLState;
import org.donglight.watchdog.common.bean.WatchDogClientConfigBean;
import org.donglight.watchdog.common.util.Response;
import org.donglight.watchdog.server.comm.AbstractWatchDogCommunicationService;
import org.donglight.watchdog.server.comm.WebApplicationManager;
import org.donglight.watchdog.server.config.WatchDogServerConfigBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * WatchDogRMIServiceImpl
 *
 * @author donglight
 * @date 2019/5/4
 * @since 1.0.0
 */
@Service
@Slf4j
public class WatchDogRMIServiceImpl extends AbstractWatchDogCommunicationService {


    @Autowired
    public WatchDogRMIServiceImpl(WebApplicationManager webApplicationManager) throws RemoteException {
        super(webApplicationManager);
        UnicastRemoteObject.exportObject(this, 0);
    }

    @PostConstruct
    public void initRMI() throws Exception {
        WatchDogServerConfigBean serverConfig = webApplicationManager.getServerConfig();
        LocateRegistry.createRegistry(serverConfig.getRmiPort());
        Naming.bind(serverConfig.getRmiName(), this);
    }


    @Override
    public Response registerApplication(WatchDogClientConfigBean watchDogClientConfigBean) {
        return super.registerApplication(watchDogClientConfigBean);
    }

    @Override
    public String renewApplication(String appName, String instanceId, String ping) {
        return super.renewApplication(appName, instanceId, ping);
    }

    @Override
    public Response setUrls(String appName, String instanceId, List<URLInfo> urlInfoList) {
        return super.setUrls(appName, instanceId, urlInfoList);
    }


    @Override
    public Response addNewURLInfo(String appName, String instanceId, URLInfo urlInfo) {
        return super.addNewURLInfo(appName, instanceId, urlInfo);
    }


    @Override
    public Response addURLState(String appName, String instanceId, URLState urlState) {
        return super.addURLState(appName, instanceId, urlState);
    }


    @Override
    public Response setMetrics(String appName, String instanceId, Metrics metrics) {
        return super.setMetrics(appName, instanceId, metrics);
    }

}
