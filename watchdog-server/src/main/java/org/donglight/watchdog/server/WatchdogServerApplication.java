package org.donglight.watchdog.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * watchdog服务端
 *
 * @author donglight
 * @date 2019/4/16
 * @since 1.0.0
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class WatchdogServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WatchdogServerApplication.class,args);
    }



}
