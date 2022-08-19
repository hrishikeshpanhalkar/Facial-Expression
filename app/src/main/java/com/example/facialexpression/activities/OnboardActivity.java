package com.example.facialexpression.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.facialexpression.R;
import com.example.facialexpression.fragments.OnboardFragment;
import com.example.facialexpression.fragments.OnboardFragment2;
import com.example.facialexpression.fragments.OnboardFragment3;

public class OnboardActivity extends AppCompatActivity {
    private static final int NUM_PAGES=3;
    private ViewPager viewPager;
    private ScreenSlidePagerAdapter pagerAdapter;
    private Animation anim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_onboarding);
        viewPager=findViewById(R.id.pager);
        pagerAdapter=new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        anim= AnimationUtils.loadAnimation(this, R.anim.fadeinanimation);
        viewPager.startAnimation(anim);
    }
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    OnboardFragment tab=new OnboardFragment();
                    return tab;
                case 1:
                    OnboardFragment2 tab2=new OnboardFragment2();
                    return tab2;
                case 2:
                    OnboardFragment3 tab3=new OnboardFragment3();
                    return tab3;
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}