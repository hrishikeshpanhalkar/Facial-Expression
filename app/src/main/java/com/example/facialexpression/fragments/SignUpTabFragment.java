package com.example.facialexpression.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.facialexpression.R;
import com.example.facialexpression.model.Registration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpTabFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Registration registration;
    private EditText Name, Email, Mobile, Pass;
    private Button signup;
    private TextView tvNameError,tvEmailError,tvPasswordError,tvPhoneError;
    private ProgressBar progressBar;
    private String Birthdate = "BirthDate";
    private String Gender = "Gender";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_sign_up_tab, container, false);

        Name = root.findViewById(R.id.name);
        Email = root.findViewById(R.id.email);
        Pass = root.findViewById(R.id.pass);
        Mobile = root.findViewById(R.id.mobile);
        signup = root.findViewById(R.id.signup);
        tvNameError=root.findViewById(R.id.tvNameError);
        tvEmailError=root.findViewById(R.id.tvEmailError);
        tvPasswordError=root.findViewById(R.id.tvPasswordError);
        tvPhoneError=root.findViewById(R.id.tvPhoneError);
        progressBar = root.findViewById(R.id.progressbar_signup);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Registration");
        firebaseAuth = FirebaseAuth.getInstance();
        registration = new Registration();

        signup.setOnClickListener(new View.OnClickListener() {
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
                    final String name = Name.getText().toString();
                    final String email = Email.getText().toString();
                    String phone = Mobile.getText().toString();
                    final String Phone1 = "+91" + phone;
                    final String Password = Pass.getText().toString();
                    if(nameCheck() && emailCheck() && passwordCheck() && phoneCheck()){
                        progressBar.setVisibility(View.VISIBLE);
                        signup.setVisibility(View.INVISIBLE);
                        firebaseAuth.createUserWithEmailAndPassword(email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                registration.setName(name);
                                                registration.setEmail(email);
                                                registration.setPassword(Password);
                                                registration.setPhone(Phone1);
                                                registration.setBirthDate(Birthdate);
                                                registration.setGender(Gender);
                                                databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(registration);
                                                Toast.makeText(getContext(), "User Created Successfully, Please verify your email address!", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.INVISIBLE);
                                                signup.setVisibility(View.VISIBLE);
                                                Email.setText("");
                                                Name.setText("");
                                                Pass.setText("");
                                                Mobile.setText("");
                                                Email.setError(null);
                                                Name.setError(null);
                                                Pass.setError(null);
                                                Mobile.setError(null);
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    signup.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Please Fill Correct Details!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        inputChange();
        return root;
    }

    private void inputChange() {
        Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameCheck();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailCheck();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordCheck();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneCheck();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @SuppressLint("ResourceType")
    private boolean passwordCheck() {
        String password = Pass.getText().toString();

        if (password.length() == 0) {
            tvPasswordError.setText("Password is Empty");
            tvPasswordError.setVisibility(View.VISIBLE);
            return false;
        } else if (password.length() < 8) {
            tvPasswordError.setText("Password is too weak!");
            tvPasswordError.setVisibility(View.VISIBLE);
            return false;
        } else if (!password.matches("(.*[A-Z].*)")) {
            tvPasswordError.setText("Password must one Uppercase letter!");
            tvPasswordError.setVisibility(View.VISIBLE);
            return false;
        } else if (!password.matches("(.*[0-9].*)")) {
            tvPasswordError.setText("Password must one Number!");
            tvPasswordError.setVisibility(View.VISIBLE);
            return false;
        } else if (!password.matches("(.*[!@#$%^&].*)")) {
            tvPasswordError.setText("Password must one Special Character!");
            tvPasswordError.setVisibility(View.VISIBLE);
            return false;
        } else {
            tvPasswordError.setText("");
            tvPasswordError.setVisibility(View.GONE);
            return true;
        }
    }

    @SuppressLint("ResourceType")
    private boolean nameCheck() {
        String name = Name.getText().toString();

        if (name.length() == 0) {
            tvNameError.setText("Name is Empty");
            tvNameError.setVisibility(View.VISIBLE);
            return false;
        } else {
            tvNameError.setText("");
            tvNameError.setVisibility(View.GONE);
            return true;
        }
    }

    @SuppressLint("ResourceType")
    private boolean emailCheck() {
        String email = Email.getText().toString();

        if (email.length() == 0) {
            tvEmailError.setText("Email is Empty");
            tvEmailError.setVisibility(View.VISIBLE);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tvEmailError.setText("Invalid Email Address!");
            tvEmailError.setVisibility(View.VISIBLE);
            return false;
        } else {
            tvEmailError.setText("");
            tvEmailError.setVisibility(View.GONE);
            return true;
        }
    }

    @SuppressLint("ResourceType")
    private boolean phoneCheck() {
        String phone = Mobile.getText().toString();

        if (phone.length() == 0) {
            tvPhoneError.setText("Phone is Empty");
            tvPhoneError.setVisibility(View.VISIBLE);
            return false;
        } else if (phone.length()<10) {
            tvPhoneError.setText("Invalid Phone number!");
            tvPhoneError.setVisibility(View.VISIBLE);
            return false;
        } else {
            tvPhoneError.setText("");
            tvPhoneError.setVisibility(View.GONE);
            return true;
        }
    }
}

