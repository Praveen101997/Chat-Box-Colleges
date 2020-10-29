package com.example.chatterbox.moderator;

import android.content.DialogInterface;
import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatterbox.R;
import com.example.chatterbox.login_register.LoginActivity;
import com.example.chatterbox.moderator.dashboard.DashActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Moderator extends AppCompatActivity {

    private ImageButton logoutbutton;
    private Button refreshbutton;
    private DatabaseReference blockedUserReference;
    private TextView notice;
    List<BlockedUser> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderator);

        logoutbutton = findViewById(R.id.logoutbutton);
        notice = findViewById(R.id.moderatornotice);

        refreshbutton = findViewById(R.id.refresh_button);
        refreshbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(Moderator.this,Moderator.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            }
        });

        logoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(Moderator.this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            }
        });

        blockedUserReference = FirebaseDatabase.getInstance().getReference().child("BlockUser");

        findViewById(R.id.profmode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(Moderator.this);
                alertdialog.setTitle("Select One Mode:-");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Moderator.this, android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("In App Mode");
                arrayAdapter.add("Google API");
                arrayAdapter.add("ML Model");

                alertdialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertdialog.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        final String strName = arrayAdapter.getItem(which);
                        FirebaseDatabase.getInstance().getReference().child("SelectedProfanity").setValue(which).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),strName+" Selected",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                });
                alertdialog.show();

            }
        });


        findViewById(R.id.comparison).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CompoundButton) view).isChecked()){
                    FirebaseDatabase.getInstance().getReference().child("ComparsionMode").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(),"Comparison Mode Enabled",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    FirebaseDatabase.getInstance().getReference().child("ComparsionMode").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(),"Comparison Mode Disabled",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        findViewById(R.id.compdash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Moderator.this, DashActivity.class);
                startActivity(intent)
                ;
            }
        });
    }



    private void addUser(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()){
            BlockedUser buser = new BlockedUser();
            String tempDetail = (String) ((DataSnapshot)iterator.next()).getValue();
            String[] temp = tempDetail.split(" - ");
            buser.setUserId(temp[0]);
            buser.setUserName(temp[1]);
            buser.setGroupEnrolled(temp[2]);
            String[] statements = temp[3].split(",");
            buser.setStatement1(statements[1]);
            buser.setStatement2(statements[2]);
            buser.setStatement3(statements[3]);

            Log.d("CC",temp[0]+"-"+temp[1]+"-"+temp[2]+"-"+statements[1]+"-"+statements[2]+"-"+statements[3]);

            users.add(buser);

        }

        Log.d("SizeUser",users.size()+" Size Before");

        for(int i=0;i<users.size();i++){
            BlockedUser u = users.get(i);
            Log.d("qw",i+". "+u.getUserId()+"-"+u.getUserName()+"-"+u.getGroupEnrolled()+"-"+u.getStatement1()+"-"+u.getStatement2()+"-"+u.getStatement3());
        }

        for(BlockedUser u:users){
            Log.d("ZZ",u.getUserId()+"-"+u.getUserName()+"-"+u.getGroupEnrolled()+"-"+u.getStatement1()+"-"+u.getStatement2()+"-"+u.getStatement3());
        }

        if(users.size()<=0){
            notice.setVisibility(View.VISIBLE);
        }else{
            notice.setVisibility(View.INVISIBLE);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.blocked_user_list);
            Log.d("SizeUser",users.size()+" Size");
            BlockedUserAdapter adapter = new BlockedUserAdapter(getApplicationContext(),users);
            recyclerView.setHasFixedSize(true);
            adapter.notifyDataSetChanged();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }


    }


    @Override
    protected void onStart() {
        super.onStart();



        blockedUserReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, @Nullable String s) {
                users = new ArrayList<>();
                if (dataSnapshot.exists()){
                    addUser(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, @Nullable String s) {
                users = new ArrayList<>();
                if (dataSnapshot.exists()){
                    addUser(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
