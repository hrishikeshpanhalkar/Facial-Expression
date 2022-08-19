package com.example.facialexpression.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.facialexpression.R;


public class AboutUsFragment extends Fragment {

    ImageView fb,insta,twitter,email;
    public AboutUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root=(ViewGroup)inflater.inflate(R.layout.fragment_about_us, container, false);
        insta=root.findViewById(R.id.instagram);
        fb=root.findViewById(R.id.facebook);
        email=root.findViewById(R.id.email);

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

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.putExtra(Intent.EXTRA_EMAIL, "your email");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject_name");
                intent.putExtra(Intent.EXTRA_TEXT, "Hello Dear");
                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent, "Choose an email client"));
            }
        });
        return root;
    }
    public Intent getOpenFacebookIntent() {
        try {
            getContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/HrishikeshPanhalkar"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/HrishikeshPanhalkar"));
        }
    }
}
