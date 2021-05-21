package com.learn.onlinemutiplechoosetest.utils;

import com.learn.onlinemutiplechoosetest.model.Answer;
import com.learn.onlinemutiplechoosetest.model.Quiz;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RoomUtils {

    public HashMap<Quiz, Answer> toAnswerMap(List<Quiz> quizzes) {
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

    public double caculateScore(HashMap<Quiz, Answer> trueAnserMap, HashMap<Integer, Integer> answerMap) {
        //quiz number,answer number
        double finalScore = 0.0;
        Set<Quiz> quizzes = trueAnserMap.keySet();
        for (Quiz quiz :
                quizzes) {
            
        }
        return 0;
    }

    ;;
}
