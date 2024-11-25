package com.kosoft.trojanjava.channel;

import cn.hutool.core.util.HexUtil;
import com.kosoft.trojanjava.service.UserService;
import com.kosoft.trojanjava.utils.TrojanServerUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RelayHandler extends ChannelInboundHandlerAdapter {

    private final Channel relayChannel;


    private UserService userService;

    private String token;


    public RelayHandler(Channel relayChannel, UserService userService, String token) {
        this.relayChannel = relayChannel;
        this.userService = userService;
        this.token = token;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        byte[] bytes = new byte[buf.readableBytes()];
//        buf.readBytes(bytes, 0, buf.readableBytes());
//        buf.readerIndex(0);
//        log.info("proxy send data : remoteAddr:{},data:{}", ctx.channel().remoteAddress(), HexUtil.encodeHexStr(bytes));
        if (relayChannel.isActive()) {
            try {
                ByteBuf buf = (ByteBuf) msg;
                userService.usedFlow(token, buf.readableBytes());
                relayChannel.writeAndFlush(msg);
            } catch (Exception e) {
                log.info("发生异常啦");
            }
        } else {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (relayChannel.isActive()) {
            TrojanServerUtils.closeOnFlush(relayChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String message = cause.getMessage();
        Channel channel = ctx.channel();
        if (message.startsWith("远程主机强迫关闭了一个现有的连接") || message.startsWith("An existing connection was forcibly closed")
                || message.startsWith("Connection reset by peer")) {
            log.warn("{}，channel: {}", message, channel);
        } else {
            log.error("代理数据转发异常, channel: {}", channel, cause);
        }
        ctx.close();
    }
}
