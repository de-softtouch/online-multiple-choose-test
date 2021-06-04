package com.learn.onlinemutiplechoosetest.ui.roomTest.createdRooms;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.onlinemutiplechoosetest.MainActivity;
import com.learn.onlinemutiplechoosetest.R;
import com.learn.onlinemutiplechoosetest.model.Room;
import com.learn.onlinemutiplechoosetest.ui.roomTest.editRoom.EditRoomFragment;

import java.util.List;

import lombok.Setter;

public class RoomsCreatedAdapter extends RecyclerView.Adapter<RoomsCreatedAdapter.MyViewHolder> {

    private List<Room> rooms;
    private Context context;

    public RoomsCreatedAdapter(List<Room> rooms, Context context) {
        this.rooms = rooms;
        this.context = context;
    }

    @Setter
    private OnPopupMenuItemClick OnPopupMenuItemClick;

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
        holder.tvRoomCode.setText(room.getCode());
        holder.tvRoomName.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("roomID", position);
            ((MainActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, EditRoomFragment.newInstance(bundle))
                    .addToBackStack(null)
                    .commit();
        });
        holder.tvOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.tvOptions);
            popupMenu.inflate(R.menu.room_created_menu);
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(item -> {
                if (OnPopupMenuItemClick != null) {
                    OnPopupMenuItemClick.popupMenuItemClick(item,position, room.getId());
                    return true;
                }
                return false;
            });
        });

    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName, tvRoomCode, tvOptions;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tv_roomName1);
            tvRoomCode = itemView.findViewById(R.id.tv_roomCode);
            tvOptions = itemView.findViewById(R.id.tv_options);
        }
    }

    public interface OnPopupMenuItemClick {
        void popupMenuItemClick(MenuItem item,int position, String roomId);
    }
}
