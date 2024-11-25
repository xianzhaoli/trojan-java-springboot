package com.kosoft.trojanjava.exception;


import io.netty.buffer.ByteBuf;

public class TrojanProtocolException extends RuntimeException {

    private final ByteBuf content;

    public TrojanProtocolException(ByteBuf content) {
        this.content = content;
    }

    public TrojanProtocolException(String message, ByteBuf content) {
        super(message);
        this.content = content;
    }

    public ByteBuf getContent() {
        return content;
    }
}
