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
    public static final String CONFIG_FILE_NAME = "watchdog.properties";
    public static final String RENEW_INTERVAL_TIMER_IN_MS_KET = "watchdog.client.renewalIntervalInSecs";
    public static final String DURATION_IN_SECS_KET = "watchdog.client.durationInSecs";
    public static final String HOME_PAGE_URL_KET = "watchdog.client.homePageUrl";
    public static final String REFRESH_METRICS_INTERVAL_IN_SECS_KET = "watchdog.client.refreshMetricsIntervalInSecs";
    public static final String RETRY_TIME_KET = "watchdog.client.retryTime";
    public static final String APP_NAME_KET = "watchdog.client.appName";
    public static final String ENABLED_KET = "watchdog.client.enabled";
    public static final String SERVER_URL_KET = "watchdog.client.serverUrl";
    public static final String PROXY_TYPE_KET = "watchdog.client.proxyType";
    public static final String WATCH_DOG_RMI_PROXY = "rmi";
    public static final String WATCH_DOG_HTTP_PROXY = "http";

    /**
     * # watchdog客户端(应用实例)续约任务间隔时间
     */
    private int renewalIntervalInSecs = DEFAULT_LEASE_RENEWAL_INTERVAL;

    /**
     * watchdog客户端多久不续约被剔除或设置为过期DWON状态
     */
    private int durationInSecs = DEFAULT_LEASE_DURATION;
    /**
     * # watchdog客户端多久收集一次自身的指标信息
     */
    private int refreshMetricsIntervalInSecs = DEFAULT_REFRESH_METRICS_INTERVAL_IN_SECS;
    /**
     * # watchdog访问服务端失败重试次数
     */
    private int retryTime = DEFAULT_RETRY_TIME;
    /**
     * 是否开启watchdog客户端
     */
    private boolean enabled = true;
    private String ipAddr;
    private int clientWebServerPort;
    /**
     * watchdog客户端应用名
     */
    private String appName = "";
    private String hostName;
    /**
     * watchdog.client.homePageUrl
     */
    private String homePageUrl;
    private String instanceId;
    private String serverInfo;
    /**
     * 注册服务端地址
     */
    private String serverUrl;
    /**
     * 代理类型rmi或http
     */
    private String proxyType = WATCH_DOG_RMI_PROXY;

    public void initWatchDogClientConfigBean(boolean isReadCustomizeProperties) {

        int localWebServerPort = CommonUtil.getLocalWebServerPort();
        this.clientWebServerPort = localWebServerPort;
        InetAddress addr = CommonUtil.getLocalIpv4Address();
        this.ipAddr = addr.getHostAddress();
        this.hostName = addr.getHostName();
        this.instanceId = addr.getHostAddress() + ":" + localWebServerPort;
        // 默认值，如果有在配置文件中配置如下内容会被覆盖
        this.homePageUrl = "http://" + addr.getHostAddress() + ":" + localWebServerPort + "/";

        if (isReadCustomizeProperties) {
            Properties properties = new Properties();
            try {
                // 在类路径下获取配置文件
                properties.load(WatchDogClientConfigBean.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME));
            } catch (Exception e) {
                log.warn("no custom config file {} find in the classpath, watchdog client will use default configuration", CONFIG_FILE_NAME);
            }
            Object renewalIntervalInSecs = properties.get(RENEW_INTERVAL_TIMER_IN_MS_KET);
            if (renewalIntervalInSecs != null) {
                this.renewalIntervalInSecs = Integer.parseInt((String) renewalIntervalInSecs);
            }
            Object durationInSecs = properties.get(DURATION_IN_SECS_KET);
            if (durationInSecs != null) {
                this.durationInSecs = Integer.parseInt((String) durationInSecs);
            }

            Object refreshMetricsIntervalInSecs = properties.get(REFRESH_METRICS_INTERVAL_IN_SECS_KET);
            if (refreshMetricsIntervalInSecs != null) {
                this.refreshMetricsIntervalInSecs = Integer.parseInt((String) refreshMetricsIntervalInSecs);
            }
            Object retryTime = properties.get(RETRY_TIME_KET);
            if (retryTime != null) {
                this.retryTime = Integer.parseInt((String) retryTime);
            }
            Object enabled = properties.get(ENABLED_KET);
            if (enabled != null) {
                this.enabled = Boolean.parseBoolean((String) enabled);
            }
            Object appName = properties.get(APP_NAME_KET);
            if (appName != null) {
                this.appName = (String) appName;
            }
            Object homePageUrl = properties.get(HOME_PAGE_URL_KET);
            if (homePageUrl != null) {
                this.homePageUrl = (String) homePageUrl;
            }
            Object serverUrl = properties.get(SERVER_URL_KET);
            if (serverUrl != null) {
                this.serverUrl = (String) serverUrl;
            }
            Object proxyType = properties.get(PROXY_TYPE_KET);
            if (proxyType != null) {
                this.proxyType = (String) proxyType;
            }
        }

    }
}
