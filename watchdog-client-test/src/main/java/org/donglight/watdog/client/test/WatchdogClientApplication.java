package org.donglight.watdog.client.test;

import org.donglight.watchdog.client.web.CommunicationProxy;
import org.donglight.watchdog.client.web.HttpCommunicationProxy;
import org.donglight.watchdog.client.web.filter.URLFilter;
import org.donglight.watchdog.client.web.listener.RegisterToRemoteApplicationListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.Filter;
import javax.servlet.ServletRequestListener;

/**
 * 〈一句话功能简述〉<br>
 * 功能
 *
 * @author donglight
 * @date 2019/4/16
 * @since 1.0.0
 */
@SpringBootApplication
@RestController
@ServletComponentScan
public class WatchdogClientApplication {

    @GetMapping("/hello")
    public String hello() throws InterruptedException {
        return "hello world";
    }

    public static void main(String[] args) {
        SpringApplication.run(WatchdogClientApplication.class,args);
    }



    @Bean
    public CommunicationProxy communicationProxy(){
        return new HttpCommunicationProxy("test-watchdog-client",8080);
    }

    @Bean
    public RegisterToRemoteApplicationListener registerToRemoteListener(){
        return new RegisterToRemoteApplicationListener();
    }

    @Bean
    public FilterRegistrationBean urlFilterRegistrationBean(){
        return new FilterRegistrationBean(urlFilter());
    }

    @Bean
    public Filter urlFilter(){
        return new URLFilter();
    }


}
