package com.example.facialexpression.fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.facialexpression.R;
import com.example.facialexpression.activities.ForgetPassword1_activity;
import com.example.facialexpression.activities.HomeActivity;
import com.example.facialexpression.activities.UserHomeActivity;
import com.example.facialexpression.model.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class LoginTabFragment extends Fragment {
    EditText email, pass;
    Button Admin, User;
    Button forgetPass;
    float v = 0;
    ConstraintLayout buttonlayout;
    FirebaseAuth firebaseAuth;
    CheckBox rememberMe;
    ProgressBar progressBar;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_login_tab, container, false);

        email = root.findViewById(R.id.email);
        pass = root.findViewById(R.id.pass);
        forgetPass = root.findViewById(R.id.forget_pass);
        Admin = root.findViewById(R.id.admin);
        User = root.findViewById(R.id.user);
        buttonlayout = root.findViewById(R.id.button_layout);
        rememberMe = (CheckBox) root.findViewById(R.id.rememberme);
        progressBar = (ProgressBar) root.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        SessionManager sessionManager = new SessionManager(getContext(), SessionManager.SESSION_REMEMBERME);
        if (sessionManager.checkRememberMe()) {
            HashMap<String, String> rememeberMedatails = sessionManager.getRememberMeDetailsFromSession();
            email.setText(rememeberMedatails.get(SessionManager.KEY_SESSIONEMAIL));
            pass.setText(rememeberMedatails.get(SessionManager.KEY_SESSIONPASSWORD));
        }

        Admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
                    Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.alert_dialog);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                    Button bttryagain = dialog.findViewById(R.id.bt_try_again);
                    bttryagain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "Connection Failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.show();
                } else {
                    if ((email.getText().toString().equals(""))) {
                        Toast.makeText(getContext(), "Please enter username", Toast.LENGTH_SHORT).show();
                    } else if ((pass.getText().toString().equals(""))) {
                        Toast.makeText(getContext(), "Please enter password", Toast.LENGTH_SHORT).show();
                    } else {
                        validate(email.getText().toString(), pass.getText().toString());
                        email.requestFocus();
                    }
                }
            }
        });
        User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
                    Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.alert_dialog);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                    Button bttryagain = dialog.findViewById(R.id.bt_try_again);
                    bttryagain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "Connection Failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.show();
                } else {
                    final String password = pass.getText().toString().trim();
                    final String Email = email.getText().toString().trim();
                    if ((email.getText().toString().equals(""))) {
                        Toast.makeText(getContext(), "Please enter Email", Toast.LENGTH_SHORT).show();
                    } else if ((pass.getText().toString().equals(""))) {
                        Toast.makeText(getContext(), "Please Enter Password!", Toast.LENGTH_SHORT).show();
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        Admin.setVisibility(View.INVISIBLE);
                        User.setVisibility(View.INVISIBLE);
                        forgetPass.setVisibility(View.INVISIBLE);
                        firebaseAuth.signInWithEmailAndPassword(Email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                        Toast.makeText(getContext(), "Login Successfully!", Toast.LENGTH_SHORT).show();
                                        if (rememberMe.isChecked()) {
                                            SessionManager sessionManager = new SessionManager(getContext(), SessionManager.SESSION_REMEMBERME);
                                            sessionManager.createRememberMeSession(Email, password);
                                        }
                                        Intent intent = new Intent(getContext(), UserHomeActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("Email", Email);
                                        intent.putExtras(bundle);
                                        Pair[] pairs = new Pair[1];
                                        pairs[0] = new Pair<View, String>(root.findViewById(R.id.user), "login_User");
                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) getContext(), pairs);
                                        startActivity(intent, options.toBundle());
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Admin.setVisibility(View.VISIBLE);
                                        User.setVisibility(View.VISIBLE);
                                        forgetPass.setVisibility(View.VISIBLE);
                                        pass.setText("");
                                        email.setText("");
                                        email.requestFocus();
                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Admin.setVisibility(View.VISIBLE);
                                        User.setVisibility(View.VISIBLE);
                                        forgetPass.setVisibility(View.VISIBLE);
                                        Toast.makeText(getContext(), "Please Verify your email address!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Admin.setVisibility(View.VISIBLE);
                                    User.setVisibility(View.VISIBLE);
                                    forgetPass.setVisibility(View.VISIBLE);
                                    Toast.makeText(getContext(), "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
                    Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.alert_dialog);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                    Button bttryagain = dialog.findViewById(R.id.bt_try_again);
                    bttryagain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "Connection Failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.show();
                } else {
                    Intent intent = new Intent(getContext(), ForgetPassword1_activity.class);
                    startActivity(intent);
                }
            }
        });

        email.setTranslationX(800);
        pass.setTranslationX(800);
        rememberMe.setTranslationX(800);
        forgetPass.setTranslationX(800);
        buttonlayout.setTranslationX(800);

        email.setAlpha(v);
        pass.setAlpha(v);
        rememberMe.setAlpha(v);
        forgetPass.setAlpha(v);
        buttonlayout.setAlpha(v);

        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        pass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        rememberMe.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        forgetPass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        buttonlayout.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(900).start();

        return root;
    }

    public void validate(final String userName, final String passWord) {
        progressBar.setVisibility(View.VISIBLE);
        Admin.setVisibility(View.INVISIBLE);
        User.setVisibility(View.INVISIBLE);
        forgetPass.setVisibility(View.INVISIBLE);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Admin");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("UserName").getValue(String.class);
                String password = snapshot.child("Password").getValue(String.class);
                if ((userName.equals(name)) && (passWord.equals(password))) {
                    Toast.makeText(getContext(), "Login Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(Admin, "login_home");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) getContext(), pairs);
                    startActivity(intent, options.toBundle());
                    progressBar.setVisibility(View.INVISIBLE);
                    Admin.setVisibility(View.VISIBLE);
                    User.setVisibility(View.VISIBLE);
                    forgetPass.setVisibility(View.VISIBLE);
                    email.setText("");
                    pass.setText("");
                } else {
                    Toast.makeText(getContext(), "Invalid username or password", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    Admin.setVisibility(View.VISIBLE);
                    User.setVisibility(View.VISIBLE);
                    forgetPass.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void onBackPressed() {
        new AlertDialog.Builder(getContext()).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you want to exit?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setNegativeButton("no", null).show();
    }
}

