package org.donglight.watchdog.client;

import org.donglight.watchdog.client.web.CommunicationProxy;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 〈一句话功能简述〉<br>
 * 当应用一启动就注册到远程监控服务器
 *
 * @author donglight
 * @date 2019/4/20
 * @since 1.0.0
 */
public class RegisterToRemoteListener implements ApplicationListener<ContextRefreshedEvent> {

    private CommunicationProxy communicationProxy;

    @Autowired
    public RegisterToRemoteListener(CommunicationProxy communicationProxy) {
        this.communicationProxy = communicationProxy;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        communicationProxy.remoteRegister("watch-dog-client");
       /* ApplicationContext context = contextRefreshedEvent.getApplicationContext();
        if (context instanceof WebApplicationContext) {
            if (context instanceof WebServerApplicationContext) {
                WebApplicationContext webApplicationContext = (WebApplicationContext) context;
                getUrlInfos(webApplicationContext);
            } else if (context.getParent() != null) {
                WebApplicationContext webApplicationContext = (WebApplicationContext) context;
                getUrlInfos(webApplicationContext);
            }
        }*/
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
            for (RequestMethod requestMethod : methodsCondition.getMethods()) {
                builder.httpMethod(requestMethod.toString());
            }
            urlInfoList.add(builder.build());
            builder.clear();
        }
        return urlInfoList;
    }
}
