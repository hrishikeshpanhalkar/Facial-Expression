package com.example.facialexpression.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.facialexpression.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileFragment extends Fragment {
    private CircleImageView circleImageView, circleImageView1;
    private TextView BirthDate, Gender=null, fullname;
    private ImageButton birthdate,showDialog;
    private Button Updatebtn;
    private TextInputLayout Fullname, Email, Phone;
    private ProgressDialog mprogressDialog;
    private final static int REQUEST_CODE = 123;
    private final static int PERMISSION_CODE = 1234;
    private String semail, parentvalue, link;
    private RadioButton selectedRadioButton;
    private String DfullName, DBirthdate, DGender=null, DPhone, DPhone1;
    private Uri FilePathUri;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private DatabaseReference databaseReference;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    ProgressBar progressBar;


    public MyProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_my_profile, container, false);
        fullname = (TextView) root.findViewById(R.id.User_fullName_update);
        circleImageView = (CircleImageView) root.findViewById(R.id.profile_image_update);
        circleImageView1 = (CircleImageView) root.findViewById(R.id.addimage);
        BirthDate = (TextView) root.findViewById(R.id.user_birthdate_label_update);
        Gender = (TextView) root.findViewById(R.id.user_gender_label_update);
        Fullname = (TextInputLayout) root.findViewById(R.id.fullname_update);
        Email = (TextInputLayout) root.findViewById(R.id.user_email_update);
        Phone = (TextInputLayout) root.findViewById(R.id.user_phone_update);
        Updatebtn = (Button) root.findViewById(R.id.teacher_update_btn);
        progressBar = (ProgressBar)root.findViewById(R.id.progressbar_profile);
        birthdate = (ImageButton) root.findViewById(R.id.bdatebtn);
        showDialog = (ImageButton) root.findViewById(R.id.showDialog);
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        showDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                BirthDate.setText(date);
            }
        };
        Bundle bundle = getActivity().getIntent().getExtras();
        semail = bundle.getString("Email");
        Fullname.requestFocus();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Registration");
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        Query query = databaseReference.orderByChild("email").equalTo(semail);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    link = ds.child("imageURL").getValue(String.class);
                    Picasso.get().load(link).placeholder(R.drawable.ic_account).into(circleImageView);
                    fullname.setText(ds.child("name").getValue(String.class));
                    Fullname.getEditText().setText(ds.child("name").getValue(String.class));
                    DfullName = ds.child("name").getValue(String.class);
                    BirthDate.setText(ds.child("birthDate").getValue(String.class));
                    DBirthdate = ds.child("birthDate").getValue(String.class);
                    Gender.setText(ds.child("gender").getValue(String.class));
                    DGender = ds.child("gender").getValue(String.class);
                    Email.getEditText().setText(ds.child("email").getValue(String.class));
                    DPhone = ds.child("phone").getValue(String.class);
                    DPhone1 = ds.child("phone").getValue(String.class);
                    final String needle = "+91";
                    final int needleSize = needle.length();
                    //String haystack = "";
                    DPhone = DPhone.startsWith(needle) ? DPhone.substring(needleSize) : DPhone;
                    Phone.getEditText().setText(DPhone);
                    parentvalue = ds.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);

        Updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Updatebtn.setVisibility(View.INVISIBLE);
                if (Fullname.getEditText().getText().toString().equals("") || Phone.getEditText().getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please Fill the details!", Toast.LENGTH_LONG).show();
                    Updatebtn.setVisibility(View.VISIBLE);
                } else {
                    if (isNameChanged() || isGenderChanged() || isBirthdateChanged() || isPhoneChanged()) {
                        Toast.makeText(getContext(), "Data has been Updated!", Toast.LENGTH_LONG).show();
                        Updatebtn.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getContext(), "Data is same and cannot be updated!", Toast.LENGTH_LONG).show();
                        Updatebtn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        circleImageView1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ObsoleteSdkInt")
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent,33);
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent,33);
                }
            }
        });
        return root;
    }

    private void showAlertDialog() {
        final Dialog dialog= new Dialog(getContext());
        dialog.setContentView(R.layout.alert_dialog_radiobutton);
        dialog.setCanceledOnTouchOutside(false);


        final RadioGroup radioGroup = (RadioGroup)dialog.findViewById(R.id.radioGroup);
        Button OK = (Button) dialog.findViewById(R.id.ok);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedRadioButton=(RadioButton)dialog.findViewById(radioGroup.getCheckedRadioButtonId());
                Gender.setText(selectedRadioButton.getText().toString());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private boolean isPhoneChanged() {
        String Phone1 = "+91" + Phone.getEditText().getText().toString();
        if (!DPhone1.equals(Phone1)) {
            databaseReference.child(parentvalue).child("phone").setValue(Phone1);
            DPhone1 = Phone1;
            return true;
        } else {
            return false;
        }
    }

    private boolean isBirthdateChanged() {
        if (!DBirthdate.equals(BirthDate.getText().toString())) {
            databaseReference.child(parentvalue).child("birthDate").setValue(BirthDate.getText().toString());
            DBirthdate = BirthDate.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    private boolean isGenderChanged() {
        if (!DGender.equals(Gender.getText().toString())) {
            databaseReference.child(parentvalue).child("gender").setValue(Gender.getText().toString());
            DGender = Gender.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    private boolean isNameChanged() {
        if (!DfullName.equals(Fullname.getEditText().getText().toString())) {
            databaseReference.child(parentvalue).child("name").setValue(Fullname.getEditText().getText().toString());
            DfullName = Fullname.getEditText().getText().toString();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData() != null){
            progressBar.setVisibility(View.VISIBLE);
            Updatebtn.setVisibility(View.INVISIBLE);
            Uri sFile = data.getData();
            circleImageView.setImageURI(sFile);

            final  StorageReference reference = storage.getReference().child("profile_picture")
                    .child(FirebaseAuth.getInstance().getUid());
            reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("Registration").child(FirebaseAuth.getInstance().getUid())
                                    .child("imageURL").setValue(uri.toString());
                            Toast.makeText(getContext(),"Profile Picture Updated!",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            Updatebtn.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent,33);
                } else {
                    Toast.makeText(getContext(), "Permission Denied...", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}


