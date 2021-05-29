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
import android.widget.ProgressBar;
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
import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.onlinemutiplechoosetest.model.Room;
import com.learn.onlinemutiplechoosetest.ui.main.HomeFragment;
import com.learn.onlinemutiplechoosetest.ui.profile.ProfileFragment;
import com.learn.onlinemutiplechoosetest.ui.roomTest.ui.RoomNewFragment;
import com.learn.onlinemutiplechoosetest.ui.roomTest.ui.RoomTestFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private MainActivityViewModel mainActivityViewModel;

    private FirebaseAuth fAuth;
    private FirebaseDatabase fDatabase;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    private ImageView ivAvatar;
    private TextView tvUsername;
    private EditText etRoomCode;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getViews();
        setUpFirebase();
        setupToolbar();
        setUpToggle();
        setUpNavigationView();
        registerForUserDataChange();
    }

    private void getViews() {
    }

    private void setUpFirebase() {
        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
    }

    public void findRoomByRoomCode(String roomCode) {
        fDatabase
                .getReference("room-with-code")
                .child(roomCode)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists() && snapshot.getValue() != null) {
                        String roomId = snapshot.child("roomId").getValue(String.class);
                        findRoomById(roomId);
                    } else {
                        //find room with room id
                        findRoomById(roomCode);
                    }
                });
    }

    private void registerForUserDataChange() {
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
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
        NavigationView navigationView = findViewById(R.id.navigation_view);
        progressBar = navigationView.findViewById(R.id.progressBar1);
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
            Button btnFindRoom = headerView.findViewById(R.id.btn_findRomm);
            btnFindRoom.setOnClickListener(this);
        }
    }

    public void selectDrawerItem(MenuItem item) {
        Class fragmentClass = null;
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.nav_item_logout: {
                signOut();
                return;
            }
            case R.id.nav_item_account_setting: {
                fragmentClass = ProfileFragment.class;
                break;
            }
            case R.id.nav_item_recent_room: {
                if (mainActivityViewModel.getRoomMutableLiveData().getValue() != null) {
                    fragmentClass = RoomTestFragment.class;
                } else {
                    Toast.makeText(this, "You never joined a room", Toast.LENGTH_LONG).show();
                    return;
                }
                break;
            }
            case R.id.nav_item_create_room: {
              fragmentClass = RoomNewFragment.class;
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

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void setUpToggle() {
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close);
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

    public void findRoomById(String roomCode) {

        fDatabase
                .getReference("rooms")
                .child(roomCode)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Room room = snapshot.getValue(Room.class);

                            if (room.isUsePassword()) {
                                showPasswordDialog(room);
                            } else {
                                openRoomTestFragment(room);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);

                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("We were not found your room code")
                                    .setMessage("Please try another")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        dialog.dismiss();
                                        etRoomCode.setText("");
                                    }).show();
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
                        openRoomTestFragment(room);
                        dialog.dismiss();
                        drawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        etPassword.setError("Your password is not correct");
                        Toast.makeText(MainActivity.this, "Enter failure!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            negativeBtn.setOnClickListener(v -> dialog.dismiss());

        });
        dialog.show();
    }

    void openRoomTestFragment(Room room) {
        mainActivityViewModel.getRoomMutableLiveData().setValue(room);
        FragmentManager sFm = getSupportFragmentManager();
        sFm
                .beginTransaction()
                .replace(R.id.nav_host_fragment, RoomTestFragment.newInstance())
                .addToBackStack(null)
                .commit();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_findRomm: {
                progressBar.setVisibility(View.VISIBLE);
                closeInputMethod();

                String roomCode = etRoomCode.getText().toString().trim();
                if (TextUtils.isEmpty(roomCode)) {
                    etRoomCode.setText("Please enter room code to join room");
                    return;
                }
                findRoomByRoomCode(roomCode);
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}