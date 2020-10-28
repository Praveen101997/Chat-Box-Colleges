package com.example.chatterbox.login_register;

import android.app.ProgressDialog;
import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatterbox.MainActivity;
import com.example.chatterbox.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountButton;
    private AutoCompleteTextView UserEmail,UserPassword;
    private TextView AlreadyHaveNewAccount;
    private FirebaseAuth mAuth;
    private ProgressDialog LoadingBar;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        InitializeFields();
        AlreadyHaveNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToLoginActivity();
            }
        });

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please Enter Email...",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please Enter Password...",Toast.LENGTH_SHORT).show();
        }else{
            LoadingBar.setTitle("Creating New Account");
            LoadingBar.setMessage("Please Wait... While we are creating your account");
            LoadingBar.setCanceledOnTouchOutside(true);
            LoadingBar.show();



            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                String deviceToken = FirebaseInstanceId.getInstance().getToken();


                                String currentUserID = mAuth.getCurrentUser().getUid();
                                RootRef.child("Users").child(currentUserID).setValue("");
                                RootRef.child("Users").child(currentUserID).child("device_token")
                                        .setValue(deviceToken);


                                SendUserToMainActivity();
                                Toast.makeText(RegisterActivity.this,"Account Created Successfully",Toast.LENGTH_SHORT).show();
                                LoadingBar.dismiss();
                            }else{
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this,"Error : "+message,Toast.LENGTH_SHORT).show();
                                LoadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void InitializeFields() {
        CreateAccountButton  = (Button) findViewById(R.id.register_button);
        UserEmail  = (AutoCompleteTextView) findViewById(R.id.register_email);
        UserPassword  = (AutoCompleteTextView) findViewById(R.id.register_password);
        AlreadyHaveNewAccount  = (TextView) findViewById(R.id.already_have_account_link);
        LoadingBar = new ProgressDialog(this);
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
}
