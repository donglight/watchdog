package org.donglight.watchdog.server.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.donglight.watchdog.common.bean.InstanceInfo;
import org.donglight.watchdog.common.bean.WebApplication;

import java.util.List;

/**
 * TODO 尚未完善
 * 持久化为json
 *
 * @author donglight
 * @date 2019/5/13
 * @since 1.0.0
 */
public class JsonWatchdogDaoImpl implements WatchdogDao {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public WebApplication getApplicationByAppName(String appName) {
        return null;
    }

    @Override
    public List<InstanceInfo> getInstanceInfosByAppName(String appName) {
        return null;
    }

    @Override
    public boolean saveApplication(WebApplication webApplication) {
        return false;
    }

    @Override
    public boolean saveInstance(List<InstanceInfo> instanceInfos) {
        return false;
    }

    @Override
    public boolean updateApplication(WebApplication webApplication) {
        return false;
    }

    @Override
    public boolean updateInstances(List<InstanceInfo> instanceInfos) {
        return false;
    }

    @Override
    public boolean removeApplicationByAppName(String appName) {
        return false;
    }

    @Override
    public boolean removeInstanceByInstanceId(String instanceId) {
        return false;
    }
}
