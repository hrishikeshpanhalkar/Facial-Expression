package com.example.facialexpression.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.facialexpression.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashBoardFragment extends Fragment {

    Button btnTakePic,btnGallary;
    ImageView myImageView;
    CircleImageView circleImageView;
    TextView textView;
    public Uri currImageURI;
    public View v;
    DatabaseReference databaseReference;
    String semail,link;
    private static final int pic_id = 123;

    private final static int REQUEST_CODE = 123;
    private final static int PERMISSION_CODE = 1234;
    private final static int PERMISSION_CODE1 = 12345;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root= (ViewGroup) inflater.inflate(R.layout.fragment_dash_board, container, false);
        btnTakePic=(Button)root.findViewById(R.id.btnTakePic);
        btnGallary=(Button)root.findViewById(R.id.btngallary);
        myImageView=(ImageView) root.findViewById(R.id.imageView);
        textView=(TextView) root.findViewById(R.id.username);
        circleImageView=(CircleImageView) root.findViewById(R.id.profile_picture);

        Bundle bundle = getActivity().getIntent().getExtras();
        semail = bundle.getString("Email");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Registration");
        Query query = databaseReference.orderByChild("email").equalTo(semail);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    link = ds.child("imageURL").getValue(String.class);
                    Picasso.get().load(link).placeholder(R.drawable.ic_account).into(circleImageView);
                    textView.setText(ds.child("name").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ObsoleteSdkInt")
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.CAMERA};
                        requestPermissions(permission, PERMISSION_CODE1);
                    } else {
                        takePhoto();
                    }
                }
            }
        });
        btnGallary.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ObsoleteSdkInt")
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        pickFromGallery();
                    }
                } else {
                    pickFromGallery();
                }
            }
        });
        return root;
    }

    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
    }

    private void takePhoto() {
        Intent camera_intent
                = new Intent(MediaStore
                .ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera_intent, pic_id);
    }

    // To handle when an image is selected from the browser, add the following to your Activity
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                // currImageURI is the global variable I'm using to hold the content:// URI of the image
                currImageURI = data.getData();
                InputStream is = null;
                try {
                    is = getContext().getContentResolver().openInputStream(currImageURI);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap myBitmap = BitmapFactory.decodeStream(is);

                Paint myRectPaint = new Paint();
                myRectPaint.setStrokeWidth(15);
                myRectPaint.setColor(Color.BLUE);
                myRectPaint.setStyle(Paint.Style.STROKE);

                Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempCanvas = new Canvas(tempBitmap);
                tempCanvas.drawBitmap(myBitmap, 0, 0, null);

                FaceDetector faceDetector = new
                        FaceDetector.Builder(getContext()).setTrackingEnabled(false)
                        .build();
                if(!faceDetector.isOperational()){
                    Toast.makeText(getContext(), "Could not set up the face detector!",Toast.LENGTH_SHORT).show();
                }

                Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                SparseArray<com.google.android.gms.vision.face.Face> faces = faceDetector.detect(frame);

                for(int i=0; i<faces.size(); i++) {
                    com.google.android.gms.vision.face.Face thisFace = faces.valueAt(i);
                    float x1 = thisFace.getPosition().x;
                    float y1 = thisFace.getPosition().y;
                    float x2 = x1 + thisFace.getWidth();
                    float y2 = y1 + thisFace.getHeight();
                    tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
                }
//                image.setImageBitmap(bitmap);
                myImageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
            }
            if (requestCode == pic_id){
                Bitmap myBitmap =(Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                Paint myRectPaint = new Paint();
                myRectPaint.setStrokeWidth(3);
                myRectPaint.setColor(Color.BLUE);
                myRectPaint.setStyle(Paint.Style.STROKE);

                Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempCanvas = new Canvas(tempBitmap);
                tempCanvas.drawBitmap(myBitmap, 0, 0, null);

                FaceDetector faceDetector = new
                        FaceDetector.Builder(getContext()).setTrackingEnabled(false)
                        .build();
                if(!faceDetector.isOperational()){
                    new AlertDialog.Builder(v.getContext()).setMessage("Could not set up the face detector!").show();
                }
                Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                SparseArray<Face> faces = faceDetector.detect(frame);

                for(int i=0; i<faces.size(); i++) {
                    com.google.android.gms.vision.face.Face thisFace = faces.valueAt(i);
                    float x1 = thisFace.getPosition().x;
                    float y1 = thisFace.getPosition().y;
                    float x2 = x1 + thisFace.getWidth();
                    float y2 = y1 + thisFace.getHeight();
                    tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
                }
//                image.setImageBitmap(bitmap);
                myImageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
            }
        }
    }
}
