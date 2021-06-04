package com.learn.onlinemutiplechoosetest.ui.main;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;
import com.learn.onlinemutiplechoosetest.MainActivity;
import com.learn.onlinemutiplechoosetest.R;
import com.learn.onlinemutiplechoosetest.ui.roomTest.createdRooms.CreatedRoomFragment;
import com.learn.onlinemutiplechoosetest.ui.roomTest.roomNew.RoomBasicInfoDialog;
import com.learn.onlinemutiplechoosetest.ui.roomTest.roomNew.RoomNewFragment;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private Button btnCreateRoom;

    private TextInputEditText etRoomCode;
    private Button btnFind, btnViewCreatedRoom;
    public HomeFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        btnCreateRoom = root.findViewById(R.id.btn_hCreateRoom);
        btnCreateRoom.setOnClickListener(this);
        etRoomCode = root.findViewById(R.id.et_hRoomCode);

        btnFind = root.findViewById(R.id.btn_hFindRoom);

        btnFind.setOnClickListener(this);
        btnViewCreatedRoom = root.findViewById(R.id.btn_viewCreatedRoom);
        btnViewCreatedRoom.setOnClickListener(this);

//        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                Toast.makeText(getContext(), "ON BACK PRESSED", Toast.LENGTH_SHORT).show();
//            }
//        };
//        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_hCreateRoom: {
                FragmentManager manager = ((MainActivity) getContext()).getSupportFragmentManager();
                new RoomBasicInfoDialog().show(manager,"room-basic-dialog");
                break;
            }
            case R.id.btn_hFindRoom: {
                etRoomCode.setError(null);
                String code = etRoomCode.getText().toString().trim();
                if (!TextUtils.isEmpty(code)) {
                    ((MainActivity) getContext()).findRoomByRoomCode(code);
                    ((MainActivity) getContext()).closeInputMethod();
                } else {
                    etRoomCode.setError("Please enter code before find");

                }
                break;
            }
            case R.id.btn_viewCreatedRoom: {
                FragmentManager manager = ((MainActivity) getContext()).getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.nav_host_fragment, new CreatedRoomFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            }
        }
    }
}