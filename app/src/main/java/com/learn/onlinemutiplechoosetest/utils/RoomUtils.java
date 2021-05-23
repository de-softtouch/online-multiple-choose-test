package com.learn.onlinemutiplechoosetest.utils;

import com.learn.onlinemutiplechoosetest.model.Answer;
import com.learn.onlinemutiplechoosetest.model.Quiz;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RoomUtils {

    public static HashMap<Quiz, Answer> toAnswerMap(List<Quiz> quizzes) {
        HashMap<Quiz, Answer> map = new HashMap<>();
        for (Quiz quiz :
                quizzes) {
            for (Answer answer :
                    quiz.getAnswers()) {
                if (answer.isTrue()) {
                    map.put(quiz, answer);
                    break;
                }
            }
        }
        return map;
    }

    public static double caculateScore(HashMap<Quiz, Answer> main, HashMap<Quiz, Answer> userMap) {
        if (userMap.size() <= 0) {
            return 0.0;
        }
        double finalScore = 0.0;
        Set<Quiz> quizzes = main.keySet();
        for (Quiz quiz :
                quizzes) {
            if (userMap.get(quiz) != null) {
                if (userMap.get(quiz).equals(main.get(quiz))) {
                    finalScore += quiz.getScore();
                }
            }

        }
        return finalScore;
    }

    public static void addNewQuiz(List<Quiz> quizzes, Quiz quiz) {
        List<Answer> answers = quiz.getAnswers();

        for (int i = 0; i < quiz.getAnswers().size(); i++) {
            answers.get(i).setCode(i + 1);
        }

        Collections.shuffle(answers);
        quiz.setAnswers(answers);

        if (quizzes.size() == 0) {
            quiz.setQuizNumber(1);
        } else {
            quiz.setQuizNumber(quizzes.size() + 1);
        }

        quizzes.add(quiz);


    }

}
