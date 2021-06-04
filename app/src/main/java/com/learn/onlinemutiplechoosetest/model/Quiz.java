package com.learn.onlinemutiplechoosetest.model;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quiz quiz = (Quiz) o;
        return quizNumber == quiz.quizNumber &&
                Double.compare(quiz.score, score) == 0 &&
                Objects.equals(title, quiz.title) &&
                Objects.equals(answers, quiz.answers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quizNumber, title, score, answers);
    }

}
