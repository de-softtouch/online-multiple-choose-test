package com.learn.onlinemutiplechoosetest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.learn.onlinemutiplechoosetest.model.User;

import java.util.Date;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private FirebaseAuth fAuth;

    private EditText etEmail, etPassword;
    private Button btnRegister,btnCancel;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        getViews();

        fAuth = FirebaseAuth.getInstance();


    }

    void getViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_passsword);
        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);
        btnCancel = findViewById(R.id.btn_cancelRegister);
        btnCancel.setOnClickListener(this);
        progressBar = findViewById(R.id.progressBar_register);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register: {
                progressBar.setVisibility(View.VISIBLE);
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email must not be blank");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmail.setError("Please fill correct email");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password must not be blank");
                    return;
                }
                registerAccount(email, password);
                break;
            }
            case R.id.btn_cancelRegister:   {
                startLoginActivity();
                break;
            }
        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void registerAccount(String email, String password) {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);

        }
        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    storeUserInfo(authResult.getUser());
                    startMainActivity();

                })
                .addOnFailureListener(e -> {
                    etEmail.setError("The email address is already in use by another account");
                    etPassword.setText("");
                    Log.d(TAG, "doRegister: " + e.toString());
                    progressBar.setVisibility(View.GONE);

                });

    }

    private void storeUserInfo(FirebaseUser firebaseUser) {
        Thread thread = new Thread(() -> {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            User user = new User();
            user.setUserId(firebaseUser.getUid());
            user.setDateModified(new Date().toString());
            user.setEmail(firebaseUser.getEmail());
            user.setUsername("user" + UUID.randomUUID().toString());

            firestore.collection("users")
                    .document(firebaseUser.getUid())
                    .set(user);
        });
        thread.start();
    }

    void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
