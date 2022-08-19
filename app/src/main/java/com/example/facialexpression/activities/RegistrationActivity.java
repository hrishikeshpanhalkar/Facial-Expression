package com.example.facialexpression.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facialexpression.model.Registration;
import com.example.facialexpression.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {
    private Button button;
    private TextInputLayout Name, Password, Email,Phone;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    Registration registration;
    ImageView logoimage;
    TextView textView;
    ProgressBar progressBar;
    String Birthdate = "BirthDate";
    String Gender = "Gender";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);
        Name = (TextInputLayout) findViewById(R.id.name);
        Password = (TextInputLayout) findViewById(R.id.password);
        Email = (TextInputLayout) findViewById(R.id.email);
        Phone = (TextInputLayout) findViewById(R.id.phone);
        button = (Button) findViewById(R.id.btn3);
        progressBar=(ProgressBar)findViewById(R.id.progressbar_registration);
        logoimage = (ImageView) findViewById(R.id.userlogo);
        textView = (TextView) findViewById(R.id.usertext);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Registration");
        firebaseAuth = FirebaseAuth.getInstance();
        registration = new Registration();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = Password.getEditText().getText().toString().trim();
                final String email = Email.getEditText().getText().toString().trim();
                if ((Name.getEditText().getText().toString().equals(""))) {
                    Name.setError("Fill the Name!");
                } else if (Email.getEditText().getText().toString().equals("")) {
                    Email.setError("Fill the Email!");
                    Name.setError(null);
                }   else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Email.setError("Invalid email address!");
                }else if (Password.getEditText().getText().toString().equals("")) {
                    Password.setError("Fill the Password!");
                    Email.setError(null);
                } else if (Password.getEditText().getText().length() < 6) {
                    Password.setError("Password must be 6 letter long!");
                }else if (Phone.getEditText().getText().toString().equals("")) {
                    Phone.setError("Fill the Phone!");
                    Password.setError(null);
                } else if (Phone.getEditText().getText().length()<10) {
                    Phone.setError("Please Enter Correct phone number");
                } else {
                    final String name = Name.getEditText().getText().toString();
                    String phone = Phone.getEditText().getText().toString();
                    final String phone1= "+91" + phone;
                    button.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            registration.setName(name);
                                            registration.setEmail(email);
                                            registration.setPassword(password);
                                            registration.setPhone(phone1);
                                            registration.setBirthDate(Birthdate);
                                            registration.setGender(Gender);
                                            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(registration);
                                            Toast.makeText(RegistrationActivity.this, "User Created Successfully, Please verify your email address!", Toast.LENGTH_LONG).show();
                                            button.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Name.getEditText().setText("");Name.setError(null);
                                            Email.getEditText().setText("");Email.setError(null);
                                            Password.getEditText().setText("");Password.setError(null);
                                            Phone.getEditText().setText("");Phone.setError(null);
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                button.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }
}
