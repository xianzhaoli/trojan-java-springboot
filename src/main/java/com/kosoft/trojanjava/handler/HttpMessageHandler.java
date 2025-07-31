package com.kosoft.trojanjava.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
public class HttpMessageHandler extends SimpleChannelInboundHandler<HttpRequest> {



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) throws Exception {
        ByteBuf content = ctx.alloc().buffer();
        content.writeBytes("<h1>404 Not Found</h1>".getBytes(StandardCharsets.UTF_8));

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                content
        );
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
