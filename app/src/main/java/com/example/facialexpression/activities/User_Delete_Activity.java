package com.example.facialexpression.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.facialexpression.adapters.User_Delete_Adapter;
import com.example.facialexpression.model.User_update;
import com.example.facialexpression.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class User_Delete_Activity extends AppCompatActivity {
    RecyclerView recyclerView;
    Toolbar toolbar;
    ProgressBar progressBar;
    ArrayList<User_update> list;
    User_Delete_Adapter user_delete_adapter;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String spassword, simageurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_delete);
        toolbar = findViewById(R.id.delete_user_toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.delete_user_recyclerview);
        this.setSupportActionBar(toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar123);
        progressBar.setVisibility(View.INVISIBLE);
        this.getSupportActionBar().setTitle("");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        firebaseAuth = FirebaseAuth.getInstance();
        list = new ArrayList<User_update>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Registration");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User_update user_update = dataSnapshot1.getValue(User_update.class);
                    list.add(user_update);
                }
                user_delete_adapter = new User_Delete_Adapter(User_Delete_Activity.this, list);
                recyclerView.setAdapter(user_delete_adapter);
                invalidateOptionsMenu();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(User_Delete_Activity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String semail = list.get(viewHolder.getAdapterPosition()).getEmail();
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Registration");
                Query query1 = databaseReference1.orderByChild("email").equalTo(semail);
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.hasChild("imageURL")) {
                                String password = ds.child("password").getValue(String.class);
                                String imageURL = ds.child("imageURL").getValue(String.class);
                                withImageUrlMethod(semail, password, imageURL, viewHolder);
                            } else {
                                String password = ds.child("password").getValue(String.class);
                                withoutImageUrlMehthod(semail, password, viewHolder);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                };
                query1.addListenerForSingleValueEvent(valueEventListener);
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void withoutImageUrlMehthod(String semail, String password, RecyclerView.ViewHolder viewHolder) {
        firebaseAuth.signInWithEmailAndPassword(semail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Query query = databaseReference.orderByChild("email").equalTo(semail);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            ds.getRef().removeValue();
                                            Toast.makeText(User_Delete_Activity.this, "User Data deleted Successfully!", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                            list.remove(viewHolder.getAdapterPosition());
                                            list.clear();
                                            user_delete_adapter.notifyDataSetChanged();
                                        }
                                        //startActivity(new Intent(User_Delete_Activity.this,User_Delete_Activity.class));
                                        //finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(User_Delete_Activity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void withImageUrlMethod(String semail, String password, String imageURL, RecyclerView.ViewHolder viewHolder) {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(semail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Query query = databaseReference.orderByChild("email").equalTo(semail);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            ds.getRef().removeValue();
                                            list.remove(viewHolder.getAdapterPosition());
                                            list.clear();
                                            user_delete_adapter.notifyDataSetChanged();
                                        }
                                        FirebaseStorage storage = FirebaseStorage.getInstance();
                                        final StorageReference storageReference1 = storage.getReferenceFromUrl(imageURL);
                                        storageReference1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(User_Delete_Activity.this, "User Data deleted Successfully!", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.INVISIBLE);
                                                //startActivity(new Intent(User_Delete_Activity.this,User_Delete_Activity.class));
                                                //finish();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(User_Delete_Activity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (user_delete_adapter == null || user_delete_adapter.getItemCount() == 0) {
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
                    user_delete_adapter.getFilter().filter(newText);
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

