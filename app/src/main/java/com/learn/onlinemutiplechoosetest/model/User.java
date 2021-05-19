package com.learn.onlinemutiplechoosetest.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {

    private String userId;
    private String username;
    private String email;
    private String password;
    private String avatar;
    private String dateModified;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return
                Objects.equals(email, user.email) &&
                        Objects.equals(password, user.password);
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }
}
