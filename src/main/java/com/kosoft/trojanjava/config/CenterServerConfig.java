package com.kosoft.trojanjava.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "center-server")
@Data
public class CenterServerConfig {

    private String host;


}
