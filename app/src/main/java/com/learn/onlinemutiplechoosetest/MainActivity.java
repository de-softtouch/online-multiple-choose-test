package com.learn.onlinemutiplechoosetest;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.learn.onlinemutiplechoosetest.model.Room;
import com.learn.onlinemutiplechoosetest.ui.main.HomeFragment;
import com.learn.onlinemutiplechoosetest.ui.profile.ProfileFragment;
import com.learn.onlinemutiplechoosetest.ui.roomTest.RoomTestFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private MainActivityViewModel mainActivityViewModel;

    private FirebaseStorage fStorage;
    private FirebaseFirestore fFirestore;
    private FirebaseAuth fAuth;
    private FirebaseDatabase fDatabase;

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private ImageView ivAvatar;
    private TextView tvUsername;
    private Button btnFindRoom;
    private EditText etRoomCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        fDatabase = FirebaseDatabase.getInstance();
        setupToolbar();

        setUpToggle();

        setUpNavigationView();

        registerForUserDataChange();
    }

    private void registerForUserDataChange() {
        mainActivityViewModel.getCurrentUserInfo().observe(this, user -> {
            if (user != null) {
                changeNavHeaderInfo(user);
            }
        });
    }

    public void changeNavHeaderInfo(com.learn.onlinemutiplechoosetest.model.User user) {
        Glide.with(this).load(user.getAvatar()).into(ivAvatar);
        tvUsername.setText(user.getUsername());
    }

    private void setUpNavigationView() {
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            selectDrawerItem(item);
            return true;
        });
        if (navigationView.getHeaderCount() > 0) {
            View headerView = navigationView.getHeaderView(0);
            ivAvatar = headerView.findViewById(R.id.iv_navigationAvartar);
            tvUsername = headerView.findViewById(R.id.tv_navUsername);
            etRoomCode = headerView.findViewById(R.id.et_roomCode);
            etRoomCode.setText("5f619064-4022-40ac-a5a9-fb7943822310");
            btnFindRoom = headerView.findViewById(R.id.btn_findRomm);
            btnFindRoom.setOnClickListener(this);
        }
    }

    private void selectDrawerItem(MenuItem item) {
        Class fragmentClass = null;
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_item_logout: {
                fAuth.signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
            case R.id.nav_item_account_setting: {
                fragmentClass = ProfileFragment.class;
                navigationView.setCheckedItem(R.id.nav_item_account_setting);

                break;
            }
            default: {
                fragmentClass = HomeFragment.class;
            }
        }

        FragmentManager manager = getSupportFragmentManager();
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            manager
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.nav_host_fragment, fragment)
                    .commit();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void setUpToggle() {

        drawerLayout = findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        drawerLayout.addDrawerListener(drawerToggle);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void closeInputMethod() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    public void checkRoomIsAvailable(String roomCode) {
        closeInputMethod();
        fDatabase
                .getReference("rooms")
                .child(roomCode.toString())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot != null && snapshot.exists()) {
                            Room room = snapshot.getValue(Room.class);
                            Log.d(TAG, "onDataChange: " + room.toString());
                            if (room.isUsePassword()) {
                                showPasswordDialog(room);
                            } else {
                                joinRoom(room);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return;
                            }
                        } else {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("We were not found your room code")
                                    .setMessage("Please try another")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        dialog.dismiss();
                                        etRoomCode.setText("");
                                        return;
                                    }).show();
                            //not found room code given
                            Log.d(TAG, "onDataChange: cannot find room code");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: " + error.toString());
                    }
                });


    }

    private void showPasswordDialog(Room room) {
        View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.et_password, null);
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setView(dialogView)
                .setTitle("This room is required password")
                .setMessage("Please enter valid password to enter")
                .setPositiveButton("Enter", null)
                .setNegativeButton("Cancel", null)
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveBtn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeBtn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
            positiveBtn.setOnClickListener(v -> {
                EditText etPassword = dialogView.findViewById(R.id.et_roomPassword);
                String pass = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(pass)) {
                    etPassword.setError("Your password is empty");
                    closeInputMethod();
                } else {
                    if (room.getPassword().equals(pass)) {
                        closeInputMethod();
                        Toast.makeText(MainActivity.this, "Enter success!", Toast.LENGTH_SHORT).show();
                        joinRoom(room);
                        dialog.dismiss();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return;
                    } else {
                        etPassword.setError("Your password is not correct");
                        Toast.makeText(MainActivity.this, "Enter failure!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            negativeBtn.setOnClickListener(v -> {
                dialog.dismiss();
            });

        });
        dialog.show();
    }

    void joinRoom(Room room) {
        mainActivityViewModel.getRoomMutableLiveData().setValue(room);
        FragmentManager sFm = getSupportFragmentManager();
        sFm
                .beginTransaction()
                .replace(R.id.nav_host_fragment, RoomTestFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_findRomm: {
                String roomCode = etRoomCode.getText().toString().trim();
                if (TextUtils.isEmpty(roomCode)) {
                    etRoomCode.setText("Please enter room code to join room");
                    return;
                }
                checkRoomIsAvailable(roomCode);
                break;
            }
        }
    }
}