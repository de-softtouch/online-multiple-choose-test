package com.learn.onlinemutiplechoosetest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.learn.onlinemutiplechoosetest.ui.main.HomeFragment;
import com.learn.onlinemutiplechoosetest.ui.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();
        setupToolbar();

        setUpToggle();

        setUpNavigationView();

        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mainActivityViewModel.getCurrentUserInfo().observe(this, user -> {
            if (user != null) {
                Glide.with(this).load(user.getAvatar()).into(ivAvatar);
                tvUsername.setText(user.getUsername());
            }
        });


//        fDatabase
//                .getReference("rooms")
//                .child("5f619064-4022-40ac-a5a9-fb7943822310")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            Room value = snapshot.getValue(Room.class);
//                            Log.d(TAG, "onDataChange: " + value.toString());
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

    }

    private void setUpNavigationView() {
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            selectDrawerItem(item);
            return true;
        });
        if (navigationView.getHeaderCount() > 0) {
            ivAvatar = navigationView.getHeaderView(0).findViewById(R.id.iv_navigationAvartar);
            tvUsername = navigationView.getHeaderView(0).findViewById(R.id.tv_navUsername);
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
}