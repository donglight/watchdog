package org.donglight.watchdog.client.web.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.donglight.watchdog.client.comm.proxy.CommunicationProxy;
import org.donglight.watchdog.client.comm.proxy.HttpCommunicationProxy;
import org.donglight.watchdog.client.comm.proxy.RmiCommunicationProxy;
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

    public static final String WD_COMMUNICATION_PROXY = "wdCommunicationProxy";

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        WatchDogClientConfigBean watchDogClientConfigBean = new WatchDogClientConfigBean();
        watchDogClientConfigBean.initWatchDogClientConfigBean(true);
        if (watchDogClientConfigBean.isEnabled()) {
            log.info("watch dog client is starting");
            ServletContext servletContext = sce.getServletContext();
            watchDogClientConfigBean.setServerInfo(servletContext.getServerInfo());

            CommunicationProxy communicationProxy = newCommunicationProxy(servletContext, watchDogClientConfigBean);

            servletContext.setAttribute(WD_COMMUNICATION_PROXY, communicationProxy);

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

    private CommunicationProxy newCommunicationProxy(ServletContext servletContext, WatchDogClientConfigBean watchDogClientConfigBean) {
        //从web.xml中获取web应用参数
        String proxyTypeFromInitParam = servletContext.getInitParameter(WatchDogClientConfigBean.PROXY_TYPE_KET);
        String serverUrlFromInitParam = servletContext.getInitParameter(WatchDogClientConfigBean.SERVER_URL_KET);

        //从配置文件中获取的参数
        String proxyTypeProperties = watchDogClientConfigBean.getProxyType();
        String serverUrlFromProperties = watchDogClientConfigBean.getServerUrl();

        CommunicationProxy communicationProxy;
        if (WatchDogClientConfigBean.WATCH_DOG_RMI_PROXY.equals(proxyTypeFromInitParam)) {
            if (StringUtils.isNotEmpty(serverUrlFromInitParam)) {
                communicationProxy = new RmiCommunicationProxy(watchDogClientConfigBean.getAppName(), serverUrlFromInitParam);
            } else {
                communicationProxy = new RmiCommunicationProxy(watchDogClientConfigBean.getAppName());
            }
        } else if (WatchDogClientConfigBean.WATCH_DOG_HTTP_PROXY.equals(proxyTypeFromInitParam)) {
            if (StringUtils.isNotEmpty(serverUrlFromInitParam)) {
                communicationProxy = new HttpCommunicationProxy(watchDogClientConfigBean.getAppName(), serverUrlFromInitParam);
            } else {
                communicationProxy = new HttpCommunicationProxy(watchDogClientConfigBean.getAppName());
            }
        } else {
            if (WatchDogClientConfigBean.WATCH_DOG_RMI_PROXY.equals(proxyTypeProperties)) {
                if (StringUtils.isNotEmpty(serverUrlFromProperties)) {
                    communicationProxy = new RmiCommunicationProxy(watchDogClientConfigBean.getAppName(), serverUrlFromProperties);
                } else {
                    communicationProxy = new RmiCommunicationProxy(watchDogClientConfigBean.getAppName());
                }
            } else if (WatchDogClientConfigBean.WATCH_DOG_HTTP_PROXY.equals(serverUrlFromProperties)) {
                if (StringUtils.isNotEmpty(serverUrlFromProperties)) {
                    communicationProxy = new HttpCommunicationProxy(watchDogClientConfigBean.getAppName(), serverUrlFromProperties);
                } else {
                    communicationProxy = new HttpCommunicationProxy(watchDogClientConfigBean.getAppName());
                }
            } else {
                throw new IllegalArgumentException("Incorrect proxy type configuration");
            }
        }
        return communicationProxy;
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
