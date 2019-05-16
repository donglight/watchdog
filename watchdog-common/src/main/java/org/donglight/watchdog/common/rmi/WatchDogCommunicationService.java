package org.donglight.watchdog.common.rmi;

import org.donglight.watchdog.common.bean.Metrics;
import org.donglight.watchdog.common.bean.URLInfo;
import org.donglight.watchdog.common.bean.URLState;
import org.donglight.watchdog.common.bean.WatchDogClientConfigBean;
import org.donglight.watchdog.common.util.Response;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * WatchDogCommunicationService
 *
 * @author donglight
 * @date 2019/5/4
 * @since 1.0.0
 */
public interface WatchDogCommunicationService extends Remote {

    /**
     * 注册需要被监控的应用
     *
     * @param watchDogClientConfigBean 应用信息
     * @return Response
     * @throws RemoteException RemoteException
     */
    Response registerApplication(WatchDogClientConfigBean watchDogClientConfigBean) throws RemoteException;

    /**
     * 应用实例续约
     *
     * @param appName    appName
     * @param instanceId 实例ID
     * @param ping       "ping"
     * @return String "pong"
     * @throws RemoteException RemoteException
     */
    String renewApplication(String appName, String instanceId, String ping) throws RemoteException;

    /**
     * 接收客户端的urlInfoList，设置到appName对应的application中
     *
     * @param appName     appName
     * @param instanceId  instanceId
     * @param urlInfoList urlInfoList
     * @return Response
     * @throws RemoteException RemoteException
     */
    Response setUrls(String appName, String instanceId, List<URLInfo> urlInfoList) throws RemoteException;

    /**
     * 接收urlInfo，添加到对相应的application的instance中
     *
     * @param appName    appName
     * @param instanceId instanceId
     * @param urlInfo    urlInfo
     * @return Response
     * @throws RemoteException RemoteException
     */
    Response addNewURLInfo(String appName, String instanceId, URLInfo urlInfo) throws RemoteException;

    /**
     * 接收URLState，添加到对应的URLInfo中
     *
     * @param appName    appName
     * @param instanceId 实例id
     * @param urlState   url状态
     * @return Response
     * @throws RemoteException RemoteException
     */
    Response addURLState(String appName, String instanceId, URLState urlState) throws RemoteException;

    /**
     * 接收metrics，添加到对相应的application的instance中
     *
     * @param appName    appName
     * @param instanceId instanceId
     * @param metrics    metrics
     * @return Response
     * @throws RemoteException RemoteException
     */
    Response saveMetrics(String appName, String instanceId, Metrics metrics) throws RemoteException;
}
