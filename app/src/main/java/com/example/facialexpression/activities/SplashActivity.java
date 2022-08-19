package com.example.facialexpression.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.facialexpression.R;

import pl.droidsonroids.gif.GifImageView;

public class SplashActivity extends AppCompatActivity {

    Animation topAnim,bottomAnim;
    TextView logo,slogan;
    GifImageView imageView;
    SharedPreferences onBoardingScreen;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        imageView=(GifImageView) findViewById(R.id.image);
        logo=(TextView)findViewById(R.id.logo);
        slogan=(TextView)findViewById(R.id.slogan);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);
        new Handler().postDelayed(new Runnable(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                onBoardingScreen=getSharedPreferences("onBoardingScreen",MODE_PRIVATE);
                boolean isFirstTime=onBoardingScreen.getBoolean("firstTime",true);
                if(isFirstTime){
                    SharedPreferences.Editor editor=onBoardingScreen.edit();
                    editor.putBoolean("firstTime",false);
                    editor.apply();
                    Intent intent=new Intent(SplashActivity.this, OnboardActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View, String>(imageView, "logoimage");
                    pairs[1] = new Pair<View, String>(logo, "logotext");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pairs);
                    startActivity(intent, options.toBundle());
                    finish();
                }
            }
        },4000);
    }
}
