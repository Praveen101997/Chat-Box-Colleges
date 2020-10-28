package com.example.chatterbox;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.design.widget.TabLayout;
//import android.support.v4.view.ViewPager;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.chatterbox.find_friends.FindFriendsActivity;
import com.example.chatterbox.login_register.LoginActivity;
import com.example.chatterbox.settings.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

//import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabAccessorAdapter myTabAccessorAdapter;
//    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootf;
    private String currentUserID;

    public static String currentUserName = "CurrentUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
//        currentUser = mAuth.getCurrentUser();
        rootf = FirebaseDatabase.getInstance().getReference();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
//        setSupportActionBar().setTitle("Chatter Box");

        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabAccessorAdapter = new TabAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabAccessorAdapter);

        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);




    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser  = mAuth.getCurrentUser();

        if(currentUser==null){
            SendUserToLoginActivity();
        }else{

            updatUserStatus("online");
            VerifyUserExistance();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser  = mAuth.getCurrentUser();


        if(currentUser!=null){
            updatUserStatus("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentUser  = mAuth.getCurrentUser();

        if(currentUser!=null){
            updatUserStatus("offline");
        }
    }

    private void VerifyUserExistance() {
        final String currentUserId = mAuth.getCurrentUser().getUid();
        rootf.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists()){

                    Toast.makeText(MainActivity.this,"Welcome User",Toast.LENGTH_SHORT).show();
                }else{
                    SendUserToSettingActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.main_find_friends_option){
            SendUserToFindFriendsActivity();
        }
        if(item.getItemId()==R.id.main_settings_option){
            SendUserToSettingActivity();
        }
        if(item.getItemId()==R.id.main_create_group_option){
            CreateGroupActivity();
        }
        if(item.getItemId()==R.id.main_logout_option){
           updatUserStatus("offline");
            mAuth.signOut();
            SendUserToLoginActivity();
            Toast.makeText(this,"Sign Out Successfully",Toast.LENGTH_SHORT).show();

        }
        return true;

    }

    private void CreateGroupActivity() {
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("Group Name");
        layout.addView(groupNameField);

        final TextView groupProfamity = new TextView(MainActivity.this);
        groupProfamity.setText("Profanity Detection Mode");
        layout.addView(groupProfamity);

        final RadioGroup rg = new RadioGroup(MainActivity.this); //create the RadioGroup
        rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
        RadioButton[] rb = new RadioButton[2];
        rb[0] = new RadioButton(MainActivity.this);
        rb[0].setText("Don't Block on 3rd Attempt");
        rb[0].setId(0);
        rg.addView(rb[0]);

        rb[1] = new RadioButton(MainActivity.this);
        rb[1].setText("Block on 3rd Attempt");
        rb[1].setId(1);
        rg.addView(rb[1]);

        layout.addView(rg);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter new Group Information");
        builder.setView(layout);


//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
//        builder.setTitle("Enter Group Name");
//        final EditText groupNameField = new EditText(MainActivity.this);
//        groupNameField.setHint("College Friends");
//        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNameField.getText().toString();
                int selectedRadioButtonId = rg.getCheckedRadioButtonId();


                if(TextUtils.isEmpty(groupName)||selectedRadioButtonId==-1){
                    Toast.makeText(MainActivity.this,"Please Enter Group Name",Toast.LENGTH_SHORT).show();
                }else{
                    if(selectedRadioButtonId==0){
                        CreateNewGroup(groupName,false);
                    }else if(selectedRadioButtonId==1){
                        CreateNewGroup(groupName,true);
                    }
//                        CreateNewGroup(groupName);
                }
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void CreateNewGroup(final String groupName,final Boolean checkProfanity) {
        rootf.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            HashMap map = new HashMap();
                            map.put(groupName,checkProfanity);
                            rootf.child("GroupPolicy").updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    Toast.makeText(MainActivity.this,groupName+" Created Successfully",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                });

    }

    private void SendUserToSettingActivity() {
        Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingIntent);
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.putExtra("name","Praveen");
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


    private void SendUserToFindFriendsActivity() {
        Intent findFriendIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
//        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(findFriendIntent);
//        finish();
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(MainActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void updatUserStatus(String state){
        String saveCurrentTime, saveCurrentDate;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String,Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time",saveCurrentTime);
        onlineStateMap.put("date",saveCurrentDate);
        onlineStateMap.put("state",state);

        currentUserID = mAuth.getCurrentUser().getUid();
        rootf.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);
    }



}
