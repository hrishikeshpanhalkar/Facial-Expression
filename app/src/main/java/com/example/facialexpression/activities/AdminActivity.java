package com.example.facialexpression.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.facialexpression.R;

public class AdminActivity extends AppCompatActivity {
    ImageView fb,insta,twitter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin);
        insta=findViewById(R.id.instagram1);
        fb=findViewById(R.id.facebook1);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(getOpenFacebookIntent());
            }
        });

        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://instagram.com/_u/hrishikesh_panhalkar");
                Intent instagram = new Intent(Intent.ACTION_VIEW,uri);
                instagram.setPackage("com.instagram.android");

                try{
                    startActivity(instagram);
                }catch (ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/hrishikesh_panhalkar")));
                }
            }
        });
    }
    public Intent getOpenFacebookIntent() {
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/HrishikeshPanhalkar"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/HrishikeshPanhalkar"));
        }
    }
}
