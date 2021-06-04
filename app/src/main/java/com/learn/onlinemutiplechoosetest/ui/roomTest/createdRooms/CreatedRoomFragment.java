package com.learn.onlinemutiplechoosetest.ui.roomTest.createdRooms;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

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


public class CreatedRoomFragment extends Fragment implements RoomsCreatedAdapter.OnPopupMenuItemClick {

    private static final String TAG = "TAG";
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private CreatedRoomViewModel viewModel;
    private RoomsCreatedAdapter adapter;
    private RecyclerView recyclerView;
    private List<Room> rooms;
    private ProgressBar progressBar;

    public CreatedRoomFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(CreatedRoomViewModel.class);
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
                        } else {
                            viewModel.getCreatedRooms().setValue(null);
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
                this.rooms = rooms;
                adapter = new RoomsCreatedAdapter(rooms, getContext());
                adapter.setOnPopupMenuItemClick(this);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }else{
                adapter = new RoomsCreatedAdapter(new ArrayList<>(), getContext());
                recyclerView.setAdapter(adapter);

            }

        });


        return root;
    }


    @Override
    public void popupMenuItemClick(MenuItem menuItem, int position, String roomId) {
        if (menuItem.getItemId() == R.id.remove_room) {

            new AlertDialog.Builder(getContext())
                    .setTitle("Are you sure to delete room")
                    .setMessage("All data will lose")
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Ok", (dialog, which) -> FirebaseDatabase.getInstance()
                            .getReference("rooms")
                            .child(roomId)
                            .removeValue()
                            .addOnSuccessListener(unused -> {
                                viewModel.getCreatedRooms().getValue().remove(position);
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeChanged(position, this.rooms.size());
                                Toast.makeText(getContext(), "Remove success", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Log.d(TAG, "popupMenuItemClick: ", e.fillInStackTrace()))).show();

        } else if (menuItem.getItemId() == R.id.edit_room) {

        }
    }
}