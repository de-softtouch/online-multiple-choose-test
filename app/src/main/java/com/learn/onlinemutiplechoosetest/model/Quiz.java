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
public class Quiz {

    private int quizNumber;
    private String title;
    private double score;
    List<Answer> answers;

}
