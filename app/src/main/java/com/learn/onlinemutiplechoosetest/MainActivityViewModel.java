package com.learn.onlinemutiplechoosetest;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.learn.onlinemutiplechoosetest.model.User;
import com.learn.onlinemutiplechoosetest.utils.UserUtils;

import lombok.Getter;

@Getter
public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<User> currentUserInfo = new MutableLiveData<>();

    public MainActivityViewModel() {
        FirebaseFirestore fFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fFirestore
                .collection("users")
                .document(fAuth.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                        User userInfo = UserUtils.toUser(documentSnapshot.getData());
                        this.currentUserInfo.setValue(userInfo);
                    }
                });
    }
}
