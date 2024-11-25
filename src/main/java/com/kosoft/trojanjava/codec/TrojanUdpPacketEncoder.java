package com.kosoft.trojanjava.codec;

import inet.ipaddr.HostName;
import inet.ipaddr.HostNameException;
import inet.ipaddr.IPAddress;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * DatagramPacket 编码为 Trojan UdpPacket
 * @author hccake
 */
@Slf4j
public class TrojanUdpPacketEncoder extends MessageToByteEncoder<DatagramPacket> {





    private static TrojanAddressType getDstAddrType(String host) {
        HostName hostName = new HostName(host);
        try {
            hostName.validate();
            if (hostName.isAddress()) {
                IPAddress addr = hostName.asAddress();
                return addr.getIPVersion().isIPv4() ? TrojanAddressType.IPv4 : TrojanAddressType.IPv6;
            } else {
                return TrojanAddressType.DOMAIN;
            }
        } catch (HostNameException e) {
            log.error("解析 Host 信息失败：{}", host);
        }
        return null;
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
     * @param ctx the {@link ChannelHandlerContext} which this
     *            {@link ByteToMessageDecoder} belongs to
     * @param datagramPacket the {@link DatagramPacket} from which to write data
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, DatagramPacket datagramPacket, ByteBuf byteBuf) throws Exception {
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String host = remoteAddress.getHostString();
        int port = remoteAddress.getPort();
        TrojanAddressType dstAddrType = getDstAddrType(host);
        if (dstAddrType == null) {
            throw new DecoderException("error host: " + host);
        }
        // ATYP
        byteBuf.writeByte(dstAddrType.byteValue());
        TrojanAddressEncoder.DEFAULT.encodeAddress(dstAddrType, host, byteBuf);
        byteBuf.writeShort((short) port);

        ByteBuf content = datagramPacket.content();
        byteBuf.writeShort((short) content.readableBytes());
        byteBuf.writeByte((byte) '\r');
        byteBuf.writeByte((byte) '\n');
        byteBuf.writeBytes(content);
    }
}
