package org.donglight.watchdog.server;

import org.donglight.watchdog.server.web.DefaultWebApplicationManager;
import org.donglight.watchdog.server.web.WebApplicationManager;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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


    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FilterRegistrationBean filter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:8082");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("OPTION");
        configuration.addAllowedMethod("PUT");
        source.registerCorsConfiguration("/**", configuration);
        return new FilterRegistrationBean(new CorsFilter(source));
    }
}
