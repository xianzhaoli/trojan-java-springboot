package com.kosoft.trojanjava.codec;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.util.NetUtil;

import java.nio.charset.StandardCharsets;

/**
 * Decodes a Trojan address field into its string representation.
 * @see TrojanAddressDecoder
 */
public interface TrojanAddressDecoder {

    TrojanAddressDecoder DEFAULT = new TrojanAddressDecoder() {

        @SuppressWarnings("all")
        private static final int IPv6_LEN = 16;

        @Override
        public String decodeAddress(TrojanAddressType addrType, ByteBuf in) {
            int readableBytes = in.readableBytes();
            if (addrType == TrojanAddressType.IPv4) {
                if (readableBytes < 4) {
                    return null;
                }
                return NetUtil.intToIpAddress(in.readInt());
            }
            if (addrType == TrojanAddressType.DOMAIN) {
                if (readableBytes < 1) {
                    return null;
                }
                final int length = in.getUnsignedByte(in.readerIndex());
                if (readableBytes - 1 < length) {
                    return null;
                }
                in.skipBytes(1);
                return in.readCharSequence(length, StandardCharsets.US_ASCII).toString();
            }
            if (addrType == TrojanAddressType.IPv6) {
                if (readableBytes < IPv6_LEN) {
                    return null;
                }
                byte[] tmp = new byte[IPv6_LEN];
                in.readBytes(tmp, 0, tmp.length);
                return NetUtil.bytesToIpAddress(tmp);
            } else {
                throw new DecoderException("unsupported address type: " + (addrType.byteValue() & 0xFF));
            }
        }
    };

    /**
     * Decodes a Trojan address field into its string representation.
     *
     * @param addrType the type of the address
     * @param in the input buffer which contains the SOCKS5 address field at its reader index
     * @return the address or {@code null} if not enough bytes are readable yet.
     */
    String decodeAddress(TrojanAddressType addrType, ByteBuf in) throws Exception;
}
