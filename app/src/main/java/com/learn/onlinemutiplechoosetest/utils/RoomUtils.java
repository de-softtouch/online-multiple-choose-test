package com.learn.onlinemutiplechoosetest.utils;

import com.learn.onlinemutiplechoosetest.model.Answer;
import com.learn.onlinemutiplechoosetest.model.Quiz;

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
        if(userMap.size()<=0){
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

    ;;
}
