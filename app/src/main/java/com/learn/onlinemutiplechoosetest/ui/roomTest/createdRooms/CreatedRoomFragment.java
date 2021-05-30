package com.learn.onlinemutiplechoosetest.ui.roomTest.createdRooms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.onlinemutiplechoosetest.R;
import com.learn.onlinemutiplechoosetest.model.Room;

import java.util.ArrayList;
import java.util.List;


public class CreatedRoomFragment extends Fragment {

    private static final String TAG = "TAG";
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private CreatedRoomViewModel viewModel;
    private RoomsCreatedAdapter adapter;
    private RecyclerView recyclerView;

    private ProgressBar progressBar;

    public CreatedRoomFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        viewModel = new ViewModelProvider(getActivity()).get(CreatedRoomViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_created_room, container, false);
        progressBar = root.findViewById(R.id.progressBar2);
        recyclerView = root.findViewById(R.id.rcv);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false));
        database.getReference("rooms")
                .orderByChild("adminId")
                .equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            progressBar.setVisibility(View.VISIBLE);

                            List<Room> rooms = new ArrayList<>();
                            for (DataSnapshot roomSnapshot :
                                    snapshot.getChildren()) {
                                rooms.add(roomSnapshot.getValue(Room.class));
                            }
                            viewModel.getCreatedRooms().setValue(rooms);
                        }else{
                            progressBar.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);

                    }
                });

        viewModel.getCreatedRooms().observe(getViewLifecycleOwner(), rooms -> {
            if (!rooms.isEmpty()) {
                adapter = new RoomsCreatedAdapter(rooms, getContext());
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);

            }

        });


        return root;
    }


}