package org.donglight.watchdog.client.comm.proxy;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.donglight.watchdog.client.web.MetricsCollector;
import org.donglight.watchdog.common.bean.InstanceInfo;
import org.donglight.watchdog.common.bean.URLInfo;
import org.donglight.watchdog.common.bean.WatchDogClientConfigBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * CommunicationProxy 抽象类
 *
 * @author donglight
 * @date 2019/5/4
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractCommunicationProxy implements CommunicationProxy {

    /**
     * 默认监控服务端通信是rmi 地址是rmi://127.0.0.1:1099/watchDogRMIService
     */
    protected static final String DEFAULT_SERVER_URL = "rmi://127.0.0.1:1099/watchDogRMIService";

    protected String serverUrl;
    /**
     * watchdog客户端配置信息
     */
    protected WatchDogClientConfigBean watchDogClientConfigBean;
    /**
     * 保存已经受监控的URL列表
     */
    protected List<URLInfo> watchingURLInfos;
    /**
     * 默认不记录静态资源的URL，可以在filter中重新自定义
     */
    protected String[] excludeUrls = {".js", ".css", ".ico", ".jpg", ".png", ".gif", ".jsp", ".html", ".xml", ".json", ".txt", ".swf", ".zip"};
    /**
     * 应用名称,默认是当前系统的用户名
     */
    protected String appName;

    protected int retryTime;

    /**
     * 服务端返回的instanceInfo
     */
    protected InstanceInfo instanceInfo;
    protected Runnable renewTask;
    protected Runnable collectTask;
    protected MetricsCollector metricsCollector;
    protected ScheduledExecutorService timerPool;
    protected static final String PING = "ping";
    protected static final String PONG = "pong";


    protected AbstractCommunicationProxy() {
        this(System.getProperty("user.name"));
    }

    protected AbstractCommunicationProxy(String appName) {
        this(appName, DEFAULT_SERVER_URL);
    }

    protected AbstractCommunicationProxy(String appName, String serverUrl) {
        this.appName = appName;
        this.serverUrl = serverUrl;
        this.watchingURLInfos = new ArrayList<>();
        this.metricsCollector = new MetricsCollector();
        this.timerPool = new ScheduledThreadPoolExecutor(2,
                new BasicThreadFactory.Builder().namingPattern("timer-pool-%d").daemon(true).build());
    }

    /**
     * 获得正在被监视的URLInfos
     *
     * @return List<URLInfo>
     */
    @Override
    public List<URLInfo> getWatchingURLInfos() {
        return this.watchingURLInfos;
    }

    /**
     * 是否过滤URL
     *
     * @param url url
     */
    @Override
    public boolean isFilterExcludeUrl(String url) {
        for (String excludeUrl : excludeUrls) {
            if (url.endsWith(excludeUrl)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setFilterExcludeUrl(String[] excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    protected void startTimerTask() {

        // 开启应用续约定时任务
        if (renewTask == null) {
            renewTask = new AbstractCommunicationProxy.RenewTimerTask();
            timerPool.scheduleAtFixedRate(renewTask, 1, watchDogClientConfigBean.getRenewalIntervalInSecs(), TimeUnit.SECONDS);
            log.info("start renew timer task task in timerPool");
        }
        // 开启metrics信息定时发送
        if (collectTask == null) {
            collectTask = new AbstractCommunicationProxy.CollectMetricsTimerTask();
            timerPool.scheduleAtFixedRate(collectTask, 5, watchDogClientConfigBean.getRefreshMetricsIntervalInSecs(), TimeUnit.SECONDS);
            log.info("start collect metric timer task in timerPool");
        }
    }

    class RenewTimerTask implements Runnable {
        @Override
        public void run() {
            try {
                AbstractCommunicationProxy.this.renew();
            }catch (Exception e){
                e.printStackTrace();
                AbstractCommunicationProxy.this.renew();
            }
        }
    }

    class CollectMetricsTimerTask implements Runnable {
        @Override
        public void run() {
            try {
                AbstractCommunicationProxy.this.pushMetrics(metricsCollector.refreshMetrics());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public WatchDogClientConfigBean getWatchDogClientConfigBean() {
        return watchDogClientConfigBean;
    }
}
