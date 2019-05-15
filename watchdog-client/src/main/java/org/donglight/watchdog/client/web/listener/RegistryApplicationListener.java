package org.donglight.watchdog.client.web.listener;

import lombok.extern.slf4j.Slf4j;
import org.donglight.watchdog.client.web.proxy.AbstractCommunicationProxy;
import org.donglight.watchdog.client.web.proxy.CommunicationProxy;
import org.donglight.watchdog.client.web.proxy.RmiCommunicationProxy;
import org.donglight.watchdog.common.bean.URLInfo;
import org.donglight.watchdog.common.bean.WatchDogClientConfigBean;
import org.donglight.watchdog.common.util.IDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


/**
 * 针对使用spring 的 java web应用
 * 当应用一启动就注册到远程监控服务器
 *
 * @author donglight
 * @date 2019/4/20
 * @since 1.0.0
 */
@Slf4j
public class RegistryApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private CommunicationProxy communicationProxy;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        AbstractCommunicationProxy abstractCommunicationProxy = (AbstractCommunicationProxy) communicationProxy;
        WatchDogClientConfigBean watchDogClientConfigBean = abstractCommunicationProxy.getWatchDogClientConfigBean();
        if (watchDogClientConfigBean == null) {
            watchDogClientConfigBean = new WatchDogClientConfigBean();
            watchDogClientConfigBean.initWatchDogClientConfigBean(true);
        }
        if (watchDogClientConfigBean.isEnabled()) {
            log.info("watch dog client is starting");
            ApplicationContext context = contextRefreshedEvent.getApplicationContext();
            if (context instanceof WebApplicationContext) {
                if (context instanceof WebServerApplicationContext || context.getParent() != null) {

                    if (communicationProxy == null) {
                        communicationProxy = new RmiCommunicationProxy();
                    }
                    WebApplicationContext webApplicationContext = (WebApplicationContext) context;

                    ServletContext servletContext = webApplicationContext.getServletContext();

                    if (servletContext != null) {
                        watchDogClientConfigBean.setServerInfo(webApplicationContext.getServletContext().getServerInfo());
                    }
                    // 注册到远程监控服务
                    if (communicationProxy.remoteRegister(watchDogClientConfigBean)) {
                        // 获取所有的mapping
                        communicationProxy.pushUrls(getUrlInfos(webApplicationContext));
                    }
                }
            } else {
                communicationProxy.remoteRegister(watchDogClientConfigBean);
            }
            log.info("watch dog client has started");
        } else {
            log.warn("watch dog client is disabled");
        }
    }

    private List<URLInfo> getUrlInfos(WebApplicationContext webApplicationContext) {

        RequestMappingHandlerMapping mapping = webApplicationContext.getBean(RequestMappingHandlerMapping.class);
        // 获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        List<URLInfo> urlInfoList = new ArrayList<>();
        URLInfo.Builder builder = new URLInfo.Builder();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            RequestMappingInfo info = m.getKey();
            //HandlerMethod method = m.getValue();
            PatternsRequestCondition p = info.getPatternsCondition();
            builder.id(IDUtils.genUrlId());
            for (String url : p.getPatterns()) {
                builder.url(url);
            }
            //map1.put("className", method.getMethod().getDeclaringClass().getName()); // 类名
            //map1.put("method", method.getMethod().getName()); // 方法名
            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            if (methodsCondition.getMethods().size() == 0) {
                builder.httpMethod("未定义");
            } else {
                for (RequestMethod requestMethod : methodsCondition.getMethods()) {
                    builder.httpMethod(requestMethod.toString());
                }
            }
            urlInfoList.add(builder.build());
            builder.clear();
        }
        urlInfoList.sort(Comparator.comparing(URLInfo::getUrl));
        return urlInfoList;
    }

    public void setCommunicationProxy(CommunicationProxy communicationProxy) {
        this.communicationProxy = communicationProxy;
    }
}
