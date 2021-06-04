package com.learn.onlinemutiplechoosetest.ui.roomTest.editRoom;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.learn.onlinemutiplechoosetest.R;
import com.learn.onlinemutiplechoosetest.model.Quiz;
import com.learn.onlinemutiplechoosetest.model.Room;
import com.learn.onlinemutiplechoosetest.ui.roomTest.createdRooms.CreatedRoomViewModel;
import com.learn.onlinemutiplechoosetest.ui.roomTest.roomTst.QuizViewAdapter;

import java.util.List;

public class EditRoomFragment extends Fragment {

    private static final String TAG = EditRoomFragment.class.getSimpleName();
    public static String ROOM_ID_PARAM = "roomID";
    private int roomIndex;
    private CreatedRoomViewModel viewModel;
    private List<Quiz> quizzes;
    private Button btnUpdate;

    public EditRoomFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomIndex = getArguments().getInt(ROOM_ID_PARAM);
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
        btnUpdate = root.findViewById(R.id.button3);
        btnUpdate.setOnClickListener(v -> {
            Room room = viewModel.getCreatedRooms().getValue().get(roomIndex);
            FirebaseDatabase.getInstance()
                    .getReference("rooms")
                    .child(room.getId())
                    .setValue(room)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(getContext(), "update success", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "onCreateView: "+e.toString()); 
                    });
            Log.d("HIHI", "onCreateView: " + room);
        });
        RecyclerView recyclerView = root.findViewById(R.id.rcv2);
        List<Room> rooms = viewModel.getCreatedRooms().getValue();
        Room room = rooms.get(roomIndex);
        quizzes = room.getQuizzes();
        QuizViewAdapter quizViewAdapter = new QuizViewAdapter(getContext(), quizzes);
        quizViewAdapter.setShowEditOptionMenu(true);
        recyclerView.setAdapter(quizViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        return root;
    }
}