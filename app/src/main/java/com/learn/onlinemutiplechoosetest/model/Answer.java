package com.learn.onlinemutiplechoosetest.model;

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

public class Answer {
    private int answerNumber;
    private String answerText;
    private boolean isTrue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer = (Answer) o;
        return answerNumber == answer.answerNumber &&
                isTrue == answer.isTrue &&
                Objects.equals(answerText, answer.answerText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(answerNumber, answerText, isTrue);
    }
}
