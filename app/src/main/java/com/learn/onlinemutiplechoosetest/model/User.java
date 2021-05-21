package com.learn.onlinemutiplechoosetest.model;

import androidx.annotation.NonNull;

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
    private String provider;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return
                Objects.equals(email, user.email) &&
                        Objects.equals(password, user.password);
    }



    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }
}
