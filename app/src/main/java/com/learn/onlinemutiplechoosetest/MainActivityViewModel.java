package com.learn.onlinemutiplechoosetest;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.learn.onlinemutiplechoosetest.model.Room;
import com.learn.onlinemutiplechoosetest.model.User;
import com.learn.onlinemutiplechoosetest.utils.UserUtils;

import java.util.Map;

import lombok.Getter;

@Getter
public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<User> currentUserInfo = new MutableLiveData<>();
    private MutableLiveData<Room> roomMutableLiveData = new MutableLiveData<>();

    public MainActivityViewModel() {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(fAuth.getUid())
                .addSnapshotListener((value, error) -> {
                    if (value != null && value.exists()) {
                        Map<String, Object> data = value.getData();
                        User user = UserUtils.toUser(data);
                        this.getCurrentUserInfo().setValue(user);
                        Log.d("MainActivityViewModel", "user info change: " + value.getData().toString());
                    }
                });
    }
}
