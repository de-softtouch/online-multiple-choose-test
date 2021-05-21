package com.learn.onlinemutiplechoosetest.ui.roomTest;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.onlinemutiplechoosetest.MainActivity;
import com.learn.onlinemutiplechoosetest.MainActivityViewModel;
import com.learn.onlinemutiplechoosetest.R;
import com.learn.onlinemutiplechoosetest.model.Room;
import com.learn.onlinemutiplechoosetest.ui.main.HomeFragment;

public class RoomTestFragment extends Fragment {

    private QuizAdapter adapter;
    private RecyclerView recyclerView;
    private Room currentRoom;
    private MainActivityViewModel viewModel;
    long seconds;
    private Handler handler;

    private TextView tvTimeCountDown, tvTitle;
    private Button btnSubmit;

    public RoomTestFragment() {
    }

    public static RoomTestFragment newInstance() {
        RoomTestFragment fragment = new RoomTestFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_room_test, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        tvTimeCountDown = root.findViewById(R.id.tv_time);
        tvTitle = root.findViewById(R.id.tv_roomName);
        btnSubmit = root.findViewById(R.id.btn_submitAnswer);
        viewModel.getRoomMutableLiveData().observe(getActivity(), room -> {
            if (room != null) {
                this.currentRoom = room;
                tvTitle.setText(room.getName());
                tvTimeCountDown.setText(formatTime(room.getTime()));
                adapter = new QuizAdapter(getContext(), currentRoom.getQuizzes());
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

    void countDownTime() {
        seconds = currentRoom.getTime() * 60;
        handler = new Handler();
        handler.postDelayed(runnable, 0);
    }

    private String formatTime(int min) {
        int sec = min * 60;
        int minutes = (int) (sec / 60);
        int s = (int) (sec % 60);
        String format = String.format("%d:%02d", minutes, s);
        return format;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seconds -= 1;
            int minutes = (int) (seconds / 60);
            int s = (int) (seconds % 60);
            String format = String.format("%d:%02d", minutes, s);
            tvTimeCountDown.setText(String.valueOf(format));
            if (seconds == 0) {
                handler.removeCallbacks(this::run);
                showTimeUpDialog();
                return;
            }
            handler.postDelayed(runnable, 1000);
        }
    };

    void showTimeUpDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Your time is up")
                .setMessage("Your answer has been submitted")
                .setPositiveButton("Got it", ((dialog, which) -> {
                    dialog.dismiss();
                })).show();
    }
}