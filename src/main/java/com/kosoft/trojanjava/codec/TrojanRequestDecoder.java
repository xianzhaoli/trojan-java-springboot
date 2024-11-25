package com.kosoft.trojanjava.codec;

import io.netty.buffer.ByteBuf;

/**
 * @author hccake
 */

public interface TrojanRequestDecoder {

    TrojanRequestDecoder DEFAULT = in -> {
        // CMD
        // o CONNECT X'01'
        // o UDP ASSOCIATE X'03'
        byte cmdByte = in.readByte();
        TrojanCommandType cmdType = TrojanCommandType.valueOf(cmdByte);

        // ATYP address type of following address
        // o IP V4 address: X'01'
        // o DOMAINNAME: X'03'
        // o IP V6 address: X'04'
        final TrojanAddressType dstAddrType = TrojanAddressType.valueOf(in.readByte());
        final String dstAddr = TrojanAddressDecoder.DEFAULT.decodeAddress(dstAddrType, in);
        final int dstPort = in.readUnsignedShort();

        TrojanRequest trojanRequest = new TrojanRequest();
        trojanRequest.setCommandType(cmdType);
        trojanRequest.setAddressType(dstAddrType);
        trojanRequest.setDstAddr(dstAddr);
        trojanRequest.setDstPort(dstPort);

        return trojanRequest;
    };


    TrojanRequest decodeRequest(ByteBuf in) throws Exception;
}
