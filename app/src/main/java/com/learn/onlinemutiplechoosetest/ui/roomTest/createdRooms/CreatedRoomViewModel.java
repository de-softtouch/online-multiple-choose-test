package com.learn.onlinemutiplechoosetest.ui.roomTest.createdRooms;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.learn.onlinemutiplechoosetest.model.Room;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatedRoomViewModel extends ViewModel {

    private MutableLiveData<List<Room>> createdRooms = new MutableLiveData<>();

    public CreatedRoomViewModel() {

        createdRooms.setValue(new ArrayList<>());
    }

    @Override
    protected void onCleared() {
        Log.d("HIHI", "onCleared: "+"ON CLEAR");
        super.onCleared();
    }
}
