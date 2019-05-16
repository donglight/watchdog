package org.donglight.watchdog.server.comm;


import org.donglight.watchdog.common.bean.InstanceInfo;
import org.donglight.watchdog.common.bean.Metrics;
import org.donglight.watchdog.common.bean.WatchDogClientConfigBean;
import org.donglight.watchdog.common.bean.WebApplication;
import org.donglight.watchdog.server.config.WatchDogServerConfigBean;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 〈一句话功能简述〉<br>
 * Web状态管理接口
 * 管理Web应用的所有状态
 *
 * @author donglight
 * @date 2019/4/17
 * @since 1.0.0
 */
public interface WebApplicationManager {


    /**
     * 注册应用
     *
     * @param watchDogClientConfigBean 客户端配置信息
     * @return Optional<InstanceInfo>
     */
    Optional<InstanceInfo> registerApplication(WatchDogClientConfigBean watchDogClientConfigBean);

    /**
     * 当应用长时间没有响应后，剔除应用
     *
     * @return <tt>true</tt> 如果剔除应用成功
     */
    void checkAndUnregisterApplication();

    /**
     * 每隔一段时间应用实例续约
     *
     * @param appName    应用name，唯一ID
     * @param instanceId 实例Id
     * @return <tt>true</tt> 如果应用续约成功
     */
    boolean renewApplication(String appName, String instanceId);

    /**
     * 获得被监控的应用数
     *
     * @return count
     */
    int getApplicationCount();

    /**
     * 获得所有应用列表
     *
     * @return applications
     */
    List<WebApplication> getAllApplications();

    /**
     * 获得所有应用Map
     *
     * @return applications
     */
    Map<String, WebApplication> getAllApplicationsMap();

    /**
     * 获得指定应用
     *
     * @param appName appName
     * @return applications
     */
    WebApplication getApplication(String appName);

    /**
     * 获得指定应用的某个实例
     *
     * @param appName    appName
     * @param instanceId instanceId
     * @return InstanceInfo
     */
    InstanceInfo getInstance(String appName, String instanceId);

    /**
     * 获得服务端配置信息
     *
     * @return WatchDogServerConfigBean
     */
    WatchDogServerConfigBean getServerConfig();

    /**
     * 保存指标信息
     *
     * @return WatchDogServerConfigBean
     * @param appName
     * @param instanceId
     * @param metrics
     */
    void saveMetrics(String appName, String instanceId, Metrics metrics);
}
