package com.learn.onlinemutiplechoosetest.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Room {

    private String adminId;
    private String code;
    private String id;
    private String name;
    private String password;
    private boolean usePassword;
    private int time;
    private List<Quiz> quizzes;

}
