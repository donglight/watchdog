package org.donglight.watchdog.client.web.listener;

import org.donglight.watchdog.client.web.CommunicationProxy;
import org.donglight.watchdog.client.web.HttpCommunicationProxy;
import org.donglight.watchdog.common.util.CommomUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;
import java.util.Map;

/**
 * TODO 尚未完成
 * 普通servlet应用，不依赖spring
 * servlet listener listen application started, register to remote server
 *
 * @author donglight
 * @date 2019/4/23
 * @since 1.0.0
 */

@WebListener
public class RegisterToRemoteServletContextListener implements ServletContextListener {

    private CommunicationProxy communicationProxy;

    public RegisterToRemoteServletContextListener(CommunicationProxy communicationProxy) {
        this.communicationProxy = communicationProxy;
    }

    public RegisterToRemoteServletContextListener() {

    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (!CommomUtil.isClassPresent("org.springframework.web.context.WebApplicationContext")) {
            Map<String, ? extends ServletRegistration> servletRegistrations = sce.getServletContext().getServletRegistrations();
            if (!communicationProxy.remoteRegister()) {
                throw new RuntimeException("fail register to remote server,please check client name and remote address.");
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    public void setCommunicationProxy(CommunicationProxy communicationProxy) {
        this.communicationProxy = communicationProxy;
    }
}
