package org.donglight.watchdog.client;

import org.donglight.watchdog.client.web.CommunicationProxy;
import org.donglight.watchdog.client.web.DefaultCommunicationProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class WatchdogClientApplication {

    @GetMapping("/hello")
    public String hello(){
        return "hello world";
    }

    public static void main(String[] args) {
        SpringApplication.run(WatchdogClientApplication.class,args);
    }

    @Bean
    public RegisterToRemoteListener registerToRemoteListener(){
        return new RegisterToRemoteListener(defaultCommunicationProxy());
    }

    @Bean
    public CommunicationProxy defaultCommunicationProxy(){
        return new DefaultCommunicationProxy("http://127.0.0.1:8080/api");
    }
}
