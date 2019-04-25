package org.donglight.watchdog.client.web.listener;

import org.donglight.watchdog.client.web.CommunicationProxy;
import org.donglight.watchdog.client.web.HttpCommunicationProxy;
import org.donglight.watchdog.client.web.filter.URLFilter;
import org.donglight.watchdog.common.domain.URLInfo;
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

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;


/**
 * 针对spring java web应用
 * implements spring ApplicationListener，
 * 当应用一启动就注册到远程监控服务器
 *
 * @author donglight
 * @date 2019/4/20
 * @since 1.0.0
 */
public class RegisterToRemoteApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private CommunicationProxy communicationProxy;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext context = contextRefreshedEvent.getApplicationContext();
        if (context instanceof WebApplicationContext) {
            if (context instanceof WebServerApplicationContext || context.getParent() != null) {

                if (communicationProxy == null) {
                    communicationProxy = new HttpCommunicationProxy();
                }
                WebApplicationContext webApplicationContext = (WebApplicationContext) context;


                // 获取所有的mapping
                getUrlInfos(webApplicationContext);

                // 注册到远程监控服务器
                if (communicationProxy.remoteRegister()) {
                    communicationProxy.pushUrlsToWatchServer(getUrlInfos(webApplicationContext));
                } else {
                    throw new RuntimeException("fail register to remote server,please check client name and remote address.");
                }
            }
        }
    }

    private List<URLInfo> getUrlInfos(WebApplicationContext webApplicationContext) {

        RequestMappingHandlerMapping mapping = webApplicationContext.getBean(RequestMappingHandlerMapping.class);
        // 获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        List<URLInfo> urlInfoList = new ArrayList<>();
        URLInfo.Builder builder;
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            RequestMappingInfo info = m.getKey();
            //HandlerMethod method = m.getValue();
            PatternsRequestCondition p = info.getPatternsCondition();
            builder = new URLInfo.Builder();
            builder.id(IDUtils.genUrlId());
            for (String url : p.getPatterns()) {
                builder.url(url);
            }
            //map1.put("className", method.getMethod().getDeclaringClass().getName()); // 类名
            //map1.put("method", method.getMethod().getName()); // 方法名
            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            if(methodsCondition.getMethods().size() == 0) {
                builder.httpMethod("All");
            }else {
                for (RequestMethod requestMethod : methodsCondition.getMethods()) {
                    builder.httpMethod(requestMethod.toString());
                }
            }
            builder.urlStatList(new ArrayList<>());
            urlInfoList.add(builder.build());
            builder.clear();
        }
        return urlInfoList;
    }

    public void setCommunicationProxy(CommunicationProxy communicationProxy) {
        this.communicationProxy = communicationProxy;
    }
}
