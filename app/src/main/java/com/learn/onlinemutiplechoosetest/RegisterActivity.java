package com.learn.onlinemutiplechoosetest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private FirebaseAuth fAuth;

    private EditText etEmail, etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getViews();

        fAuth = FirebaseAuth.getInstance();


    }

    void getViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_passsword);
        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register: {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email must not be blank");
                    return;
                }
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmail.setError("Please fill correct email");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password must not be blank");
                    return;
                }
                doRegister(email, password);
                break;
            }
        }
    }

    private void doRegister(String email, String password) {

        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    startMainActivity();
                })
                .addOnFailureListener(e -> {
                    etEmail.setError("The email address is already in use by another account");
                    etPassword.setText("");
                    Log.d(TAG, "doRegister: " + e.toString());
                });

    }

    void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
