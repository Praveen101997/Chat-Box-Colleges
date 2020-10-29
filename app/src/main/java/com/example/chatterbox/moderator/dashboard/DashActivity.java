package com.example.chatterbox.moderator.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.chatterbox.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashActivity extends AppCompatActivity {
    TextView tvTotal,tvNormal,tvGoogle,tvModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        tvTotal = findViewById(R.id.totalcount);
        tvNormal = findViewById(R.id.normalcount);
        tvGoogle = findViewById(R.id.googlecount);
        tvModel = findViewById(R.id.modelcount);
        retriveTotalData();
        retriveModelData();
        retriveGoogleData();
        retriveNormalData();


    }

    private void retriveTotalData(){
        final DatabaseReference rootf = FirebaseDatabase.getInstance().getReference().child("CompMode");
        rootf.child("Total").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvTotal.setText(String.valueOf(dataSnapshot.getValue()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void retriveGoogleData(){
        final DatabaseReference rootf = FirebaseDatabase.getInstance().getReference().child("CompMode");
        rootf.child("GoogleMode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvGoogle.setText(String.valueOf(dataSnapshot.getValue()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void retriveNormalData(){
        final DatabaseReference rootf = FirebaseDatabase.getInstance().getReference().child("CompMode");
        rootf.child("NormalMode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvNormal.setText(String.valueOf(dataSnapshot.getValue()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void retriveModelData(){
        final DatabaseReference rootf = FirebaseDatabase.getInstance().getReference().child("CompMode");
        rootf.child("ModelMode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvModel.setText(String.valueOf(dataSnapshot.getValue()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
