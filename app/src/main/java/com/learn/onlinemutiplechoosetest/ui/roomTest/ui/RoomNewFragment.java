package com.learn.onlinemutiplechoosetest.ui.roomTest.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.learn.onlinemutiplechoosetest.MainActivity;
import com.learn.onlinemutiplechoosetest.R;
import com.learn.onlinemutiplechoosetest.model.Quiz;
import com.learn.onlinemutiplechoosetest.model.Room;
import com.learn.onlinemutiplechoosetest.ui.main.HomeFragment;
import com.learn.onlinemutiplechoosetest.ui.quiz.QuizNewDialog;
import com.learn.onlinemutiplechoosetest.ui.roomTest.RoomViewModel;

import java.util.List;

public class RoomNewFragment extends Fragment implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private Button btnAddQuiz, btnSave;
    private FragmentManager fragmentManager;
    private RoomViewModel roomViewModel;
    private TextView tvRoomName, tvTime;

    private QuizAdapter adapter;
    private RecyclerView recyclerView;

    private MainActivity mainActivity;

    public RoomNewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = ((MainActivity) getContext()).getSupportFragmentManager();
        roomViewModel = new ViewModelProvider(getActivity()).get(RoomViewModel.class);
        mainActivity = ((MainActivity) getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = getViews(inflater, container);

        registerQuizzesChange();
        return root;
    }

    private void registerQuizzesChange() {
        roomViewModel.getRoomNew().observe(getActivity(), room -> {
            if (room != null) {
                tvRoomName.setText(room.getName() + "");
                tvTime.setText(room.getTime() + "");
            } else {
                new RoomBasicInfoDialog()
                        .show(mainActivity.getSupportFragmentManager(), "Basic Info");

            }
        roomViewModel.getQuizzes().observe(getActivity(), quizzes -> {
            if (quizzes != null) {
                adapter = new QuizAdapter(getContext(), quizzes);
                recyclerView.setAdapter(adapter);
            }
        });
//        adapter = new QuizAdapter(getContext(), roomViewModel.getQuizzes().getValue());
//        recyclerView.setAdapter(adapter);




        });
    }

    @NonNull
    private View getViews(LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_room_new, container, false);
        btnAddQuiz = root.findViewById(R.id.btn_addQuiz);
        btnAddQuiz.setOnClickListener(this);
        tvRoomName = root.findViewById(R.id.tv_nNewRoomName);
        tvTime = root.findViewById(R.id.tv_233232);
        btnSave = root.findViewById(R.id.btn_saveRoomNew);
        btnSave.setOnClickListener(this);
        recyclerView = root.findViewById(R.id.rcv_quizzesNew);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

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