package com.example.facialexpression.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

import com.example.facialexpression.model.MySingleton;
import com.example.facialexpression.R;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

public class ModifierActivity extends AppCompatActivity {
    ImageView imageView;
    Button Share, Next;
    ProgressBar progressBar;
    String currentImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_modifier);
        imageView = (ImageView) findViewById(R.id.imageView);
        Share = (Button) findViewById(R.id.shareButton);
        Next = (Button) findViewById(R.id.nextButton);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        loadMeme();
    }

    private void loadMeme() {
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://meme-api.herokuapp.com/gimme";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            currentImageUrl = response.getString("url");
                            Glide.with(ModifierActivity.this).load(currentImageUrl).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    return false;
                                }
                            }).into(imageView);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void next(View view) {
        loadMeme();
    }

    public void share(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Hey, Checkout this cool meme I got from Reddit " + currentImageUrl);
        Intent intent1 = Intent.createChooser(intent, "Share this meme using");
        startActivity(intent1);
    }
}