package com.learn.onlinemutiplechoosetest.ui.roomTest.roomTst;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.learn.onlinemutiplechoosetest.model.Answer;
import com.learn.onlinemutiplechoosetest.model.Quiz;
import com.learn.onlinemutiplechoosetest.model.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

@Getter
public class RoomViewModel extends ViewModel {

    private MutableLiveData<Room> roomNew = new MutableLiveData<>();
    private MutableLiveData<List<Quiz>> quizzes = new MutableLiveData<>();
    private MutableLiveData<HashMap<Quiz, Answer>> map = new MutableLiveData<>();
    private MutableLiveData<Boolean> isSubmitted = new MutableLiveData<>();
    public RoomViewModel() {
        quizzes.setValue(new ArrayList<>());
        roomNew.setValue(null);
        isSubmitted.setValue(false);
    }

}
