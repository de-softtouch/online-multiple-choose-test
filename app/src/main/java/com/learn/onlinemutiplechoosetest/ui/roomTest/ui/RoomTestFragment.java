package com.learn.onlinemutiplechoosetest.ui.roomTest.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.learn.onlinemutiplechoosetest.MainActivity;
import com.learn.onlinemutiplechoosetest.MainActivityViewModel;
import com.learn.onlinemutiplechoosetest.R;
import com.learn.onlinemutiplechoosetest.model.Answer;
import com.learn.onlinemutiplechoosetest.model.Quiz;
import com.learn.onlinemutiplechoosetest.model.Room;
import com.learn.onlinemutiplechoosetest.model.User;
import com.learn.onlinemutiplechoosetest.ui.main.HomeFragment;
import com.learn.onlinemutiplechoosetest.ui.roomTest.RoomViewModel;
import com.learn.onlinemutiplechoosetest.utils.RoomUtils;

import java.util.Date;
import java.util.HashMap;

public class RoomTestFragment extends Fragment implements QuizAdapter.OnAnswerCheckedListener, View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private QuizAdapter adapter;
    private RecyclerView recyclerView;
    private Room currentRoom;
    private MainActivityViewModel viewModel;
    private FirebaseDatabase fDatabase;
    private RoomViewModel roomViewModel;

    long seconds;
    private Handler handler;

    private TextView tvTimeCountDown, tvTitle;
    private Button btnSubmit;

//    private HashMap<Quiz, Answer> map = new HashMap<>();

    public RoomTestFragment() {
    }

    public static RoomTestFragment newInstance() {
        return new RoomTestFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        roomViewModel = new ViewModelProvider(getActivity()).get(RoomViewModel.class);
        if (roomViewModel.getMap().getValue() == null) {
            roomViewModel.getMap().setValue(new HashMap<>());
        }
        fDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = getViews(inflater, container);
        viewModel.getRoomMutableLiveData().observe(getActivity(), room -> {
            if (room != null) {
                this.currentRoom = room;
                Log.d(TAG, "onCreateView: " + currentRoom.toString());
                tvTitle.setText(room.getName());
                tvTimeCountDown.setText(formatTime(room.getTime()));
                adapter = new QuizAdapter(getContext(), currentRoom.getQuizzes());
                adapter.setOnAnswerCheckedListener(this);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(adapter);
                if (room.getTime() > 0) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Are you ready?")
                            .setPositiveButton("Ready", ((dialog, which) -> {
                                dialog.dismiss();
                                countDownTime();
                            }))
                            .setNegativeButton("Not Ready", ((dialog, which) -> {
                                FragmentManager sFm = ((MainActivity) getContext()).getSupportFragmentManager();
                                sFm.beginTransaction()
                                        .replace(R.id.nav_host_fragment, new HomeFragment())
                                        .commit();
                            }))
                            .setCancelable(false)
                            .show();
                }
            }
        });

        return root;
    }

    @NonNull
    private View getViews(LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_room_test, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        tvTimeCountDown = root.findViewById(R.id.tv_time);
        tvTitle = root.findViewById(R.id.tv_roomName);
        btnSubmit = root.findViewById(R.id.btn_submitAnswer);
        btnSubmit.setOnClickListener(this);
        ImageButton btnBack = root.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        return root;
    }

    void countDownTime() {
        seconds = currentRoom.getTime() * 60;
        handler = new Handler();
        handler.postDelayed(runnable, 0);
    }

    private String formatTime(int min) {
        int sec = min * 60;
        int minutes = (int) (sec / 60);
        int s = (int) (sec % 60);
        return String.format("%d:%02d", minutes, s);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seconds -= 1;
            int minutes = (int) (seconds / 60);
            int s = (int) (seconds % 60);
            String format = String.format("%d:%02d", minutes, s);
            tvTimeCountDown.setText(format);
            if (seconds == 0) {
                handler.removeCallbacks(this::run);
                showTimeUpDialog();
                return;
            }
            handler.postDelayed(runnable, 1000);
        }
    };

    void showTimeUpDialog() {
        preventUserSubmitAgain();
        new AlertDialog.Builder(getContext())
                .setTitle("Your time is up")
                .setMessage("Your answer has been submitted")
                .setPositiveButton("Got it", ((dialog, which) -> {
                    dialog.dismiss();
                    scoreDialog();
                    return;
                })).show();
    }

    @Override
    public void onAnswerChecked(boolean isChecked, Quiz quiz, Answer answer) {
        if (isChecked) {
            roomViewModel.getMap().getValue().put(quiz, answer);
        } else {
            roomViewModel.getMap().getValue().remove(quiz);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submitAnswer: {
                scoreDialog();
                roomViewModel.getIsSubmitted().setValue(true);
                User user = viewModel.getCurrentUserInfo().getValue();
                String roomID = currentRoom.getId();
                HashMap<String, Object> map = new HashMap<>();
                map.put("userId", user.getUserId());
                map.put("username", user.getUsername());
                map.put("score", getScore());
                map.put("time", new Date().toString());
                //
                fDatabase
                        .getReference("rooms-and-users")
                        .child(roomID)
                        .child(user.getUserId())
                        .setValue(map);
                break;
            }
            case R.id.btn_back: {
             if(roomViewModel.getIsSubmitted().getValue()==false){
                    new AlertDialog.Builder(getContext())
                        .setTitle("Are you sure to exit test?")
                        .setMessage("Your answer will not be submitted!")
                        .setPositiveButton("Ok", ((dialog, which) -> {
                            ((MainActivity) getContext())
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.nav_host_fragment, new HomeFragment())
                                    .commit();
                        }))
                        .setNegativeButton("Cancel", ((dialog, which) -> {
                            dialog.dismiss();
                        }))
                        .show();
             }else{
                 ((MainActivity) getContext())
                         .getSupportFragmentManager()
                         .beginTransaction()
                         .replace(R.id.nav_host_fragment, new HomeFragment())
                         .commit();
                 roomViewModel.getIsSubmitted().setValue(false);
             }
                break;
            }
        }
    }

    private void scoreDialog() {
        preventUserSubmitAgain();
        new AlertDialog.Builder(getContext())
                .setTitle("Your answers have been submitted")
                .setMessage("Score: " + getScore())
                .setPositiveButton("OK", ((dialog, which) -> {
                    dialog.dismiss();
                })).show();
    }

    private void preventUserSubmitAgain() {
        btnSubmit.setClickable(false);
        btnSubmit.setBackgroundResource(R.color.blueberry_soda);
        handler.removeCallbacks(runnable);
    }

    public double getScore() {
        return RoomUtils.caculateScore(RoomUtils.toAnswerMap(currentRoom.getQuizzes()), roomViewModel.getMap().getValue());
    }
}