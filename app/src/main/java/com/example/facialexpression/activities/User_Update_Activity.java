package com.example.facialexpression.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.facialexpression.adapters.User_Update_Adapter;
import com.example.facialexpression.model.User_update;
import com.example.facialexpression.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class User_Update_Activity extends AppCompatActivity implements User_Update_Adapter.SelectedUser {
    RecyclerView recyclerView;
    Toolbar toolbar;
    ArrayList<User_update> list;
    User_Update_Adapter user_update_adapter;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_update);
        toolbar = findViewById(R.id.user_update_toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.myRecycler);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        list = new ArrayList<User_update>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Registration");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User_update user_update = dataSnapshot1.getValue(User_update.class);
                    list.add(user_update);
                }
                user_update_adapter = new User_Update_Adapter(User_Update_Activity.this, list, User_Update_Activity.this);
                recyclerView.setAdapter(user_update_adapter);
                invalidateOptionsMenu();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(User_Update_Activity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void selectedUser(User_update user_update) {
        finish();
        startActivity(new Intent(User_Update_Activity.this, User_Update_1Activity.class).putExtra("Email", user_update));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (user_update_adapter == null || user_update_adapter.getItemCount() == 0) {
            return super.onCreateOptionsMenu(menu);
        } else {
            getMenuInflater().inflate(R.menu.menu, menu);
            MenuItem menuItem = menu.findItem(R.id.search_view);
            SearchView searchView = (SearchView) menuItem.getActionView();
            searchView.setMaxWidth(Integer.MAX_VALUE);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    user_update_adapter.getFilter().filter(newText);
                    return true;
                }
            });
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search_view) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

