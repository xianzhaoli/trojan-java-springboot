package com.kosoft.trojanjava;

import com.kosoft.trojanjava.server.TrojanServer;
import jakarta.annotation.Resource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
public class TrojanJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrojanJavaApplication.class, args);
    }

}
