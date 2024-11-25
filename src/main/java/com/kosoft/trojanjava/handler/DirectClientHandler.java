package com.kosoft.trojanjava.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.concurrent.Promise;

public final class DirectClientHandler extends SimpleChannelInboundHandler<ByteBuf> {


	private final Promise<Channel> promise;

	public DirectClientHandler(Promise<Channel> promise) {
		this.promise = promise;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.pipeline().remove(this);
		promise.setSuccess(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		promise.setFailure(cause);
	}


	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
		channelHandlerContext.fireChannelRead(byteBuf);
	}
}
