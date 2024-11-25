package com.kosoft.trojanjava.codec;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * Trojan 协议消息
 * @see TrojanDecoder
 * @author hccake
 */
@Data
public class TrojanMessage {

    /**
     * hex(SHA224(password))
     */
    private String key;

    private TrojanRequest trojanRequest;

    private ByteBuf payload;
}
