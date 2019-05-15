package org.donglight.watchdog.server.persistence;

import org.donglight.watchdog.common.bean.InstanceInfo;
import org.donglight.watchdog.common.bean.WebApplication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * TODO 尚未完善
 * 用数据库持久化
 *
 * @author donglight
 * @date 2019/5/13
 * @since 1.0.0
 */
public class DatabaseWatchdogImpl implements WatchdogDao {

    private final JdbcUtil jdbcUtil;

    public DatabaseWatchdogImpl(JdbcUtil jdbcUtil) {
        this.jdbcUtil = jdbcUtil;
    }


    @Override
    public WebApplication getApplicationByAppName(String appName) {
        WebApplication webApplication = null;
        try {
            String sql = "select * from wd_app where app_name = ?";
            ResultSet resultSet = jdbcUtil.executeQuery(sql, appName);
            if (resultSet.next()) {
                long appAccessCount = resultSet.getLong("app_access_count");
                long timestamp = resultSet.getLong("timestamp");
                webApplication = new WebApplication(appName, appAccessCount, timestamp);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return webApplication;
    }

    @Override
    public List<InstanceInfo> getInstanceInfosByAppName(String appName) {
        return null;
    }

    @Override
    public boolean saveApplication(WebApplication webApplication) {
        try {
            String sql = "insert into wd_app (app_name,app_access_count,timestamp) values (?,?,?)";
            int row = jdbcUtil.executeUpdate(sql, webApplication.getAppName(), webApplication.getAppTotalAccessCount().get(), webApplication.getTimestamp());
            if (row > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean saveInstance(List<InstanceInfo> instanceInfos) {
        return false;
    }

    @Override
    public boolean updateApplication(WebApplication webApplication) {
        String sql = "update wd_app set app_access_count = ?, timestamp = ? where app_name = ?";
        try {
            int row = jdbcUtil.executeUpdate(sql, webApplication.getAppTotalAccessCount().get(), webApplication.getTimestamp(), webApplication.getAppName());
            if (row > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateInstances(List<InstanceInfo> instanceInfos) {
        return false;
    }

    @Override
    public boolean removeApplicationByAppName(String appName) {
        String sql = "delete from wd_app where app_name = ?";
        try {
            int row = jdbcUtil.executeUpdate(sql, appName);
            if (row > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeInstanceByInstanceId(String instanceId) {
        return false;
    }
}
