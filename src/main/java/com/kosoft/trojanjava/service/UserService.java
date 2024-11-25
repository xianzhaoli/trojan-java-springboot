package com.kosoft.trojanjava.service;

import com.kosoft.trojanjava.bean.UserTraffic;

import java.util.List;
import java.util.Map;

public interface UserService {


    User getByToken(String token);


    boolean auth(String token);


    void usedFlow(String token, int used);


    void refreshUserInfo(List<UserTraffic> userTrafficList);

    List<User> getActiveUser();
}
