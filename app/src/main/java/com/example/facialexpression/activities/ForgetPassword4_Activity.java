package com.example.facialexpression.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.facialexpression.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ForgetPassword4_Activity extends AppCompatActivity {
    Button nextbtn;
    TextInputEditText password_et, confirmPassword_et;
    TextView tvPasswordError, tvConfirmPasswordError;
    String spassword, parentvalue;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forget_password4);
        final String Email = getIntent().getStringExtra("Email");
        nextbtn = (Button) findViewById(R.id.forget_password4);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar=(ProgressBar)findViewById(R.id.password_update_prgressbar);
        progressBar.setVisibility(View.INVISIBLE);
        password_et = (TextInputEditText) findViewById(R.id.forget_password4_password_et);
        confirmPassword_et = (TextInputEditText) findViewById(R.id.forget_password4_Cpassword_et);
        tvPasswordError = (TextView) findViewById(R.id.tvPasswordError);
        tvConfirmPasswordError = (TextView) findViewById(R.id.tvConfirmPasswordError);
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Password = password_et.getText().toString();
                String ConfirmPassword = confirmPassword_et.getText().toString();
                if(passwordCheck() && confirmPasswordCheck()){
                    progressBar.setVisibility(View.VISIBLE);
                    DatabaseReference databaseReference12 = FirebaseDatabase.getInstance().getReference().child("Registration");
                    Query query1 = databaseReference12.orderByChild("email").equalTo(Email);
                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                spassword = ds.child("password").getValue(String.class);
                                parentvalue = ds.getKey();
                                firebaseAuth.signInWithEmailAndPassword(Email,spassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            firebaseAuth.getCurrentUser().updatePassword(Password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Registration");
                                                        databaseReference.child(parentvalue).child("password").setValue(Password);
                                                        firebaseAuth.signOut();
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(ForgetPassword4_Activity.this,"Password Updated Successfully!",Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(ForgetPassword4_Activity.this, ForgetPassword5_Activity.class));
                                                        finish();
                                                    }else {
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(ForgetPassword4_Activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }else {
                                            Toast.makeText(ForgetPassword4_Activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(ForgetPassword4_Activity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    };
                    query1.addListenerForSingleValueEvent(valueEventListener);
                } else {
                    Toast.makeText(ForgetPassword4_Activity.this,"Please Fill Correct details!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        inputChange();
    }
    private void inputChange() {
        password_et.addTextChangedListener(new TextWatcher() {
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

        confirmPassword_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmPasswordCheck();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @SuppressLint("ResourceType")
    private boolean passwordCheck() {
        String password = password_et.getText().toString();

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
    private boolean confirmPasswordCheck() {
        String cpassword = confirmPassword_et.getText().toString();
        String password = password_et.getText().toString();

        if (cpassword.length() == 0) {
            tvConfirmPasswordError.setText("Password is Empty");
            tvConfirmPasswordError.setVisibility(View.VISIBLE);
            return false;
        }  else if (!password.equals(cpassword)) {
            tvConfirmPasswordError.setText("Password doesn't match");
            tvConfirmPasswordError.setVisibility(View.VISIBLE);
            return false;
        } else {
            tvConfirmPasswordError.setText("");
            tvConfirmPasswordError.setVisibility(View.GONE);
            return true;
        }
    }
}