package org.donglight.watchdog.server;

import org.donglight.watchdog.server.web.DefaultWebApplicationManager;
import org.donglight.watchdog.server.web.WebApplicationManager;
import org.donglight.watchdog.server.web.WebApplicationManagerFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 〈一句话功能简述〉<br>
 * WatchdogServer配置类
 *
 * @author donglight
 * @date 2019/4/18
 * @since 1.0.0
 */
@Configuration
public class WatchdogServerConfiguration {


    @Bean
    public WebApplicationManager webApplicationManager(){
        return new DefaultWebApplicationManager();
    }

    @Bean
    public WebApplicationManagerFacade managerFacade(WebApplicationManager webApplicationManager){

        return new WebApplicationManagerFacade(webApplicationManager);
    }
}
