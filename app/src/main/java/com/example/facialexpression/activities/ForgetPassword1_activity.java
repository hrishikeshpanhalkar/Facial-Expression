package com.example.facialexpression.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.facialexpression.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ForgetPassword1_activity extends AppCompatActivity {
    Button Nextbtn;
    ImageButton Back;
    TextInputLayout Email;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forget_password1);
        Nextbtn = (Button) findViewById(R.id.forget_password1);
        Back = (ImageButton) findViewById(R.id.forget_password1_back);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        Email = (TextInputLayout) findViewById(R.id.forget_password1_email);
        progressBar.setVisibility(View.INVISIBLE);

        Nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = Email.getEditText().getText().toString();
                if (email.equals("")) {
                    Email.setError("Please Fill the details!");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Email.setError("Invalid email address!");
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    Query checkuser = FirebaseDatabase.getInstance().getReference("Registration").orderByChild("email").equalTo(email);
                    checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                    if(dataSnapshot1.hasChild("password")){
                                        Intent intent = new Intent(ForgetPassword1_activity.this,ForgetPassword2_Activity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("Email",email);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        Email.setError(null);
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }else {
                                        Email.setError("No such Error exist!");
                                        Email.requestFocus();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            } else {
                                Email.setError("No such Error exist!");
                                Email.requestFocus();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                }
            }
        });
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetPassword1_activity.this, LoginActivity.class));
                finish();
            }
        });
    }
}
