package org.donglight.watchdog.server.comm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.donglight.watchdog.common.bean.*;
import org.donglight.watchdog.server.comm.web.PollingState;
import org.donglight.watchdog.server.config.WatchDogServerConfigBean;
import org.donglight.watchdog.server.persistence.WatchdogDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DefaultWebApplicationManager
 *
 * @author donglight
 * @date 2019/4/17
 * @since 1.0.0
 */
@Slf4j
public class DefaultWebApplicationManager implements WebApplicationManager {

    private final Map<String, WebApplication> applications = new ConcurrentHashMap<>();


    private final WatchDogServerConfigBean watchDogServerConfigBean;

    private static final int MIN_INTERVAL = 1;

    @Autowired(required = false)
    private WatchdogDao watchdogDao;

    @Autowired
    public DefaultWebApplicationManager(WatchDogServerConfigBean watchDogServerConfigBean) {
        this.watchDogServerConfigBean = watchDogServerConfigBean;
    }

    @Override
    public Optional<InstanceInfo> registerApplication(WatchDogClientConfigBean watchDogClientConfigBean) {

        String appName = watchDogClientConfigBean.getAppName();
        String instanceId = watchDogClientConfigBean.getInstanceId();
        if (StringUtils.isEmpty(appName) || StringUtils.isEmpty(instanceId)) {
            throw new NullPointerException("application name and instanceId could not be empty or null");
        }
        if (watchDogClientConfigBean.getRefreshMetricsIntervalInSecs() < MIN_INTERVAL) {
            watchDogClientConfigBean.setRefreshMetricsIntervalInSecs(MIN_INTERVAL);
        }
        if (watchDogClientConfigBean.getRenewalIntervalInSecs() < MIN_INTERVAL) {
            watchDogClientConfigBean.setRenewalIntervalInSecs(MIN_INTERVAL);
        }
        if (watchDogClientConfigBean.getRenewalIntervalInSecs() < watchDogClientConfigBean.getDurationInSecs()) {
            watchDogClientConfigBean.setRenewalIntervalInSecs(watchDogClientConfigBean.getDurationInSecs());
        }
        if (applications.containsKey(appName)) {
            // 应用已经注册过
            WebApplication webApplication = applications.get(appName);

            Map<String, InstanceInfo> instanceInfoMap = webApplication.getInstanceInfos();
            if (instanceInfoMap.containsKey(instanceId)) {
                // 这个应用实例已经注册,更新配置文件
                InstanceInfo instanceInfo = instanceInfoMap.get(instanceId);
                webApplication.updateInstance(instanceInfo, watchDogClientConfigBean);
                return Optional.of(instanceInfo);
            } else {
                // 这个应用实例没有注册，创建实例
                return Optional.ofNullable(webApplication.addInstanceInfo(watchDogClientConfigBean));
            }
        }
        // 应用第一次注册
        WebApplication webApplication
                = new WebApplication(appName);
        // 是否开启持久化
        if (watchDogServerConfigBean.isPersistenceEnabled()) {

            // 是否之前持久化过(file,database...)
            WebApplication applicationFromStore = watchdogDao.getApplicationByAppName(appName);
            if (applicationFromStore != null) {
                webApplication.setAppTotalAccessCount(applicationFromStore.getAppTotalAccessCount());
                webApplication.setTimestamp(applicationFromStore.getTimestamp());
            } else {
                watchdogDao.saveApplication(webApplication);
            }
        }
        applications.put(appName, webApplication);
        InstanceInfo instanceInfo = webApplication.addInstanceInfo(watchDogClientConfigBean);
        if (instanceInfo != null) {
            log.info("successful registry instance '{}' of app '{}'", instanceId, appName);
        } else {
            log.info("fail registry instance '{}' of app '{}'", instanceId, appName);
        }
        return Optional.ofNullable(instanceInfo);
    }

