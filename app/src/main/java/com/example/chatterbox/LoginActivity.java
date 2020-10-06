package com.example.chatterbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
//    private FirebaseUser currentUser;
    private Button LoginButton,PhoneLoginButton;
    private AutoCompleteTextView UserEmail,UserPassword;
    private TextView NeedNewAccountLink, ForgotPasswordLink;
    private CheckBox ismoderator;
    private FirebaseAuth mAuth;
    private ProgressDialog LoadingBar;
    private DatabaseReference UsersRef;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
//        currentUser = mAuth.getCurrentUser();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        InitializeFields();

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToRegisterActivity();
            }
        });

        ForgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToResetPasswordActivity();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowUserToLogin();
            }
        });

    }

    private void AllowUserToLogin(){
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please Enter Email...",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please Enter Password...",Toast.LENGTH_SHORT).show();
        }else{

            if (ismoderator.isChecked()){
                LoadingBar.setTitle("Sign In");
                LoadingBar.setMessage("Please Wait... ");
                LoadingBar.setCanceledOnTouchOutside(true);
                LoadingBar.show();
                getModeratorCredential();
//                if(validateModerator(email,password)){
                if (email.equals(modEmail)&&password.equals(modPass)){
                    LoadingBar.dismiss();
                    Intent mainIntent = new Intent(LoginActivity.this,Moderator.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                }else{
                    String message = "Credential Incorrect!!";
                    Toast.makeText(LoginActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                    LoadingBar.dismiss();
                }
            }else {


                LoadingBar.setTitle("Sign In");
                LoadingBar.setMessage("Please Wait... ");
                LoadingBar.setCanceledOnTouchOutside(true);
                LoadingBar.show();


                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String currentUserId = mAuth.getCurrentUser().getUid();
                                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                    UsersRef.child(currentUserId).child("device_token")
                                            .setValue(deviceToken)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    SendUserToMainActivity();
                                                    Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();

                                                    LoadingBar.dismiss();
                                                }
                                            });


                                    SendUserToMainActivity();
                                    LoadingBar.dismiss();
                                } else {
                                    String message = task.getException().toString();
                                    Toast.makeText(LoginActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                                    LoadingBar.dismiss();
                                }
                            }
                        });
            }
        }
    }

    private void InitializeFields() {
        LoginButton  = (Button) findViewById(R.id.login_button);
//        PhoneLoginButton  = (Button) findViewById(R.id.phone_login_button);
        UserEmail  = (AutoCompleteTextView) findViewById(R.id.login_email);
        UserPassword  = (AutoCompleteTextView) findViewById(R.id.login_password);
        NeedNewAccountLink  = (TextView) findViewById(R.id.need_new_account_link);
        ForgotPasswordLink  = (TextView) findViewById(R.id.forget_password_link);
        LoadingBar = new ProgressDialog(this);
        ismoderator = findViewById(R.id.isModerator);

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if(currentUser!=null){
//            SendUserToMainActivity();
//        }
//    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void SendUserToResetPasswordActivity() {
        Intent passresetIntent = new Intent(LoginActivity.this,PWresetActivity.class);
        startActivity(passresetIntent);
    }

    String modEmail;
    String modPass;

    private void getModeratorCredential(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Moderator");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("emailId") && dataSnapshot.hasChild("password"))){
                    String mail = dataSnapshot.child("emailId").getValue().toString();
                    String passwrd = dataSnapshot.child("password").getValue().toString();
                    modEmail = mail;
                    modPass = passwrd;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
