package org.donglight.watdog.client.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * 启动类
 *
 * @author donglight
 * @date 2019/4/16
 * @since 1.0.0
 */
@SpringBootApplication
@RestController
public class WatchdogClientApplication {

    @GetMapping("/hello/{id}")
    public String hello(@PathVariable("id") String id) throws InterruptedException {
        Random random = new Random();
        Thread.sleep(random.nextInt(500));
        return "hello world " + id;
    }

    @GetMapping("/hello2")
    public String hello2() throws InterruptedException {
        Random random = new Random();
        Thread.sleep(random.nextInt(500));
        return "hello world2";
    }

    @GetMapping("/hello3dsfsdfdfgdfgdfgdsadsad423423423")
    public String hello3() throws InterruptedException {
        Random random = new Random();
        Thread.sleep(random.nextInt(500));
        return "hello world3dsfsdfdfgdfgdfgdsadsad";
    }

    public static void main(String[] args) {
        SpringApplication.run(WatchdogClientApplication.class, args);
    }

}
