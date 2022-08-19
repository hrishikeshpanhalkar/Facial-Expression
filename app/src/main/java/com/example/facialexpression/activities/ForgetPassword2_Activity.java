package com.example.facialexpression.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facialexpression.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ForgetPassword2_Activity extends AppCompatActivity {
    Button Smsbtn,Mailbtn;
    TextView Phonetextview,Emailtextview;
    ImageButton Back;
    String Email;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forget_password2);
        Smsbtn=(Button)findViewById(R.id.forget_password2_sms);
        Mailbtn=(Button)findViewById(R.id.forget_password2_mail);
        Back=(ImageButton) findViewById(R.id.forget_password2_back);
        Phonetextview=(TextView) findViewById(R.id.mobile_des);
        Emailtextview=(TextView) findViewById(R.id.sms_des);
        Email=getIntent().getStringExtra("Email");
        Emailtextview.setText(Email);
        Smsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ForgetPassword2_Activity.this,ForgetPassword3_Activity.class);
                intent.putExtra("Phone",Phonetextview.getText().toString());
                intent.putExtra("Email",Emailtextview.getText().toString());
                startActivity(intent);
                finish();
            }
        });
        Mailbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth=FirebaseAuth.getInstance();
                firebaseAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Reset Password Link send to email!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ForgetPassword2_Activity.this,LoginActivity.class));
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetPassword2_Activity.this,ForgetPassword1_activity.class));
                finish();
            }
        });
        Query checkphone= FirebaseDatabase.getInstance().getReference("Registration").orderByChild("email").equalTo(Email);
        checkphone.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Phonetextview.setText(ds.child("phone").getValue(String.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
