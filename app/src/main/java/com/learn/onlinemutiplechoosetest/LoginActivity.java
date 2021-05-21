package com.learn.onlinemutiplechoosetest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.learn.onlinemutiplechoosetest.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ACTION_GG_SIGN_IN = 1;
    private final String TAG = getClass().getSimpleName();

    private TextView btnRegister;
    private EditText etEmail, etPassword;
    private Button btnLogin, btnSignInGoogle;
    private ProgressBar process;

    private Button btnLoginWithFB;

    private CallbackManager callbackManager;
    private GoogleSignInClient myGoogleSignInClient;
    private FirebaseAuth fAuth;

    private LoginManager fbLoginManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            startMainActivity();
        }
        getSupportActionBar().hide();
        setContentView(R.layout.acivity_login);
        getViews();

        etEmail.setText("test@gmail.com");
        etPassword.setText("password");

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
                handleSignInWithEmailAndPass(email, password);
                break;
            }

            case R.id.btn_loginWithGG: {
                fAuth.signOut();
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.google_client_id))
                        .requestEmail()
                        .build();
                myGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                Intent intent = myGoogleSignInClient.getSignInIntent();
                myGoogleSignInClient.signOut();
                startActivityForResult(intent, ACTION_GG_SIGN_IN);
                break;
            }
            case R.id.btn_signInWithFB: {
                fbLoginManager = LoginManager.getInstance();
                fbLoginManager.logOut();
                List<String> permission = new ArrayList<>();
                permission.add("public_profile");
                permission.add("email");
                fbLoginManager.logInWithReadPermissions(this, permission);
                callbackManager = CallbackManager.Factory.create();
                fbLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess" + loginResult.toString());
                        firebaseAuthWithFacebook(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError " + error.toString());
                    }
                });
                process.setVisibility(View.GONE);

                break;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        process.setVisibility(View.VISIBLE);
        if (resultCode == RESULT_OK && requestCode == ACTION_GG_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                process.setVisibility(View.VISIBLE);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    void firebaseAuthWithGoogle(String idToken) {
        process.setVisibility(View.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = fAuth.getCurrentUser();
                        storeUserInfo(user);
                        startMainActivity();
                        return;
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        process.setVisibility(View.GONE);

                    }
                });

    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        process.setVisibility(View.VISIBLE);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = fAuth.getCurrentUser();
                        storeUserInfo(user);
                        startMainActivity();
                        return;
                    } else {
                        //TODO: email has been used
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        LoginManager.getInstance().logOut();
                        process.setVisibility(View.GONE);

                    }
                });
    }

    void handleSignInWithEmailAndPass(String email, String password) {
        closeInputMethod();
        process.setVisibility(View.VISIBLE);
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
                    process.setVisibility(View.GONE);
                });

    }

    private void closeInputMethod() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    void storeUserInfo(FirebaseUser fUser) {
        Thread thread = new Thread(() -> {
            FirebaseFirestore fFirestore = FirebaseFirestore.getInstance();
            fFirestore.collection("users")
                    .document(fAuth.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot result = task.getResult();
                            if (!result.exists()) {
                                User user = new User();
                                user.setUserId(fUser.getUid());
                                user.setDateModified(new Date().toString());
                                user.setAvatar(fUser.getPhotoUrl().toString());
                                user.setEmail(fUser.getEmail());
                                String providerId = fUser.getProviderId();
                                if (providerId != null) {
                                    user.setProvider(providerId);
                                } else {
                                    user.setProvider("com.online.mutichoosetest");
                                }
                                if (fUser.getDisplayName() != null) {
                                    user.setUsername(fUser.getDisplayName());
                                } else {
                                    user.setUsername("User" + new Date().getTime());
                                }
                                fFirestore.collection("users")
                                        .document(fUser.getUid())
                                        .set(user);
                                return;
                            }
                        }
                    });
        });
        thread.start();
    }

    void getViews() {
        etEmail = findViewById(R.id.et_loginEmail);
        etPassword = findViewById(R.id.et_loginPassword);
        btnLogin = findViewById(R.id.btn_signIn);
        btnLogin.setOnClickListener(this);
        btnSignInGoogle = findViewById(R.id.btn_loginWithGG);
        btnSignInGoogle.setOnClickListener(this);
        btnLoginWithFB = findViewById(R.id.btn_signInWithFB);
        btnLoginWithFB.setOnClickListener(this);
        btnRegister = findViewById(R.id.tv_register);
        btnRegister.setOnClickListener(v -> {
            startRegisterActivity();
        });
        process = findViewById(R.id.progress_login);
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