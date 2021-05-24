package com.learn.onlinemutiplechoosetest.ui.roomTest.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.FirebaseDatabase;
import com.learn.onlinemutiplechoosetest.MainActivity;
import com.learn.onlinemutiplechoosetest.R;
import com.learn.onlinemutiplechoosetest.model.Quiz;
import com.learn.onlinemutiplechoosetest.model.Room;
import com.learn.onlinemutiplechoosetest.ui.main.HomeFragment;
import com.learn.onlinemutiplechoosetest.ui.quiz.QuizNewDialog;
import com.learn.onlinemutiplechoosetest.ui.roomTest.RoomViewModel;

import java.util.List;

public class CreateRoomFragment extends Fragment implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private Button btnAddQuiz, btnSave;
    private FragmentManager fragmentManager;
    private RoomViewModel roomViewModel;
    private TextView tvRoomName;

    public CreateRoomFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = ((MainActivity) getContext()).getSupportFragmentManager();
        roomViewModel = new ViewModelProvider(getActivity()).get(RoomViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_create_room, container, false);
        btnAddQuiz = root.findViewById(R.id.btn_addQuiz);
        btnAddQuiz.setOnClickListener(this);
        tvRoomName = root.findViewById(R.id.tv_nNewRoomName);
        btnSave = root.findViewById(R.id.btn_saveRoomNew);
        btnSave.setOnClickListener(this);
        new RoomBasicInfoDialog()
                .show(((MainActivity) getContext())
                        .getSupportFragmentManager(), "Basic Info");

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addQuiz: {
                new QuizNewDialog().show(fragmentManager, "HIHI");
                break;
            }
            case R.id.btn_saveRoomNew: {
                //
                Room room = roomViewModel.getRoomNew().getValue();
                List<Quiz> quizzes = roomViewModel.getQuizzes().getValue();
                room.setQuizzes(quizzes);

                new AlertDialog.Builder(getContext())
                        .setTitle("Are your sure to save")
                        .setPositiveButton("Save", ((dialog, which) -> {
                            saveRoomToFDatabase(room);
                            ((MainActivity) getContext())
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.nav_host_fragment, new HomeFragment())
                                    .commit();
                            dialog.dismiss();
                        }))
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();

                break;
            }
        }
    }

    public void saveRoomToFDatabase(Room room) {
        FirebaseDatabase
                .getInstance()
                .getReference("rooms")
                .child(room.getId())
                .setValue(room)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Your Room has been saved!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "saveRoomToFDatabase: " + room.toString());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Sorry something went wrong!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "saveRoomToFDatabase: ", e.fillInStackTrace());
                });
    }
}