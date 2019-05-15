package org.donglight.watchdog.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * WatchDog 服务相关配置类，可在外部配置
 *
 * @author donglight
 * @date 2019/4/30
 * @since 1.0.0
 */
@Component
@ConfigurationProperties(prefix = WatchDogServerConfigBean.PREFIX)
@Getter
@Setter
public class WatchDogServerConfigBean {

    public static final String PREFIX = "watchdog.server";

    /**
     * 服务失效剔除任务轮询间隔时间
     */
    private long evictionIntervalTimerInMs = 10 * 1000;


    /**
     * 发现失效后，多长时间remove 剔除
     */
    private int leaseExpirationRemoveDurationInSeconds = 30;

    /**
     * RMI名，默认是 watchDogRMIService
     */
    private String rmiName = "watchDogRMIService";

    /**
     * RMI端口，默认是1099
     */
    private int rmiPort = 1099;

    private boolean persistenceEnabled = false;

    /**
     * 持久化策略，默认是 database
     */
    private String persistenceType = "database";


}
