package com.kosoft.trojanjava.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Trojan UdpPacket 解码为 DatagramPacket
 * @author hccake
 */
@Slf4j
public class TrojanUdpPacketDecoder extends ByteToMessageDecoder {



    public static DatagramPacket getDatagramPacket(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        /*
         * 获取 UDP 数据包部分，udp 的实际请求地址根据这里的 addr 和 port 走
         * +------+----------+----------+--------+---------+----------+
         * | ATYP | DST.ADDR | DST.PORT | Length |  CRLF   | Payload  |
         * +------+----------+----------+--------+---------+----------+
         * |  1   | Variable |    2     |   2    | X'0D0A' | Variable |
         * +------+----------+----------+--------+---------+----------+
         */
        TrojanAddressType dstAddrType = TrojanAddressType.valueOf(in.readByte());
        final String dstAddr = TrojanAddressDecoder.DEFAULT.decodeAddress(dstAddrType, in);
        final int dstPort = in.readUnsignedShort();
        log.info("udp 请求目标地址为：[{}:{}]", dstAddr, dstPort);

        // 数据长度
        int udpDataLength = in.readShort();
        log.info("udp data 长度为：{}", udpDataLength);

        // skip CRLF
        in.skipBytes(2);

        ByteBuf dataByteBuffer = Unpooled.directBuffer(udpDataLength);
        in.readBytes(dataByteBuffer);
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        return new DatagramPacket(dataByteBuffer, new InetSocketAddress(dstAddr, dstPort), (InetSocketAddress) socketAddress);
    }

    /**
     * <pre>
     * +------+----------+----------+--------+---------+----------+
     * | ATYP | DST.ADDR | DST.PORT | Length |  CRLF   | Payload  |
     * +------+----------+----------+--------+---------+----------+
     * |  1   | Variable |    2     |   2    | X'0D0A' | Variable |
     * +------+----------+----------+--------+---------+----------+
     * </pre>
     *
     * @param channelHandlerContext the {@link ChannelHandlerContext} which this
     *            {@link ByteToMessageDecoder} belongs to
     * @param byteBuf  the {@link Buffer} from which to read data
     */

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        DatagramPacket datagramPacket = getDatagramPacket(channelHandlerContext, byteBuf);
        channelHandlerContext.fireChannelRead(datagramPacket);
    }
}
