package com.learn.onlinemutiplechoosetest.ui.roomTest.createdRooms;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.onlinemutiplechoosetest.MainActivity;
import com.learn.onlinemutiplechoosetest.R;
import com.learn.onlinemutiplechoosetest.model.Room;
import com.learn.onlinemutiplechoosetest.ui.roomTest.editRoom.EditRoomFragment;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RoomsCreatedAdapter extends RecyclerView.Adapter<RoomsCreatedAdapter.MyViewHolder> {

    private List<Room> rooms;
    private Context context;


    @NonNull
    @Override
    public RoomsCreatedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.room_created_entry, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomsCreatedAdapter.MyViewHolder holder, int position) {
        Room room = rooms.get(position);
        holder.tvRoomName.setText(room.getName());
        holder.tvRoomName.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("roomID",position);
            ((MainActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, EditRoomFragment.newInstance(bundle))
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tv_roomName1);
        }
    }
}