    @Override
    public void checkAndUnregisterApplication() {
        Iterator<WebApplication> iteratorOfApps = applications.values().iterator();
        long now = System.currentTimeMillis();
        while (iteratorOfApps.hasNext()) {
            WebApplication app = iteratorOfApps.next();
            Map<String, InstanceInfo> instanceInfoMap = app.getInstanceInfos();
            Iterator<InstanceInfo> iteratorOfInstance = instanceInfoMap.values().iterator();
            while (iteratorOfInstance.hasNext()) {
                InstanceInfo instanceInfo = iteratorOfInstance.next();
                LeaseInfo leaseInfo = instanceInfo.getLeaseInfo();
                if (InstanceInfo.InstanceStatus.UP == instanceInfo.getStatus()) {

                    if (now - instanceInfo.getLastUpdateTimestamp() > leaseInfo.getDurationInSecs() * 1000) {
                        // 标记为过期失效的应用
                        instanceInfo.setStatus(InstanceInfo.InstanceStatus.DOWN);
                        // 改变轮询状态
                        PollingState.setApplicationsChanged(true);
                        log.info("instance '{}' of application '{}' expire", instanceInfo.getInstanceId(), app.getAppName());
                    }
                }
                if (now - instanceInfo.getLastUpdateTimestamp()
                        > leaseInfo.getDurationInSecs() * 1000 + watchDogServerConfigBean.getLeaseExpirationRemoveDurationInSeconds() * 1000) {
                    // 服务失效后，经过watchDogServerConfigBean.leaseExpirationRemoveDuration秒，如果客户端还没有续约,即还是DOWN(过期/失效)状态,再剔除
                    if (InstanceInfo.InstanceStatus.DOWN == instanceInfo.getStatus()) {
                        iteratorOfInstance.remove();
                        if (instanceInfoMap.size() == 0) {
                            iteratorOfApps.remove();
                            // 是否开启持久化
                            if (watchDogServerConfigBean.isPersistenceEnabled()) {
                                // 根据持久化策略持久化应用的一些必要信息
                                if (watchdogDao.updateApplication(app)) {
                                    log.info("successfully update application '{}' to store ", app.getAppName());
                                }
                            }
                        }
                        PollingState.setApplicationsChanged(true);
                        log.info("remove instance '{}' of application '{}' ", instanceInfo.getInstanceId(), app.getAppName());
                    }
                }
            }
        }
    }

    @Override
    public boolean renewApplication(String appName, String instanceId) {
        if (applications.containsKey(appName)) {
            WebApplication webApplication = applications.get(appName);
            Map<String, InstanceInfo> instanceInfoMap = webApplication.getInstanceInfos();
            if (instanceInfoMap.containsKey(instanceId)) {
                long now = System.currentTimeMillis();
                InstanceInfo instanceInfo = instanceInfoMap.get(instanceId);
                instanceInfo.setLastUpdateTimestamp(now);
                instanceInfo.setStatus(InstanceInfo.InstanceStatus.UP);
                LeaseInfo leaseInfo = instanceInfo.getLeaseInfo();
                leaseInfo.setLastRenewalTimestamp(now);
                leaseInfo.setServiceUpTimestamp(now);

                PollingState.setApplicationsChanged(true);
                return true;
            }
        }
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
    public Map<String, WebApplication> getAllApplicationsMap() {
        return applications;
    }

    @Override
    public WebApplication getApplication(String appName) {
        return applications.get(appName);
    }

    @Override
    public InstanceInfo getInstance(String appName, String instanceId) {
        if (applications.containsKey(appName)) {
            return applications.get(appName).getInstanceInfos().get(instanceId);
        }
        return null;
    }

    @Override
    public WatchDogServerConfigBean getServerConfig() {
        return watchDogServerConfigBean;
    }

    @Override
    public void saveMetrics(String appName, String instanceId, Metrics metrics) {
        InstanceInfo instance = getInstance(appName, instanceId);
        if (instance != null) {
            instance.setMetrics(metrics);
        }
    }

}
