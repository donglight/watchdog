package org.donglight.watchdog.server.config;

import org.donglight.watchdog.server.comm.DefaultWebApplicationManager;
import org.donglight.watchdog.server.comm.WebApplicationManager;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * WatchdogServer配置类
 *
 * @author donglight
 * @date 2019/4/18
 * @since 1.0.0
 */
@Configuration
public class WatchdogServerCorsConfiguration {


    @Bean
    public WebApplicationManager webApplicationManager(WatchDogServerConfigBean watchDogServerConfigBean){
        return new DefaultWebApplicationManager(watchDogServerConfigBean);
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FilterRegistrationBean filter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("OPTION");
        configuration.addAllowedMethod("PUT");
        source.registerCorsConfiguration("/**", configuration);
        return new FilterRegistrationBean(new CorsFilter(source));
    }
}
