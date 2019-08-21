package org.donglight.spring.boot.watchdog.spring.boot.starter;

import lombok.Getter;
import lombok.Setter;
import org.donglight.watchdog.common.bean.WatchDogClientConfigBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * WatchDogProperties 属性配置类
 *
 * @author donglight
 * @date 2019/5/12
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "watchdog.client")
@Getter
@Setter
public class WatchDogProperties {

    @NestedConfigurationProperty
    private WatchDogClientConfigBean config = new WatchDogClientConfigBean();

    private boolean readCustomizeProperties = false;

    private String proxyType = "rmi";
}
