package org.donglight.watchdog.common.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 〈一句话功能简述〉<br>
 * Web应用类
 *
 * @author donglight
 * @date 2019/4/17
 * @since 1.0.0
 */
@Getter
@Setter
public final class WebApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用名，唯一Id
     */
    private String appName;

    /**
     * 一个应用下有多个实例(InstanceInfo)
     */
    private Map<String, InstanceInfo> instanceInfos;

    /**
     * app注册时间
     */
    private Long timestamp;

    private AtomicLong appTotalAccessCount = new AtomicLong(0);

    public WebApplication(String appName) {
        this.appName = appName;
        this.instanceInfos = new ConcurrentHashMap<>();
        this.timestamp = System.currentTimeMillis();
    }

    public WebApplication(String appName, long appAccessTotalCount, long timestamp) {
        this.appName = appName;
        this.appTotalAccessCount.set(appAccessTotalCount);
        this.timestamp = timestamp;
    }

    public InstanceInfo addInstanceInfo(WatchDogClientConfigBean watchDogClientConfigBean) {


        LeaseInfo.LeaseInfoBuilder builder = LeaseInfo.builder();
        long now = System.currentTimeMillis();
        LeaseInfo leaseInfo = builder.durationInSecs(watchDogClientConfigBean.getDurationInSecs())
                .renewalIntervalInSecs(watchDogClientConfigBean.getRenewalIntervalInSecs())
                .registrationTimestamp(now)
                .lastRenewalTimestamp(now)
                .serviceUpTimestamp(now)
                .build();
        String instanceId = watchDogClientConfigBean.getInstanceId();
        InstanceInfo instanceInfo = InstanceInfo.builder()
                .instanceId(instanceId)
                .appName(appName)
                .serverInfo(watchDogClientConfigBean.getServerInfo())
                .ipAddr(watchDogClientConfigBean.getIpAddr())
                .hostName(watchDogClientConfigBean.getHostName())
                .port(watchDogClientConfigBean.getClientWebServerPort())
                .homePageUrl(watchDogClientConfigBean.getHomePageUrl())
                .leaseInfo(leaseInfo)
                .refreshMetricsIntervalInSecs(watchDogClientConfigBean.getRefreshMetricsIntervalInSecs())
                .build();
        return instanceInfos.put(instanceId, instanceInfo) == null ? instanceInfo : null;
    }

    public void updateInstance(InstanceInfo instanceInfo, WatchDogClientConfigBean watchDogClientConfigBean) {
        long now = System.currentTimeMillis();
        instanceInfo.setLastUpdateTimestamp(now);
        instanceInfo.setRefreshMetricsIntervalInSecs(watchDogClientConfigBean.getRefreshMetricsIntervalInSecs());
        instanceInfo.setHomePageUrl(watchDogClientConfigBean.getHomePageUrl());
        instanceInfo.setServerInfo(watchDogClientConfigBean.getServerInfo());
        instanceInfo.setHostName(watchDogClientConfigBean.getHostName());
        LeaseInfo leaseInfo = instanceInfo.getLeaseInfo();
        leaseInfo.setDurationInSecs(watchDogClientConfigBean.getDurationInSecs());
    }
}
