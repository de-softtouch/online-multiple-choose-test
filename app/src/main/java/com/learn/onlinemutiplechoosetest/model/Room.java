package com.learn.onlinemutiplechoosetest.model;

import java.util.ArrayList;
import java.util.Date;
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

    private String id;
    private String adminId;
    private String name;
    private String code;
    private String password;
    private int maxUser;
    private Date dateToTest;
    private boolean usePassword;
    private int time;
    private List<Quiz> quizzes;
    private List<String> emailRegex;

    public void saveRoom() {

        Answer answer1 = new Answer(1, "write", false);
        Answer answer2 = new Answer(2, "writing", false);
        Answer answer3 = new Answer(3, "to write", true);
        List<Answer> answers1 = new ArrayList<>();
        answers1.add(answer1);
        answers1.add(answer2);
        answers1.add(answer3);
        Quiz quiz1 = new Quiz(1, "dfldslfhdklfhdf",1.0, answers1);


        Answer answer11 = new Answer(1, "play", false);
        Answer answer22 = new Answer(2, "playing", true);
        Answer answer33 = new Answer(3, "to play", false);
        List<Answer> answers11 = new ArrayList<>();
        answers11.add(answer11);
        answers11.add(answer22);
        answers11.add(answer33);
        Quiz quiz2 = new Quiz(1, "dfldslfhdklfhdf",1, answers11);


        List<Quiz> quizzes = new ArrayList<>();
        quizzes.add(quiz1);
        quizzes.add(quiz2);

        Room room = new Room();

    }

}
