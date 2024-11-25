package com.kosoft.trojanjava.schedule;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.kosoft.trojanjava.bean.UserTraffic;
import com.kosoft.trojanjava.config.CenterServerConfig;
import com.kosoft.trojanjava.service.User;
import com.kosoft.trojanjava.service.UserService;
import jakarta.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

@Configuration
public class TrafficSchedule {


    @Resource
    private UserService userService;

    @Resource
    private CenterServerConfig centerServerConfig;

    @Scheduled(fixedRate = 1000 * 30, initialDelay = 1000)
    public void trafficInfoSTask() {
        final String post = HttpUtil.post(centerServerConfig.getHost() + ":" + centerServerConfig.getPort() + "/api/updateUsed", JSON.toJSONString(Collections.EMPTY_LIST), 5000);
        List<UserTraffic> userTraffics = JSON.parseArray(post, UserTraffic.class);
        userService.refreshUserInfo(userTraffics);
    }

    @Scheduled(fixedDelay = 1000 * 60)
    public void uploadUserUsedFlow() {
        List<User> activeUser = userService.getActiveUser();
        List<Map<String,Object>> userTraffics = new ArrayList<>();
        for (User user : activeUser) {
            Map<String, Object> map = new HashMap<>();
            map.put("usedFlow", user.getCurrentUsedFlow().getAndSet(0));
            map.put("userId", user.getId());
            userTraffics.add(map);
        }
        final String post = HttpUtil.post(centerServerConfig.getHost() + ":" + centerServerConfig.getPort() + "/api/updateUsed", JSON.toJSONString(userTraffics), 30000);
        System.out.println("上报流量返回"+post);
        List<UserTraffic> userTrafficList = JSON.parseArray(post, UserTraffic.class);
        userService.refreshUserInfo(userTrafficList);
    }


}
