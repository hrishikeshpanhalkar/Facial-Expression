package com.example.facialexpression.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.facialexpression.adapters.MusicAdapter;
import com.example.facialexpression.model.MusicModel;
import com.example.facialexpression.model.MySingleton;
import com.example.facialexpression.R;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity implements MusicAdapter.SelectedUser{
    RecyclerView recyclerView;
    MusicAdapter musicAdapter;
    ArrayList<MusicModel> arrayList;
    SimpleExoPlayer simpleExoPlayer;
    PlayerView playerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_music);
        recyclerView = findViewById(R.id.recyclerview);
        playerView = findViewById(R.id.exoplayer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<>();
        getJsonData();
        String url = "https://facereecognitation.000webhostapp.com/music/guitar_kal_ho_na_ho.mp3";
        playMusic(url);
    }
    private void getJsonData() {
        final ProgressDialog dialog = new ProgressDialog(MusicActivity.this);
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.show();

        String url = "https://facereecognitation.000webhostapp.com/music.json";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println(response);
                dialog.dismiss();
                parseArray(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    private void parseArray(JSONArray jsonArray) {
        arrayList.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                MusicModel data = new MusicModel();
                data.setCover_image(jsonObject.getString("cover_image"));
                data.setSong(jsonObject.getString("song"));
                data.setUrl(jsonObject.getString("url"));
                arrayList.add(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            musicAdapter = new MusicAdapter(MusicActivity.this, arrayList, MusicActivity.this);
            recyclerView.setAdapter(musicAdapter);
        }
    }

    @Override
    public void selectedUser(MusicModel musicModel) {
        playMusic(musicModel.getUrl());
    }

    private void playMusic(String url) {
        if(simpleExoPlayer != null){
            simpleExoPlayer.setPlayWhenReady(false);
            simpleExoPlayer.stop();
            simpleExoPlayer.seekTo(0);
        }
        playerView.setControllerShowTimeoutMs(0);
        playerView.setCameraDistance(30);
        simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(simpleExoPlayer);
        DataSource.Factory datasourcefactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "app"));
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(datasourcefactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)));
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState == Player.STATE_ENDED){
                    Toast.makeText(MusicActivity.this, "Music Ended!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}