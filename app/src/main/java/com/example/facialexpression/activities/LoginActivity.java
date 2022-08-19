package com.example.facialexpression.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import com.example.facialexpression.adapters.LoginAdapter;
import com.example.facialexpression.model.Registration;
import com.example.facialexpression.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fb,google,twiter;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions googleSignInOptions;
    private FirebaseAuth firebaseAuth;
    private Registration registration;
    private ConstraintLayout constraintLayout;
    private String Birthdate = "BirthDate";
    private String Gender = "Gender";
    private TabItem tablogin,tabsignup;
    private DatabaseReference databaseReference;
    float v=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        tabLayout=findViewById(R.id.tab_layout);
        tablogin=findViewById(R.id.tab2);
        tabsignup=findViewById(R.id.tab1);
        viewPager=findViewById(R.id.viewPager);
        fb=findViewById(R.id.fab_fb);
        google=findViewById(R.id.fab_google);
        twiter=findViewById(R.id.fab_twitter);
        constraintLayout=findViewById(R.id.layout);
        firebaseAuth = FirebaseAuth.getInstance();
        registration = new Registration();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Registration");
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
                showFirstSnackbar(constraintLayout);
            } else {
                Intent intent= new Intent(LoginActivity.this, UserHomeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Email",firebaseUser.getEmail());
                intent.putExtras(bundle);
                startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }

        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final LoginAdapter adapter=new LoginAdapter(getSupportFragmentManager(), this,tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 0 || tab.getPosition()==1){
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        fb.setTranslationY(300);
        google.setTranslationY(300);
        twiter.setTranslationY(300);
        tabLayout.setTranslationY(300);

        fb.setAlpha(v);
        google.setAlpha(v);
        twiter.setAlpha(v);
        tabLayout.setAlpha(v);

        fb.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        google.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        twiter.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("Your Project Token")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, googleSignInOptions);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = googleSignInClient.getSignInIntent();
                System.out.println("data: ");
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100){
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(signInAccountTask.isSuccessful()){
                String s = "Google sign in successful";
                System.out.println("data: "  + s);
                displayToast(s);
                try {
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    String Email =  googleSignInAccount.getEmail();
                    System.out.println("data: " + Email);
                    Query checkUser= databaseReference.orderByChild("email").equalTo(Email);
                    checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken()
                                        ,null);

                                firebaseAuth.signInWithCredential(authCredential)
                                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if(task.isSuccessful()){
                                                    Intent intent= new Intent(LoginActivity.this, UserHomeActivity.class);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("Email",googleSignInAccount.getEmail());
                                                    intent.putExtras(bundle);
                                                    startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                                    displayToast("Firebase authentication successful!");
                                                }else {
                                                    displayToast("Authentication Failed: " + task.getException()
                                                            .getMessage());
                                                }
                                            }
                                        });
                            }else {
                                AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken()
                                        ,null);
                                firebaseAuth.signInWithCredential(authCredential)
                                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if(task.isSuccessful()){
                                                    registration.setName(googleSignInAccount.getDisplayName());
                                                    registration.setEmail(googleSignInAccount.getEmail());
                                                    registration.setBirthDate(Birthdate);
                                                    registration.setGender(Gender);
                                                    registration.setPhone("Phone no");
                                                    databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(registration);
                                                    Intent intent= new Intent(LoginActivity.this, UserHomeActivity.class);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("Email",googleSignInAccount.getEmail());
                                                    intent.putExtras(bundle);
                                                    startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                                    displayToast("Firebase authentication successful!");
                                                }else {
                                                    displayToast("Authentication Failed: " + task.getException()
                                                            .getMessage());
                                                }
                                            }
                                        });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void displayToast(String s) {
        Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you want to exit?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("no", null).show();
    }

    private void showFirstSnackbar(View view) {
        final Snackbar snackbar = Snackbar.make(view, "Please check your internet connection", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
                    showFirstSnackbar(view);
                    snackbar.dismiss();
                } else {
                    Toast.makeText(LoginActivity.this, "Internet Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        snackbar.show();
    }
}