package org.donglight.watchdog.server.persistence;

import org.donglight.watchdog.common.bean.InstanceInfo;
import org.donglight.watchdog.common.bean.WebApplication;

import java.util.List;

/**
 * watchdog访问数据库对象
 *
 * @author donglight
 * @date 2019/5/13
 * @since 1.0.0
 */
public interface WatchdogDao {


    /**
     * 根据appName查询应用
     *
     * @param appName appName
     * @return WebApplication
     */
    WebApplication getApplicationByAppName(String appName);


    /**
     * 根据appName查找实例列表
     *
     * @param appName appName
     * @return List<InstanceInfo>
     */
    List<InstanceInfo> getInstanceInfosByAppName(String appName);

    /**
     * 保存application信息
     *
     * @param webApplication webApplication
     * @return boolean
     */
    boolean saveApplication(WebApplication webApplication);

    /**
     * 保存实例列表
     *
     * @param instanceInfos instanceInfos
     * @return boolean
     */
    boolean saveInstance(List<InstanceInfo> instanceInfos);


    /**
     * 更新应用信息
     *
     * @param webApplication webApplication
     * @return boolean
     */
    boolean updateApplication(WebApplication webApplication);

    /**
     * 更新应用实例列表信息
     *
     * @param instanceInfos instanceInfos
     * @return boolean
     */
    boolean updateInstances(List<InstanceInfo> instanceInfos);

    /**
     * 删除应用信息
     *
     * @param appName appName
     * @return boolean
     */
    boolean removeApplicationByAppName(String appName);

    /**
     * 删除应用实例信息
     *
     * @param instanceId instanceId
     * @return boolean
     */
    boolean removeInstanceByInstanceId(String instanceId);


}
