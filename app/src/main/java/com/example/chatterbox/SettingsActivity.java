package com.example.chatterbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private Button UpdateProfileSettings;
    private EditText userName,userStatus;
    private CircleImageView userProfileImage;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference rootf;

    private static final int GalleryPick = 1;
    private StorageReference UserProfileImageRef;
    private ProgressDialog Loadingbar;
    private static String downloadUrll  = "";
    private Toolbar SettingsToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootf = FirebaseDatabase.getInstance().getReference();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        InitializeFeilds();

        UpdateProfileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateSettings();
            }
        });


        RetrieveUserInfo();

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                startActivityForResult(galleryIntent.createChooser(galleryIntent,"Select Image"),GalleryPick);

            }
        });
    }



    private void UpdateSettings() {
        String setUserName = userName.getText().toString();
        String setStatus = userStatus.getText().toString();
        if(TextUtils.isEmpty(setUserName)){
            Toast.makeText(this,"Please Write your UserName",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(setStatus)){
            Toast.makeText(this,"Please Write your Status",Toast.LENGTH_SHORT).show();
        }else{

            HashMap<String,Object> profileMap = new HashMap<>();

            profileMap.put("uid",currentUserId);
            profileMap.put("name",setUserName);
            profileMap.put("status",setStatus);
            rootf.child("Users").child(currentUserId).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                SendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this,"Profile Updated Successfully",Toast.LENGTH_SHORT).show();
                            }else{
                                String message = task.getException().toString();
                                Toast.makeText(SettingsActivity.this,"Error : "+message,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void RetrieveUserInfo() {
        rootf.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && dataSnapshot.hasChild("image"))){
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveUserStatus = dataSnapshot.child("status").getValue().toString();
                            String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();
                            Picasso.get().load(retrieveProfileImage).into(userProfileImage);
                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveUserStatus);
                        }else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveUserStatus = dataSnapshot.child("status").getValue().toString();

                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveUserStatus);

                        }else{
                            Toast.makeText(SettingsActivity.this,"Please Update Username And Status",Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void InitializeFeilds() {
        UpdateProfileSettings = (Button) findViewById(R.id.update_setting_button);
        userName = (EditText) findViewById(R.id.set_user_name);
        userStatus = (EditText) findViewById(R.id.set_profile_status);
        userProfileImage= (CircleImageView) findViewById(R.id.set_profile_image);
        Loadingbar = new ProgressDialog(this);
        SettingsToolBar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== GalleryPick&& resultCode==RESULT_OK && data!=null) {
            Uri ImageUri = data.getData();
            Uri destUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
            UCrop.of(ImageUri, destUri)
                    .withAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == UCrop.REQUEST_CROP) {
            Loadingbar.setTitle("Set Profile Image");
            Loadingbar.setMessage("Please Wait While Your Profile Photo is being Uploading");
            Loadingbar.setCanceledOnTouchOutside(false);
            Loadingbar.show();

            final Uri resultUri = UCrop.getOutput(data);


            final StorageReference ref = UserProfileImageRef.child(currentUserId+".jpg");
//            uploadTask = ref.putFile(resultUri);

            ref.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        final Uri downloadUri = task.getResult();
                        downloadUrll=String.valueOf(downloadUri);
                        Toast.makeText(SettingsActivity.this,"Profile Image uploaded Successfully",Toast.LENGTH_SHORT).show();
//                            final String downloaUrl = task.getResult().getDownloadUrl();
                        rootf.child("Users").child(currentUserId).child("image")
                                .setValue(downloadUrll)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(SettingsActivity.this,"Profile Image Saved to database Successfully",Toast.LENGTH_SHORT).show();
                                            Loadingbar.dismiss();
                                        }else{
                                            String message = task.getException().toString();
                                            Toast.makeText(SettingsActivity.this,"Error: "+message,Toast.LENGTH_SHORT).show();
                                            Loadingbar.dismiss();
                                        }
                                    }
                                });
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(SettingsActivity.this,"Error: "+message,Toast.LENGTH_SHORT).show();
                        Loadingbar.dismiss();
                    }
                }
            });





//            final StorageReference filePath = UserProfileImageRef.child(currentUserId+".jpg");
//
//            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                    if(task.isSuccessful()){
//                        Toast.makeText(SettingsActivity.this,"Profile Image uploaded Successfully",Toast.LENGTH_SHORT).show();
//                            final String downloaUrl = task.getResult().getDownloadUrl();
//                        rootf.child("Users").child(currentUserId).child("image")
//                                .setValue(downloaUrl)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if(task.isSuccessful()){
//                                            Toast.makeText(SettingsActivity.this,"Profile Image Saved to database Successfully",Toast.LENGTH_SHORT).show();
//                                            Loadingbar.dismiss();
//                                        }else{
//                                            String message = task.getException().toString();
//                                            Toast.makeText(SettingsActivity.this,"Error: "+message,Toast.LENGTH_SHORT).show();
//                                            Loadingbar.dismiss();
//                                        }
//                                    }
//                                });
//
//                    }else{
//                        String message = task.getException().toString();
//                        Toast.makeText(SettingsActivity.this,"Error: "+message,Toast.LENGTH_SHORT).show();
//                        Loadingbar.dismiss();
//                    }
//                }
//            });


        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
//            UCrop.of(sourceUri, destinationUri)
//                    .withAspectRatio(16, 9)
//                    .withMaxResultSize(maxWidth, maxHeight)
//                    .start(context);
//        }
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//        }

    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
