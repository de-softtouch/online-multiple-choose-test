package com.learn.onlinemutiplechoosetest.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.learn.onlinemutiplechoosetest.MainActivity;
import com.learn.onlinemutiplechoosetest.MainActivityViewModel;
import com.learn.onlinemutiplechoosetest.R;
import com.learn.onlinemutiplechoosetest.model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener {


    private MainActivityViewModel viewModel;


    private FirebaseStorage fStorage;

    private ImageView ivAvatar, btnChangeImage;
    private Button btnUpdate;
    private User userInfo;
    private ProgressBar progressBar;
    private TextInputEditText etUsername, etEmail;

    private Bitmap bitmap;
    private FirebaseFirestore fFirestore;
    private FirebaseAuth fAuth;
    private static final int ACTION_GET_IMAGE = 1322;

    public ProfileFragment() {
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        fAuth = FirebaseAuth.getInstance();
        fFirestore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        getViews(root);

        registerForUserInfoChange();


        return root;
    }

    private void getViews(View root) {
        progressBar = root.findViewById(R.id.progressBar);
        ivAvatar = root.findViewById(R.id.iv_pAvatar);
        etUsername = root.findViewById(R.id.et_pUsername);
        etEmail = root.findViewById(R.id.et_profileEmail);
        btnUpdate = root.findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(this);
        btnChangeImage = root.findViewById(R.id.iv_changeAvatar);
        btnChangeImage.setOnClickListener(this);
    }

    private void registerForUserInfoChange() {
        viewModel.getCurrentUserInfo().observe(requireActivity(), user -> {

            if (user != null) {
                userInfo = user;
                Glide.with(ivAvatar).load(user.getAvatar()).into(ivAvatar);
                etUsername.setText(user.getUsername());
                etEmail.setText(user.getEmail());
            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update: {
                String username = etUsername.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    etUsername.setError("Your username is blank");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                updateProfile(username);
                break;
            }
            case R.id.iv_changeAvatar: {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, ACTION_GET_IMAGE);
                break;
            }
        }
    }

    private void updateProfile(String username) {
        ((MainActivity) getContext()).closeInputMethod();
        etUsername.clearFocus();
        if (bitmap != null) {
            uploadImage();
        }

        User user = userInfo;
        user.setUsername(username);
        user.setDateModified(new Date().toString());

        fFirestore.collection("users")
                .document(fAuth.getUid())
                .set(user, SetOptions.merge())
                .addOnSuccessListener(unused -> {

//                    Toast.makeText(getContext(), "Update username success", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Cannot update username", Toast.LENGTH_SHORT).show();

                });
        progressBar.setVisibility(View.GONE);

    }

    private void uploadImage() {
        btnUpdate.setClickable(false);
//        Handler handler = new Handler();
//        Thread thread = new Thread(() -> {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        fStorage.getReference()
                .child("avatars")
                .child(fAuth.getUid())
                .putBytes(bytes)
                .addOnSuccessListener(taskSnapshot -> {
                    updateUserInfo(taskSnapshot);

                });
        bitmap = null;
//            handler.post(() -> {
        progressBar.setVisibility(View.INVISIBLE);
//            });
        btnUpdate.setClickable(true);


//        });
//        thread.start();
    }

    private void updateUserInfo(UploadTask.TaskSnapshot taskSnapshot) {

        taskSnapshot
                .getMetadata()
                .getReference()
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("avatar", uri.toString());
                    fFirestore
                            .collection("users")
                            .document(fAuth.getUid())
                            .set(map, SetOptions.merge()).addOnSuccessListener(unused -> {
                    });
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_GET_IMAGE
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                Glide.with(getContext()).load(bitmap).into(ivAvatar);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}