package com.example.facialexpression.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.facialexpression.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    CircleImageView Acccount;
    ImageButton Back;
    CardView cardView1,cardView2,cardView3;
    RelativeLayout Addlayout, Updatelayout, Deletelayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        Acccount=(CircleImageView) findViewById(R.id.account);
        Back=(ImageButton) findViewById(R.id.home_back);
        Addlayout = (RelativeLayout) findViewById(R.id.adduser_layout);
        Updatelayout = (RelativeLayout) findViewById(R.id.updateuser_layout);
        Deletelayout = (RelativeLayout)findViewById(R.id.deleteuser_layout);
        cardView1 = (CardView) findViewById(R.id.cardview1);
        cardView2 = (CardView) findViewById(R.id.cardview2);
        cardView3 = (CardView) findViewById(R.id.cardview3);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
                alertDialog.setTitle("Leave application?");
                alertDialog.setMessage("Are you sure you want to leave the application?");
                alertDialog.setIcon(R.drawable.ic_warning);
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent=new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });

        Acccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this, AdminActivity.class);
                Pair[] pair=new Pair[1];
                pair[0]=new Pair<View,String>(Acccount,"transition_profile_picture");
                ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this,pair);
                startActivity(intent,options.toBundle());
            }
        });

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RegistrationActivity.class);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(Addlayout, "Addfragment");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, User_Update_Activity.class);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(Updatelayout, "Updatefragment");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, User_Delete_Activity.class);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(Deletelayout, "Deletefragment");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });

    }
    public void onBackPressed(){
        //Display alert message when back button has been pressed
        backButtonHandler();
    }
    public void backButtonHandler(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Leave application?");
        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to leave the application?");
        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_warning);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Intent intent=new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }
}