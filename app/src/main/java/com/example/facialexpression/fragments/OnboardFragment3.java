package com.example.facialexpression.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.facialexpression.R;
import com.example.facialexpression.activities.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class OnboardFragment3 extends Fragment {
    TextView skip;
    FloatingActionButton floatingActionButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root=(ViewGroup)inflater.inflate(R.layout.fragment_onboarding3, container, false);
        skip=root.findViewById(R.id.skip3);
        floatingActionButton=root.findViewById(R.id.fab);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return root;
    }

}