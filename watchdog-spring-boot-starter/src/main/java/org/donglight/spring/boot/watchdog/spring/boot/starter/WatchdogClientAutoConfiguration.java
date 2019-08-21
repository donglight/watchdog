package org.donglight.spring.boot.watchdog.spring.boot.starter;

import org.apache.commons.lang3.StringUtils;
import org.donglight.watchdog.client.comm.proxy.CommunicationProxy;
import org.donglight.watchdog.client.comm.proxy.HttpCommunicationProxy;
import org.donglight.watchdog.client.comm.proxy.RmiCommunicationProxy;
import org.donglight.watchdog.client.web.filter.URLFilterForSpringMvc;
import org.donglight.watchdog.client.web.listener.RegistryApplicationListener;
import org.donglight.watchdog.common.bean.WatchDogClientConfigBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * WatchdogClientAutoConfiguration 自动配置类
 *
 * @author donglight
 * @date 2019/5/12
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(WatchDogProperties.class)
@ConditionalOnProperty(prefix = "watchdog.client.config", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WatchdogClientAutoConfiguration {


    private final WatchDogProperties watchDogProperties;

    public WatchdogClientAutoConfiguration(WatchDogProperties watchDogProperties) {
        this.watchDogProperties = watchDogProperties;
    }

    @Bean
    @ConditionalOnMissingBean(CommunicationProxy.class)
    @ConditionalOnProperty(prefix = "watchdog.client", name = "proxy-type", havingValue = "rmi", matchIfMissing = true)
    public RmiCommunicationProxy rmiCommunicationProxy() {
        RmiCommunicationProxy rmiCommunicationProxy;
        WatchDogClientConfigBean config = watchDogProperties.getConfig();
        String serverUrl = config.getServerUrl();
        if (StringUtils.isNotEmpty(serverUrl)) {
            rmiCommunicationProxy = new RmiCommunicationProxy(config.getAppName(), serverUrl);
        } else {
            String appName = config.getAppName();
            if (StringUtils.isNotEmpty(appName)) {
                rmiCommunicationProxy = new RmiCommunicationProxy(appName);
            } else {
                rmiCommunicationProxy = new RmiCommunicationProxy();
            }
        }
        config.initWatchDogClientConfigBean(watchDogProperties.isReadCustomizeProperties());
        rmiCommunicationProxy.setWatchDogClientConfigBean(config);
        return rmiCommunicationProxy;
    }

    @Bean
    @ConditionalOnMissingBean(CommunicationProxy.class)
    @ConditionalOnProperty(prefix = "watchdog.client", name = "proxy-type", havingValue = "http")
    public HttpCommunicationProxy httpCommunicationProxy() {
        HttpCommunicationProxy httpCommunicationProxy;
        WatchDogClientConfigBean config = watchDogProperties.getConfig();
        String serverUrl = config.getServerUrl();
        if (StringUtils.isNotEmpty(serverUrl)) {
            httpCommunicationProxy = new HttpCommunicationProxy(config.getAppName(), serverUrl);
        } else {
            String appName = config.getAppName();
            if (StringUtils.isNotEmpty(appName)) {
                httpCommunicationProxy = new HttpCommunicationProxy(appName);
            } else {
                httpCommunicationProxy = new HttpCommunicationProxy();
            }
        }
        config.initWatchDogClientConfigBean(watchDogProperties.isReadCustomizeProperties());
        httpCommunicationProxy.setWatchDogClientConfigBean(config);
        return httpCommunicationProxy;
    }

    @Bean
    @ConditionalOnBean(CommunicationProxy.class)
    public RegistryApplicationListener registerToRemoteListener() {
        return new RegistryApplicationListener();
    }

    @Bean
    @ConditionalOnBean(RmiCommunicationProxy.class)
    public Filter rmiUrlFilter() {
        return new URLFilterForSpringMvc(rmiCommunicationProxy());
    }

    @Bean
    @ConditionalOnBean(HttpCommunicationProxy.class)
    public Filter httpUrlFilter() {
        return new URLFilterForSpringMvc(httpCommunicationProxy());
    }

    @Bean
    @ConditionalOnBean({URLFilterForSpringMvc.class, RmiCommunicationProxy.class})
    public FilterRegistrationBean urlFilterRegistrationBean() {
        FilterRegistrationBean<javax.servlet.Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(rmiUrlFilter());
        filterFilterRegistrationBean.addUrlPatterns("/*");
        filterFilterRegistrationBean.setOrder(1);
        return new FilterRegistrationBean();
    }

    @Bean
    @ConditionalOnBean({URLFilterForSpringMvc.class, HttpCommunicationProxy.class})
    public FilterRegistrationBean httpFilterRegistrationBean() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(httpUrlFilter());
        filterFilterRegistrationBean.addUrlPatterns("/*");
        filterFilterRegistrationBean.setOrder(1);
        return new FilterRegistrationBean();
    }
}
