package com.kosoft.trojanjava.config;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * trojan服务配置
 */
@Configuration
@ConfigurationProperties(prefix = "trojan")
@Data
public class TrojanServerConfig {

    private int port = 443;

    private int bossThreads = 1;

    private int workerThreads = 16;

    private EventLoopGroup bossExecutors = null;
    private EventLoopGroup eventExecutors = null;

    public EventLoopGroup getBossExecutors() {
        if (bossExecutors == null) {
            bossExecutors =  new NioEventLoopGroup(bossThreads);
        }
        return bossExecutors;
    }

    public EventLoopGroup getEventExecutors() {
        if (eventExecutors == null) {
            eventExecutors = new NioEventLoopGroup(workerThreads);
        }
        return eventExecutors;
    }
}
