package com.kosoft.trojanjava.config;

import cn.hutool.core.io.resource.ResourceUtil;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
public class SslConfig {

    private SslContext sslContext = null;
    @Value("${proxy-server.ssl.cert:/config/ssl/fullchain.pem}")
    private String sslCertPath;
    @Value("${proxy-server.ssl.key:/config/ssl/privkey.pem}")
    private String sslKeyPath;


    public SslContext getSslContext() {
        if (sslContext == null) {
            synchronized (this) {
                if (sslContext == null) {
                    try {
                        sslContext = sslContext(sslCertPath, sslKeyPath);
                    } catch (IOException e) {
                        log.error("SSL证书不存在");
                    }
                }
            }
        }
        return sslContext;
    }

    private SslContext sslContext(String cert, String key) throws IOException {
        // 配置 ssl
        final SslContext sslCtx;
        if (new File(key).exists() && new File(cert).exists()) {
            log.info("load local cert ---!");
            sslCtx = SslContextBuilder.forServer(new FileInputStream(cert), new FileInputStream(key)).build();
        } else {
            // 开发测试，用本地的 key
            InputStream keyInputStream = ResourceUtil.getResource("certificate/pkcs8_localhost.key").openStream();
            InputStream keyCertChainInputStream = ResourceUtil.getResource("certificate/localhost.crt").openStream();
            System.out.println("使用配置内的证书");
//            InputStream certInputStream = ResourceUtil.getResource("certificate/localhost.crt").openStream();
//            InputStream keyInputStream = ResourceUtil.getResource("certificate/pkcs8_localhost.key").openStream();
            sslCtx = SslContextBuilder.forServer(keyCertChainInputStream, keyInputStream).build();
        }
        return sslCtx;
    }

    public void updateSslContext( String keyPassword) throws IOException {
        //多线程情况下 可能存在并发问题，这里加锁
        synchronized (this) {
            sslContext = sslContext(sslCertPath, sslKeyPath);
        }
    }


    public String getSslCertPath() {
        return sslCertPath;
    }

    public String getSslKeyPath() {
        return sslKeyPath;
    }
}
