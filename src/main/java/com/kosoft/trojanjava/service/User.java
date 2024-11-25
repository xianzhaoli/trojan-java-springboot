package com.kosoft.trojanjava.service;

import lombok.Data;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class User {

    private Long id;

    private String authToken;

    //加密后的token
    private String encryptedAuthToken;

    //总流量(bytes)
    private Long traffic;

    //已用流量(bytes)
    private Long usedFlow;

    //当前使用量（每次上报后清空）
    private AtomicLong currentUsedFlow = new AtomicLong(0);

    //失效时间
    private Date expire;

}
