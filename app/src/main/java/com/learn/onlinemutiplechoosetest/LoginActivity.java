package com.learn.onlinemutiplechoosetest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();


    private TextView btnRegister;
    private EditText etEmail, etPassword;
    private Button btnLogin;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null) {
            startMainActivity();
        }
        getSupportActionBar().hide();
        setContentView(R.layout.acivity_login);
        getViews();

        btnRegister = findViewById(R.id.tv_register);
        btnRegister.setOnClickListener(v -> {
            startRegisterActivity();
        });
        fAuth = FirebaseAuth.getInstance();

        etEmail.setText("test@gmail.com");
        etPassword.setText("password");

    }

    private void getViews() {
        etEmail = findViewById(R.id.et_loginEmail);
        etPassword = findViewById(R.id.et_loginPassword);
        btnLogin = findViewById(R.id.btn_signIn);
        btnLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signIn: {
                String password = etPassword.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    etPassword.setText("Password must not be blank!");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    etEmail.setText("Email must not be blank!");
                    return;
                }
                checkLogin(email, password);
                break;
            }

        }
    }

    void checkLogin(String email, String password) {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);

        }
        fAuth
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Log.d(TAG, "checkLogin: " + authResult.getUser().toString());
                    startMainActivity();
                })
                .addOnFailureListener(e -> {
                    etEmail.setError("Email or password is not correct!");
                    etPassword.setText("");
                    Log.d(TAG, "checkLogin: " + e.toString());
                });
    }

    void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    void startRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}
