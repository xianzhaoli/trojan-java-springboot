package com.kosoft.trojanjava.server;

import com.kosoft.trojanjava.codec.TrojanDecoder;
import com.kosoft.trojanjava.config.SslConfig;
import com.kosoft.trojanjava.config.TrojanServerConfig;
import com.kosoft.trojanjava.handler.HttpMessageHandler;
import com.kosoft.trojanjava.handler.TrojanMessageHandler;
import com.kosoft.trojanjava.service.UserService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.flow.FlowControlHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TrojanServer  implements CommandLineRunner, DisposableBean {



    @Resource
    private TrojanServerConfig trojanServerConfig;


    @Resource
    private SslConfig sslConfig;

    @Resource
    private UserService userService;

    @Override
    public void destroy() throws Exception {
        trojanServerConfig.getBossExecutors().shutdownGracefully();
        trojanServerConfig.getEventExecutors().shutdownGracefully();
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(trojanServerConfig.getBossExecutors(), trojanServerConfig.getEventExecutors())
                    .channel(NioServerSocketChannel.class);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000);

            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    final ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(sslConfig.getSslContext().newHandler(socketChannel.alloc()));  // 添加 SSL 加密
                    pipeline
//                        .addLast(new IdleStateHandler(5, 5, 5, TimeUnit.MINUTES))
                            .addLast(new FlowControlHandler())
                            .addLast(new TrojanDecoder())
                            .addLast(new HttpServerCodec())
//                        .addLast(new TrojanOutBoundHandler(userService))
                            .addLast(new TrojanMessageHandler(userService))
                            .addLast(new HttpMessageHandler());


                }
            }).childOption(ChannelOption.AUTO_READ, false);
            final ChannelFuture sync = bootstrap.bind(trojanServerConfig.getPort()).sync();
            log.info("---------------------------------------------");
            log.info("|trojan server start successful on port {} ! |",trojanServerConfig.getPort());
            log.info("---------------------------------------------");
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            trojanServerConfig.getBossExecutors().shutdownGracefully();
            trojanServerConfig.getEventExecutors().shutdownGracefully();
        }
    }
}
