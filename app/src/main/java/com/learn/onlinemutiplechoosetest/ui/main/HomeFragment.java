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
import com.learn.onlinemutiplechoosetest.ui.roomTest.ui.RoomNewFragment;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private Button btnCreateRoom;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private TextInputEditText etRoomCode;
    private Button btnFind;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
                manager.beginTransaction()
                        .replace(R.id.nav_host_fragment, new RoomNewFragment())
                        .addToBackStack(null)
                        .commit();
            }
            case R.id.btn_hFindRoom: {
                etRoomCode.setError(null);
                String code = etRoomCode.getText().toString().trim();
                if (!TextUtils.isEmpty(code)) {
                    ((MainActivity) getContext()).findRoomByRoomCode(code);
                    ((MainActivity) getContext()).closeInputMethod();
                }else{
                    etRoomCode.setError("Please enter code before find");

                }
                break;
            }
        }
    }
}