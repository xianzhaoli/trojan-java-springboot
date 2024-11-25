package com.kosoft.trojanjava.handler;

import cn.hutool.core.util.HexUtil;
import com.kosoft.trojanjava.channel.RelayHandler;
import com.kosoft.trojanjava.codec.*;
import com.kosoft.trojanjava.service.UserService;
import com.kosoft.trojanjava.utils.TrojanServerUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.flow.FlowControlHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Block;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class TrojanMessageHandler extends ChannelInboundHandlerAdapter {


    private final Bootstrap b = new Bootstrap();

    private UserService userService;

    public TrojanMessageHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof TrojanMessage) {
            // 直接删除当前 handler 和 解码器
            ctx.pipeline().remove(TrojanDecoder.class);
            // ctx.pipeline().remove(TrojanMessageHandler.class);
            handleTrojanMessage(ctx, (TrojanMessage) msg);
        }
        ctx.fireChannelRead(msg);

    }

    private void handleTrojanMessage(ChannelHandlerContext ctx, TrojanMessage trojanMessage) throws Exception {
        String trojanKey = trojanMessage.getKey();
        TrojanRequest trojanRequest = trojanMessage.getTrojanRequest();
        TrojanCommandType cmdType = trojanRequest.getCommandType();
        // 只支持 tcp 和 udp
        // udp需要单独处理，现在没开发。
        if (!(cmdType.equals(TrojanCommandType.CONNECT) || cmdType.equals(TrojanCommandType.UDP_ASSOCIATE))) {
            throw new RuntimeException("unsupported cmd type: " + cmdType);
        }
        final String dstAddr = trojanRequest.getDstAddr();
        final int dstPort = trojanRequest.getDstPort();
        log.debug("cmdType: {}, 请求目标地址为：[{}:{}]", cmdType, dstAddr, dstPort);

        // TODO 添加流量校验和统计
        final Channel userChannel = ctx.channel();
        if (!userService.auth(trojanKey)) {
            TrojanServerUtils.closeOnFlush(userChannel);
            return;
        }
        ByteBuf payload = trojanMessage.getPayload();

        if (TrojanCommandType.CONNECT.equals(cmdType)) {

            Promise<Channel> promise = ctx.executor().newPromise();
            promise.addListener(future -> {
                final Channel outboundChannel = (Channel) future.getNow();
                if (future.isSuccess()) {
                    bindChannelAndWrite(userChannel, outboundChannel, payload, trojanKey);
                } else {
                    TrojanServerUtils.closeOnFlush(userChannel);
                }
            });

            b.group(userChannel.eventLoop())
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new DirectClientHandler(promise));

            b.connect(dstAddr, dstPort).addListener(future -> {
                if (!future.isSuccess()) {
//                    log.info("代理连接失败:{},问题{}", dstAddr + ":"+dstPort,future.cause());
                    // Close the connection if the connection attempt has failed.
                    TrojanServerUtils.closeOnFlush(ctx.channel());
                } else {
//                    log.info("代理连接成功:{}", dstAddr + ":"+dstPort);
                }
            });
            //ctx.pipeline().addLast(new TrojanTunnelHandler(dstAddr, dstPort, userChannel,payload));
        } else {
            // udp protocol
            Promise<Channel> promise = ctx.executor().newPromise();
            promise.addListener(futureListener -> {
                final Channel outboundChannel = (Channel) futureListener.getNow();
                if (futureListener.isSuccess()) {
                    outboundChannel.pipeline().addLast(new RelayHandler(userChannel,userService, trojanKey));
                    ChannelPipeline userChannelPipeline = userChannel.pipeline();
                    userChannelPipeline.addLast(new TrojanUdpPacketEncoder());
                    userChannelPipeline.addLast(new TrojanUdpPacketDecoder());
                    userChannelPipeline.addLast(new RelayHandler(outboundChannel,userService, trojanKey));
                    userChannelPipeline.remove(FlowControlHandler.class);
                    userChannel.config().setAutoRead(true);
                    if (payload != null) {
                        DatagramPacket datagramPacket = TrojanUdpPacketDecoder.getDatagramPacket(ctx, payload);
                        outboundChannel.writeAndFlush(datagramPacket);
                    }
                } else {
                    TrojanServerUtils.closeOnFlush(userChannel);
                }
            });

            Bootstrap udpBootStrap = new Bootstrap();
            udpBootStrap.group(userChannel.eventLoop())
                    .channel(NioDatagramChannel.class)
                    .handler(new DirectClientHandler(promise));
            udpBootStrap.bind(0).addListener(futureListener -> {
                if (!futureListener.isSuccess()) {
                    TrojanServerUtils.closeOnFlush(userChannel);
                }
            });

        }
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
        ctx.channel().config().setAutoRead(false);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        log.info("用户连接关闭:{}", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }


    private void bindChannelAndWrite(Channel inboundChannel, Channel outboundChannel, Object writeData, String token) {
        if (writeData != null) {
//            ByteBuf buf = (ByteBuf) writeData;
//            byte[] bytes = new byte[buf.readableBytes()];
//            buf.readBytes(bytes, 0, buf.readableBytes());
//            buf.readerIndex(0);
//            log.info("proxy send data : remoteAddr:{}      data:{}", outboundChannel.remoteAddress(), HexUtil.encodeHexStr(bytes));
            Future<Void> responseFuture = outboundChannel.writeAndFlush(writeData);
            responseFuture.addListener(channelFuture -> bindChannel(inboundChannel, outboundChannel, token));
        } else {
            bindChannel(inboundChannel, outboundChannel, token);
        }
    }

    private void bindChannel(Channel inboundChannel, Channel outboundChannel, String token) {
        outboundChannel.pipeline().addLast(new RelayHandler(inboundChannel, userService, token));
        ChannelPipeline inboundChannelPipeline = inboundChannel.pipeline();
        inboundChannelPipeline.addLast(new RelayHandler(outboundChannel, userService, token));
        inboundChannelPipeline.remove(FlowControlHandler.class);
        inboundChannel.config().setAutoRead(true);
    }

}
