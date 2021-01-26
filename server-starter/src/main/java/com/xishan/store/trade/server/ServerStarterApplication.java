package com.xishan.store.trade.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServerStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerStarterApplication.class, args);
    }

}
