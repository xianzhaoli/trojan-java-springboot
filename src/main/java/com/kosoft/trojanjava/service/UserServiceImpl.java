package com.kosoft.trojanjava.service;

import com.kosoft.trojanjava.bean.UserTraffic;
import com.kosoft.trojanjava.utils.Encipher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService{


    Map<String, User> map = new ConcurrentHashMap<>();

    @Override
    public User getByToken(String token) {
        return null;
    }

    @Override
    public boolean auth(String token) {
        final User user = map.get(token);
        if (user != null && user.getExpire().after(new Date())
                && user.getTraffic() != null && user.getUsedFlow() != null
                && user.getTraffic().compareTo(user.getUsedFlow()) > 0 ){
            return true;
        }
        return false;
    }

    @Override
    public void usedFlow(String token, int used) {
        map.get(token).getCurrentUsedFlow().addAndGet(used);
//        System.out.println("usedFlow: " + used);
    }


    @Override
    public void refreshUserInfo(List<UserTraffic> userTrafficList) {
        Set<String> keys = new HashSet<>();
        for (UserTraffic userTraffic : userTrafficList) {
            String encryptedAuthToken = Encipher.jdkSHA224(userTraffic.getAuthToken());
            keys.add(encryptedAuthToken);

            if (map.containsKey(userTraffic.getAuthToken())){
                User user = map.get(userTraffic.getAuthToken());
                user.setAuthToken(userTraffic.getAuthToken());
                user.setEncryptedAuthToken(encryptedAuthToken);
                user.setTraffic(userTraffic.getTotalData());
                user.setUsedFlow(userTraffic.getTotalUsedFlow());
                user.setExpire(userTraffic.getFailureTime());
            }else{
                User user = new User();
                user.setEncryptedAuthToken(encryptedAuthToken);
                user.setAuthToken(userTraffic.getAuthToken());
                user.setTraffic(userTraffic.getTotalData());
                user.setUsedFlow(userTraffic.getTotalUsedFlow());
                user.setExpire(userTraffic.getFailureTime());
                user.setId(userTraffic.getUserId());
                map.put(user.getEncryptedAuthToken(), user);
            }
        }
        for (String s : map.keySet()) {
            if (!keys.contains(s)){
                map.remove(s);
            }
        }
    }

    @Override
    public List<User> getActiveUser() {
        return map.values().stream().filter(user -> user.getCurrentUsedFlow().get() > 0).collect(Collectors.toList());
    }

}
