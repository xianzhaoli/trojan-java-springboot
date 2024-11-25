package com.kosoft.trojanjava.codec;

import cn.hutool.core.util.HexUtil;
import com.kosoft.trojanjava.exception.TrojanProtocolException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class TrojanDecoder extends ByteToMessageDecoder {

    private static final String ERROR_REQUEST_MESSAGE = "error request message";

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {

        // 读取 trojan 的密码
        int hashLength = 56;
        String trojanKey = in.readCharSequence(hashLength, StandardCharsets.UTF_8).toString();
        if (in.readByte() != '\r' || in.readByte() != '\n'){
            channelHandlerContext.close();
            return;
        }

        TrojanRequest trojanRequest = TrojanRequestDecoder.DEFAULT.decodeRequest(in);

        // 后续两个是 CRLF
        if (in.readByte() != '\r' || in.readByte() != '\n') {
            in.readerIndex(0);
            throw new TrojanProtocolException(ERROR_REQUEST_MESSAGE, in.copy());
        }

        // 载荷
        ByteBuf payload = null;
        int payloadLength = in.readableBytes();
        if (payloadLength > 0) {
            payload = channelHandlerContext.alloc().buffer(payloadLength);
            payload.writeBytes(in);
        }
        TrojanMessage trojanMessage = new TrojanMessage();
        trojanMessage.setKey(trojanKey);
        trojanMessage.setTrojanRequest(trojanRequest);
        trojanMessage.setPayload(payload);

        channelHandlerContext.fireChannelRead(trojanMessage);
    }
}
