package com.example.facialexpression.activities;

import android.Manifest;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.facialexpression.R;
import com.example.facialexpression.model.User_update;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
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

public class User_Update_1Activity extends AppCompatActivity {
    CircleImageView circleImageView, circleImageView1;
    TextView BirthDate, Gender = null, fullname;
    ImageButton birthdate, showDialog, Back;
    Button Updatebtn;
    TextInputLayout Fullname, Email, Phone;
    ProgressDialog mprogressDialog;
    private final static int PERMISSION_CODE = 1234;
    String semail, parentvalue, link;
    RadioButton selectedRadioButton;
    String DfullName, DBirthdate, DGender = null, DPhone, DPhone1;
    Uri FilePathUri, oldFilePathUri;
    DatePickerDialog.OnDateSetListener dateSetListener;
    DatabaseReference databaseReference;
    FirebaseDatabase database;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_update1);
        fullname = (TextView) findViewById(R.id.User_fullName_update);
        circleImageView = (CircleImageView) findViewById(R.id.profile_image_update);
        circleImageView1 = (CircleImageView) findViewById(R.id.addimage);
        BirthDate = (TextView) findViewById(R.id.user_birthdate_label_update);
        Gender = (TextView) findViewById(R.id.user_gender_label_update);
        Fullname = (TextInputLayout) findViewById(R.id.fullname_update);
        Email = (TextInputLayout) findViewById(R.id.user_email_update);
        Phone = (TextInputLayout) findViewById(R.id.user_phone_update);
        Updatebtn = (Button) findViewById(R.id.teacher_update_btn);
        birthdate = (ImageButton) findViewById(R.id.bdatebtn);
        showDialog = (ImageButton) findViewById(R.id.showDialog);
        Back = (ImageButton) findViewById(R.id.BackUpdate);
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(User_Update_1Activity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_Update_1Activity.this, User_Update_Activity.class);
                startActivity(intent);
                finish();
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
        Intent intent = getIntent();
        Fullname.requestFocus();
        if (intent.getExtras() != null) {
            User_update user_update = (User_update) intent.getSerializableExtra("Email");
            semail = user_update.getEmail();
        }
        Fullname.requestFocus();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Registration");
        storage = FirebaseStorage.getInstance();
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
                    Toast.makeText(User_Update_1Activity.this, "Please Fill the details!", Toast.LENGTH_LONG).show();
                    Updatebtn.setVisibility(View.VISIBLE);
                } else {
                    if (isNameChanged() || isGenderChanged() || isBirthdateChanged() || isPhoneChanged()) {
                        Toast.makeText(User_Update_1Activity.this, "Data has been Updated!", Toast.LENGTH_LONG).show();
                        Updatebtn.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(User_Update_1Activity.this, "Data is same and cannot be updated!", Toast.LENGTH_LONG).show();
                        Updatebtn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        circleImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent,33);
                }
            }
        });
    }

    private void showAlertDialog() {
        final Dialog dialog = new Dialog(User_Update_1Activity.this);
        dialog.setContentView(R.layout.alert_dialog_radiobutton);
        dialog.setCanceledOnTouchOutside(false);


        final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
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
                selectedRadioButton = (RadioButton) dialog.findViewById(radioGroup.getCheckedRadioButtonId());
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
            Uri sFile = data.getData();
            circleImageView.setImageURI(sFile);

            final  StorageReference reference = storage.getReference().child("profile_picture")
                    .child(parentvalue);
            reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("Registration").child(parentvalue)
                                    .child("imageURL").setValue(uri.toString());
                            Toast.makeText(User_Update_1Activity.this,"Profile Picture Updated!",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, 33);
                } else {
                    Toast.makeText(User_Update_1Activity.this, "Permission Denied...", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void onBackPressed() {
        //Display alert message when back button has been pressed
        backButtonHandler();
        return;
    }

    public void backButtonHandler() {
        finish();
        Intent intent = new Intent(User_Update_1Activity.this, User_Update_Activity.class);
        startActivity(intent);
    }
}
