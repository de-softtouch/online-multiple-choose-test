package com.learn.onlinemutiplechoosetest;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.learn.onlinemutiplechoosetest.model.Room;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private MainActivityViewModel mainActivityViewModel;

    private FirebaseStorage fStorage;
    private FirebaseFirestore fFirestore;
    private FirebaseAuth fAuth;
    private FirebaseDatabase fDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mainActivityViewModel.getCurrentUserInfo().observe(this, user -> {
            Log.d(TAG, "onCreate: " + user.toString());
        });

//        fStorage = FirebaseStorage.getInstance();
//        fDatabase = FirebaseDatabase.getInstance();

//        fDatabase
//                .getReference("rooms")
//                .child("5f619064-4022-40ac-a5a9-fb7943822310")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            Room value = snapshot.getValue(Room.class);
//                            Log.d(TAG, "onDataChange: " + value.toString());
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

    }
}