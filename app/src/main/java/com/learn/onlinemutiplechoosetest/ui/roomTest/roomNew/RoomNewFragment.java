package com.learn.onlinemutiplechoosetest.ui.roomTest.roomNew;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.learn.onlinemutiplechoosetest.MainActivity;
import com.learn.onlinemutiplechoosetest.R;
import com.learn.onlinemutiplechoosetest.model.Quiz;
import com.learn.onlinemutiplechoosetest.model.Room;
import com.learn.onlinemutiplechoosetest.ui.main.HomeFragment;
import com.learn.onlinemutiplechoosetest.ui.quiz.QuizNewDialog;
import com.learn.onlinemutiplechoosetest.ui.roomTest.roomTst.QuizViewAdapter;
import com.learn.onlinemutiplechoosetest.ui.roomTest.roomTst.RoomViewModel;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RoomNewFragment extends Fragment implements View.OnClickListener {

    public static final String ROOM_NAME_KEY = "roomName";
    public static final String ROOM_TIME_KEY = "roomTime";
    private String roomName;
    private int roomTime;

    private final String TAG = getClass().getSimpleName();
    private RoomViewModel roomViewModel;
    private TextView tvRoomName, tvTime;
    private FloatingActionButton fab, fabSave, fabPasswordConfig;

    private QuizViewAdapter adapter;
    private RecyclerView recyclerView;


    public RoomNewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomName = getArguments().getString(ROOM_NAME_KEY);
            roomTime = getArguments().getInt(ROOM_TIME_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = getViews(inflater, container);
        roomViewModel = new ViewModelProvider(getActivity()).get(RoomViewModel.class);

        Room newRoom = new Room();
        newRoom.setId(UUID.randomUUID().toString());
        newRoom.setName(roomName);
        newRoom.setTime(roomTime);
        newRoom.setAdminId(FirebaseAuth.getInstance().getUid());
        roomViewModel.getRoomNew().setValue(newRoom);

        //
        tvRoomName.setText(roomName);
        tvTime.setText(String.valueOf(roomTime));
        registerQuizzesChange();
        return root;
    }

    private void registerQuizzesChange() {
        roomViewModel.getQuizzes().observe(getActivity(), quizzes -> {
            if (quizzes != null && quizzes.size() > 0) {
                adapter = new QuizViewAdapter(getContext(), quizzes);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            }
        });
    }


    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = ((MainActivity) getContext()).getSupportFragmentManager();
        switch (v.getId()) {
            case R.id.fab_addQuiz: {
                new QuizNewDialog().show(fragmentManager, "HIHI");
                break;
            }
            case R.id.fab_save: {
                Room room = roomViewModel.getRoomNew().getValue();
                room.setCode(generateValidCode());
                List<Quiz> quizzes = roomViewModel.getQuizzes().getValue();
                room.setQuizzes(quizzes);


                if (quizzes != null && quizzes.size() == 0) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("You have not any questions in your room")
                            .setMessage("Please fill at least 3 questions before save!")
                            .setPositiveButton("Got it!", ((dialog, which) -> {
                                dialog.dismiss();
                            }))
                            .show();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Are your sure to save")
                            .setPositiveButton("Save", ((dialog, which) -> {
                                saveRoomToFDatabase(room);
                                fragmentManager
                                        .beginTransaction()
                                        .replace(R.id.nav_host_fragment, new HomeFragment())
                                        .commit();
                                dialog.dismiss();
                            }))
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();

                }

                break;
            }
            case R.id.fab_password_config: {
                Room newRoom = roomViewModel.getRoomNew().getValue();
                Dialog passwordConfigDialog = new Dialog(getContext());
                passwordConfigDialog.setContentView(LayoutInflater.from(getContext()).inflate(R.layout.password_config_dialog, null, false));
                passwordConfigDialog.show();
                TextInputEditText et = passwordConfigDialog.findViewById(R.id.password);
                if (newRoom.getPassword() != null) {
                    et.setText(newRoom.getPassword());
                }
                Switch aSwitch = passwordConfigDialog.findViewById(R.id.switch1);
                if (newRoom.isUsePassword()) {
                    aSwitch.setChecked(true);
                }
                Button btnSavePassword = passwordConfigDialog.findViewById(R.id.btn_savePassword);
                Button btnCancel = passwordConfigDialog.findViewById(R.id.btn_cancelSavePass);
                btnSavePassword.setOnClickListener(view -> {
                    if (aSwitch.isChecked()) {
                        if (TextUtils.isEmpty(et.getText().toString().trim())) {
                            et.setError("Your password is blank!");
                            return;
                        }
                        newRoom.setUsePassword(true);
                        newRoom.setPassword(et.getText().toString().trim());

                        Toast.makeText(getContext(), "Your room password was saved!", Toast.LENGTH_SHORT).show();

                    } else {
                        newRoom.setUsePassword(false);
                    }
                    passwordConfigDialog.dismiss();
                });
                btnCancel.setOnClickListener(v1 -> passwordConfigDialog.dismiss());

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
                    generateRoomCode(room);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Sorry something went wrong!", Toast.LENGTH_SHORT).show());

    }

    public void generateRoomCode(Room room) {
        HashMap<String, Object> value = new HashMap<>();
        value.put("roomId", room.getId());

        FirebaseDatabase.getInstance()
                .getReference("room-with-code")
                .child(room.getCode())
                .setValue(value);
    }

    @NonNull
    private String generateValidCode() {
        String code;
        String s1 = UUID.randomUUID().toString();
        String a = s1.substring(0, 3);
        String s = String.valueOf(new Date().getTime());
        String b = s.substring(s.length() - 3);
        String c = s1.substring(s1.length() - 3);
        code = a + "-" + b + "-" + c;
        return code;
    }

    @NonNull
    private View getViews(LayoutInflater inflater, ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_room_new, container, false);
        fab = root.findViewById(R.id.fab_addQuiz);
        fab.setOnClickListener(this);
        fabSave = root.findViewById(R.id.fab_save);
        fabSave.setOnClickListener(this);
        fabPasswordConfig = root.findViewById(R.id.fab_password_config);
        fabPasswordConfig.setOnClickListener(this);
        tvRoomName = root.findViewById(R.id.tv_nNewRoomName);

        tvTime = root.findViewById(R.id.tv_233232);
        recyclerView = root.findViewById(R.id.rcv_quizzesNew);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        return root;
    }
}