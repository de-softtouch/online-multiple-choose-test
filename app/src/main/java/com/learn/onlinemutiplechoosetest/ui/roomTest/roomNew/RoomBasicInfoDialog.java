package com.learn.onlinemutiplechoosetest.ui.roomTest.roomNew;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.learn.onlinemutiplechoosetest.MainActivity;
import com.learn.onlinemutiplechoosetest.R;
import com.learn.onlinemutiplechoosetest.ui.main.HomeFragment;
import com.learn.onlinemutiplechoosetest.ui.roomTest.roomTst.RoomViewModel;

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
        MainActivity mainActivity = (MainActivity) getContext();
        switch (v.getId()) {

            case R.id.btn_closeCreateRoom: {
                this.dismiss();
                mainActivity.getSupportFragmentManager()
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
                Bundle bundle = new Bundle();
                bundle.putString(RoomNewFragment.ROOM_NAME_KEY, roomName);
                bundle.putInt(RoomNewFragment.ROOM_TIME_KEY, Integer.parseInt(roomTime));

                mainActivity.closeDrawer();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, RoomNewFragment.class, bundle)
                        .addToBackStack(null)
                        .commit();
                this.dismiss();
            }
        }
    }
}
