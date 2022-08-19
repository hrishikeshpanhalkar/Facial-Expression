package com.example.facialexpression.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.facialexpression.R;
import com.example.facialexpression.activities.ObjectActivity;

public class Object_Fragment extends Fragment {
    Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_object_, container, false);
        button = view.findViewById(R.id.objectBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ObjectActivity.class));
            }
        });
        return view;
    }

}
