package org.donglight.watchdog.common.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.donglight.watchdog.common.util.CommonUtil;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Properties;

/**
 * WatchDog 客户端信息配置类
 *
 * @author donglight
 * @date 2019/4/30
 * @since 1.0.0
 */
@Getter
@Setter
@Slf4j
public class WatchDogClientConfigBean implements Serializable {

    private static final long serialVersionUID = 42L;

    public static final int DEFAULT_LEASE_RENEWAL_INTERVAL = 30;
    public static final int DEFAULT_LEASE_DURATION = 90;

    public static final int DEFAULT_REFRESH_METRICS_INTERVAL_IN_SECS = 5;
    public static final int DEFAULT_RETRY_TIME = 5;
    private static final String CONFIG_FILE_NAME = "watchdog.properties";
    private static final String RENEW_INTERVAL_TIMER_IN_MS = "watchdog.client.renewalIntervalInSecs";
    private static final String DURATION_IN_SECS = "watchdog.client.durationInSecs";
    private static final String HOME_PAGE_URL = "watchdog.client.homePageUrl";
    private static final String REFRESH_METRICS_INTERVAL_IN_SECS = "watchdog.client.refreshMetricsIntervalInSecs";
    private static final String RETRY_TIME = "watchdog.client.retryTime";
    private static final String APP_NAME = "watchdog.client.appName";
    private static final String ENABLED = "watchdog.client.enabled";
    /**
     * 应用续约任务间隔时间
     */
    private int renewalIntervalInSecs = DEFAULT_LEASE_RENEWAL_INTERVAL;
    private int durationInSecs = DEFAULT_LEASE_DURATION;
    private int refreshMetricsIntervalInSecs = DEFAULT_REFRESH_METRICS_INTERVAL_IN_SECS;
    private int retryTime = DEFAULT_RETRY_TIME;
    private boolean enabled = true;
    private String ipAddr;
    private int clientWebServerPort;
    private String appName = "";
    private String hostName;
    private String homePageUrl;
    private String instanceId;
    private String serverInfo;

    public void initWatchDogClientConfigBean(boolean isReadCustomizeProperties) {

        int localWebServerPort = CommonUtil.getLocalWebServerPort();
        this.clientWebServerPort = localWebServerPort;
        InetAddress addr = CommonUtil.getLocalIpv4Address();
        this.ipAddr = addr.getHostAddress();
        this.hostName = addr.getHostName();
        this.instanceId = addr.getHostAddress() + ":" + localWebServerPort;
        // 默认值，如果有在配置文件中配置如下内容会被覆盖
        this.homePageUrl = "http://" + addr.getHostAddress() + ":" + localWebServerPort + "/";

        if(isReadCustomizeProperties){
            Properties properties = new Properties();
            try {
                // 在类路径下获取配置文件
                properties.load(WatchDogClientConfigBean.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME));
            } catch (Exception e) {
                log.warn("no custom config file {} find in the classpath, watchdog client will use default configuration", CONFIG_FILE_NAME);
            }
            Object renewalIntervalInSecs = properties.get(RENEW_INTERVAL_TIMER_IN_MS);
            if (renewalIntervalInSecs != null) {
                this.renewalIntervalInSecs = Integer.parseInt((String) renewalIntervalInSecs);
            }
            Object durationInSecs = properties.get(DURATION_IN_SECS);
            if (durationInSecs != null) {
                this.durationInSecs = Integer.parseInt((String) durationInSecs);
            }

            Object refreshMetricsIntervalInSecs = properties.get(REFRESH_METRICS_INTERVAL_IN_SECS);
            if (refreshMetricsIntervalInSecs != null) {
                this.refreshMetricsIntervalInSecs = Integer.parseInt((String) refreshMetricsIntervalInSecs);
            }
            Object retryTime = properties.get(RETRY_TIME);
            if (retryTime != null) {
                this.retryTime = Integer.parseInt((String) retryTime);
            }
            Object enabled = properties.get(ENABLED);
            if (enabled != null) {
                this.enabled = Boolean.parseBoolean((String) enabled);
            }
            Object appName = properties.get(APP_NAME);
            if (appName != null) {
                this.appName = (String) appName;
            }
            Object homePageUrl = properties.get(HOME_PAGE_URL);
            if (homePageUrl != null) {
                this.homePageUrl = (String) homePageUrl;
            }
        }

    }
}
