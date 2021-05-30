package com.learn.onlinemutiplechoosetest.ui.roomTest.editRoom;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.learn.onlinemutiplechoosetest.R;
import com.learn.onlinemutiplechoosetest.model.Room;
import com.learn.onlinemutiplechoosetest.ui.roomTest.createdRooms.CreatedRoomViewModel;

import java.util.List;

public class EditRoomFragment extends Fragment {

    public static String ROOM_ID_PARAM = "roomID";
    private int roomId;
    private CreatedRoomViewModel viewModel;
    public EditRoomFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomId = getArguments().getInt(ROOM_ID_PARAM);
        }
        viewModel = new ViewModelProvider(getActivity()).get(CreatedRoomViewModel.class);
    }

    public static EditRoomFragment newInstance(Bundle bundle) {
        EditRoomFragment fragment = new EditRoomFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_room, container, false);
        List<Room> rooms = viewModel.getCreatedRooms().getValue();
        Room room = rooms.get(roomId);
        Log.d("hjkl", "onCreateView: "+room.toString());
        return root;
    }
}