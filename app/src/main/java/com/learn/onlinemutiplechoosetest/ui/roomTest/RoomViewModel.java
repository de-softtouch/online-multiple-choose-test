package com.learn.onlinemutiplechoosetest.ui.roomTest;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.learn.onlinemutiplechoosetest.model.Quiz;
import com.learn.onlinemutiplechoosetest.model.Room;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class RoomViewModel extends ViewModel {

    private MutableLiveData<Room> roomNew = new MutableLiveData<>();
    private MutableLiveData<List<Quiz>> quizzes = new MutableLiveData<>();

    public RoomViewModel() {
        roomNew.setValue(new Room());
        quizzes.setValue(new ArrayList<>());

    }

}
