package com.learn.onlinemutiplechoosetest.ui.roomTest.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.learn.onlinemutiplechoosetest.MainActivity;
import com.learn.onlinemutiplechoosetest.R;
import com.learn.onlinemutiplechoosetest.model.Room;
import com.learn.onlinemutiplechoosetest.ui.main.HomeFragment;
import com.learn.onlinemutiplechoosetest.ui.roomTest.RoomViewModel;

import java.util.UUID;

public class RoomBasicInfoDialog extends DialogFragment implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private RoomViewModel roomViewModel;
    private TextInputEditText etRoomName, etRoomTime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);
        roomViewModel = new ViewModelProvider(getActivity()).get(RoomViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_room_info, container, false);
        ImageButton btnClose = root.findViewById(R.id.btn_closeCreateRoom);
        etRoomName = root.findViewById(R.id.et_nRoomName);
        etRoomTime = root.findViewById(R.id.et_nRoomTime);
        Button btnSave = root.findViewById(R.id.btn_saveRoomInfo);
        btnSave.setOnClickListener(this);
        btnClose.setOnClickListener(this);

        return root;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_closeCreateRoom: {
                this.dismiss();
                ((MainActivity) getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, new HomeFragment())
                        .commit();
                break;
            }
            case R.id.btn_saveRoomInfo: {
                String roomName = etRoomName.getText().toString().trim();
                String roomTime = etRoomTime.getText().toString().trim();
                if (TextUtils.isEmpty(roomName)) {
                    etRoomName.setError("Your room name is empty");
                    return;
                }
                if (TextUtils.isEmpty(roomTime)) {
                    etRoomTime.setError("");
                    return;
                }
                Room room = new Room();
                room.setId(UUID.randomUUID().toString());
                room.setUsePassword(false);
                room.setName(roomName);
                room.setTime(Integer.parseInt(roomTime));
                room.setAdminId(FirebaseAuth.getInstance().getUid());

                roomViewModel.getRoomNew().setValue(room);
                Log.d(TAG, "onCreateRoom: "+room.toString());
                this.dismiss();
            }
        }
    }
}
