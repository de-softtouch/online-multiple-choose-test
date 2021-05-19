package com.learn.onlinemutiplechoosetest.utils;

import com.learn.onlinemutiplechoosetest.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserUtils {

    public HashMap<String, String> toMap(User user) {
        HashMap<String, String> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("email", user.getEmail());
        map.put("avatar", user.getAvatar());
        return map;
    }

    public static User toUser(Map<String, Object> map) {
        User user
                = new User();
        user.setUserId((String) map.get("userId"));
        user.setUsername((String) map.get("username"));
        user.setEmail((String) map.get("email"));
        user.setAvatar((String) map.get("avatar"));
        return user;
    }
}
