package com.kosoft.trojanjava.bean;

import java.util.Date;

public class UserTraffic {

    private Long userId;

    private String authToken;

    private Long totalData;

    private Date failureTime;

    private Long totalUsedFlow;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Long getTotalData() {
        return totalData;
    }

    public void setTotalData(Long totalData) {
        this.totalData = totalData;
    }

    public Date getFailureTime() {
        return failureTime;
    }

    public void setFailureTime(Date failureTime) {
        this.failureTime = failureTime;
    }

    public Long getTotalUsedFlow() {
        return totalUsedFlow;
    }

    public void setTotalUsedFlow(Long totalUsedFlow) {
        this.totalUsedFlow = totalUsedFlow;
    }
}
