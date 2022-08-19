package com.example.facialexpression.activities;

import static com.example.facialexpression.R.color.pink;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facialexpression.R;
import com.example.facialexpression.adapters.DrawerAdapter;
import com.example.facialexpression.fragments.AboutUsFragment;
import com.example.facialexpression.fragments.DashBoardFragment;
import com.example.facialexpression.fragments.Detection_Fragment;
import com.example.facialexpression.fragments.MyProfileFragment;
import com.example.facialexpression.fragments.Object_Fragment;
import com.example.facialexpression.model.DrawerItem;
import com.example.facialexpression.model.SimpleItem;
import com.example.facialexpression.model.SpaceItem;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

public class UserHomeActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    private static final int POS_CLOSE =0;
    private static final int POS_DASHBOARD =1;
    private static final int POS_MY_PROFILE =2;
    private static final int POS_NEARBY_RES =3;
    private static final int POS_IMAGE =4;
    private static final int POS_ABOUT_US =5;
    private static final int POS_LOGOUT =7;

    Toolbar toolbar;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;

    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_home);
        firebaseAuth = FirebaseAuth.getInstance();


        googleSignInClient = GoogleSignIn.getClient(UserHomeActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        slidingRootNav=new SlidingRootNavBuilder(this)
                .withDragDistance(180)
                .withRootViewScale(0.75f)
                .withRootViewElevation(25)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();

        screenIcons=loadScreeenIcons();
        screenTitles=loadScreeenTitles();

        DrawerAdapter adapter=new DrawerAdapter(Arrays.asList(
                createItemFor(POS_CLOSE),
                createItemFor(POS_DASHBOARD).setChecked(true),
                createItemFor(POS_MY_PROFILE),
                createItemFor(POS_NEARBY_RES),
                createItemFor(POS_IMAGE),
                createItemFor(POS_ABOUT_US),
                new SpaceItem(220),
                createItemFor(POS_LOGOUT)
        ));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.drawer_list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_DASHBOARD);
    }

    private DrawerItem createItemFor(int position){
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(pink))
                .withTextTint(color(R.color.black))
                .withSelectedIconTint(color(pink))
                .withSelectedTextTint(color(pink));
    }

    @ColorInt
    private int color(@ColorRes int res){
        return ContextCompat.getColor(this, res);
    }
    private Drawable[] loadScreeenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.id_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for(int i=0; i<ta.length(); i++){
            int id =ta.getResourceId(i, 0);
            if(id!=0){
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @Override
    public void onBackPressed(){
        backButtonHandler();
        return;
    }
    public void backButtonHandler(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserHomeActivity.this);
        alertDialog.setTitle("LOGOUT?");
        alertDialog.setMessage("Are you sure you want to Logout?");
        alertDialog.setIcon(R.drawable.ic_warning);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            firebaseAuth.signOut();
                            Toast.makeText(UserHomeActivity.this, "Logout Successful!",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private String[] loadScreeenTitles() {
        return getResources().getStringArray(R.array.id_activityScreenTitles);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onItemSelected(int position) {
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();

        if(position == POS_DASHBOARD){
            DashBoardFragment dashBoardFragment= new DashBoardFragment();
            toolbar.setTitleTextColor(getResources().getColor(R.color.whiteTextColor));
            toolbar.setBackground(getResources().getDrawable(R.drawable.ctr_bg));
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu));
            toolbar.setTitle("DASHBOARD");
            transaction.replace(R.id.container, dashBoardFragment);
        }else if(position == POS_MY_PROFILE){
            MyProfileFragment myProfileFragment= new MyProfileFragment();
            toolbar.setTitle("MY PROFILE");
            transaction.replace(R.id.container, myProfileFragment);
        }else if(position == POS_NEARBY_RES){
            Detection_Fragment detection_fragment= new Detection_Fragment();
            toolbar.setTitle("EMOTION DETECTION");
            transaction.replace(R.id.container, detection_fragment);
        }else if(position == POS_IMAGE){
            Object_Fragment settingFragment= new Object_Fragment();
            toolbar.setTitle("IMAGE TO TEXT");
            transaction.replace(R.id.container, settingFragment);
        } else if(position == POS_ABOUT_US){
            AboutUsFragment aboutUsFragment = new AboutUsFragment();
            toolbar.setTitle("ABOUT US");
            transaction.replace(R.id.container, aboutUsFragment);
        }else if(position == POS_LOGOUT){
            onBackPressed();
        }

        slidingRootNav.closeMenu();
        transaction.addToBackStack(null);
        transaction.commit();
    }
}