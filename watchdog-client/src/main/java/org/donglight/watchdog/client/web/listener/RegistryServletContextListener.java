package org.donglight.watchdog.client.web.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.donglight.watchdog.client.web.proxy.CommunicationProxy;
import org.donglight.watchdog.client.web.proxy.HttpCommunicationProxy;
import org.donglight.watchdog.client.web.proxy.RmiCommunicationProxy;
import org.donglight.watchdog.common.bean.URLInfo;
import org.donglight.watchdog.common.bean.WatchDogClientConfigBean;
import org.donglight.watchdog.common.util.IDUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 普通servlet应用，不依赖spring
 * servlet listener listen application startup, then register to remote server
 *
 * @author donglight
 * @date 2019/4/23
 * @since 1.0.0
 */

@Slf4j
public class RegistryServletContextListener implements ServletContextListener {

    private static final String WATCH_DOG_PROXY_TYPE = "watch.dog.proxy.type";
    public static final String WD_COMMUNICATION_PROXY = "wdCommunicationProxy";
    private static final String WATCH_DOG_RMI_PROXY = "rmi";
    private static final String WATCH_DOG_HTTP_PROXY = "http";
    private static final String SERVER_URL = "serverUrl";

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        WatchDogClientConfigBean watchDogClientConfigBean = new WatchDogClientConfigBean();
        watchDogClientConfigBean.initWatchDogClientConfigBean(true);
        if (watchDogClientConfigBean.isEnabled()) {
            log.info("watch dog client is starting");
            ServletContext servletContext = sce.getServletContext();
            //从web.xml中获取web应用参数
            String proxyType = servletContext.getInitParameter(WATCH_DOG_PROXY_TYPE);
            String serverUrl = servletContext.getInitParameter(SERVER_URL);

            CommunicationProxy communicationProxy;
            if (StringUtils.isEmpty(proxyType) || WATCH_DOG_RMI_PROXY.equals(proxyType)) {
                if (StringUtils.isNotEmpty(serverUrl)) {
                    communicationProxy = new RmiCommunicationProxy(watchDogClientConfigBean.getAppName(), serverUrl);
                } else {
                    communicationProxy = new RmiCommunicationProxy();
                }
            } else if (WATCH_DOG_HTTP_PROXY.equals(proxyType)) {
                if (StringUtils.isNotEmpty(serverUrl)) {
                    communicationProxy = new HttpCommunicationProxy(watchDogClientConfigBean.getAppName(), serverUrl);
                } else {
                    communicationProxy = new HttpCommunicationProxy();
                }
            } else {
                throw new IllegalArgumentException("Incorrect proxy type configuration");
            }
            servletContext.setAttribute(WD_COMMUNICATION_PROXY, communicationProxy);
            watchDogClientConfigBean.setServerInfo(servletContext.getServerInfo());
            // 注册到远程监控服务
            if (communicationProxy.remoteRegister(watchDogClientConfigBean)) {
                // 获取所有的mapping
                communicationProxy.pushUrls(getUrlInfos(servletContext));
            }
            log.info("watch dog client has started");
        } else {
            log.warn("watch dog client is disabled");
        }
    }

    private List<URLInfo> getUrlInfos(ServletContext servletContext) {
        Map<String, ? extends ServletRegistration> servletRegistrations = servletContext.getServletRegistrations();
        List<URLInfo> urlInfos = new ArrayList<>();
        URLInfo.Builder builder = new URLInfo.Builder();
        for (ServletRegistration value : servletRegistrations.values()) {
            builder.id(IDUtils.genUrlId());
            Collection<String> mappings = value.getMappings();
            StringBuilder stringBuilder = new StringBuilder();
            for (String mapping : mappings) {
                stringBuilder.append(mapping).append(",");
            }
            builder.url(stringBuilder.substring(0, stringBuilder.lastIndexOf(",")));
            builder.httpMethod("未定义");
            urlInfos.add(builder.build());
            builder.clear();
        }
        return urlInfos;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
