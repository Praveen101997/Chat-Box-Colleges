package com.example.chatterbox.group_chat;

import com.example.chatterbox.modelprediction;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatterbox.R;
import com.example.chatterbox.group_chat.MessageAdapterGroup;
import com.example.chatterbox.group_chat.MessageGroup;
import com.example.chatterbox.profanity.BadWordFilter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koushikdutta.async.future.FutureCallback;
//import com.koushikdutta.async.http.BasicNameValuePair;
//import com.koushikdutta.async.http.NameValuePair;
import com.koushikdutta.ion.Ion;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

//import static java.net.Proxy.Type.HTTP;
//import android.widget.TextView;
//import android.widget.Toolbar;

public class ChatActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageButton SendMessageButton;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessage, blockingMessage;

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef,GroupNameRef, GroupMessageKeyRef;
    private String currentGroupName, currentUserID,currentUserName, currentDate,currentTime;

    List<MessageGroup> messages = new ArrayList<>();

    String currentName = "";
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(this,currentGroupName,Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);






        InitializeFields();

        getUserInfo();

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveMessageinfoToDatabase();

                userMessageInput.setText("");
//                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    displayMessage(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    displayMessage(dataSnapshot);
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



    private void getUserInfo() {
        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentUserName = dataSnapshot.child("name").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void InitializeFields() {
        mToolbar = (Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);

        SendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        userMessageInput = (EditText) findViewById(R.id.input_group_message);
//        displayTextMessage = (TextView) findViewById(R.id.group_chat_text_display);
//        mScrollView = (ScrollView) findViewById(R.id.my_scroll_view);

        blockingMessage = findViewById(R.id.blocking);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("NoOfWarning").child(currentUserID).child(currentGroupName);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("statement") && dataSnapshot.hasChild("warningCount"))){
                    Statement = dataSnapshot.child("statement").getValue().toString();
                    count = Integer.parseInt(dataSnapshot.child("warningCount").getValue().toString());
                    if(count>=3){
                        SendMessageButton.setVisibility(View.INVISIBLE);
                        userMessageInput.setVisibility(View.INVISIBLE);
                        blockingMessage.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    int count = 0;
    String Statement = "";
    boolean profanityEnabled = false;
    String new_message = "";
    private void SaveMessageinfoToDatabase() {
        String message = userMessageInput.getText().toString();
        String messageKey = GroupNameRef.push().getKey();



        if (TextUtils.isEmpty(message)){
            Toast.makeText(this,"Please write message first...",Toast.LENGTH_SHORT).show();
        }else{


            FirebaseDatabase.getInstance().getReference().child("SelectedProfanity").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String value = String.valueOf(dataSnapshot.getValue());
                    modeNo = Integer.parseInt(value);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            switch (modeNo){
                case 0:
                    //mode inapp
                    if(checkProfanityInApp(message)){
                        profcompletion(message);
                        message = new_message;
                        Toast.makeText(getApplicationContext(), "Profanity Detected", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Clear Text", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    //google api
                    if(checkProfanityGoogle(message)){
                        profcompletion(message);
                        message = new_message;
                        Toast.makeText(getApplicationContext(), "Profanity Detected", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Clear Text", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    // model
                    if(checkProfanityByModel(message)){
                        profcompletion(message);
                        message = new_message;
//                        Toast.makeText(getApplicationContext(), "Profanity Detected", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Clear Text", Toast.LENGTH_SHORT).show();
                    }
                default:
                    break;
            }


            Calendar calForDate =  Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime =  Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForDate.getTime());

            HashMap<String,Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

            GroupMessageKeyRef = GroupNameRef.child(messageKey);

            HashMap<String,Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);


            GroupMessageKeyRef.updateChildren(messageInfoMap);

        }

    }
    int modeNo = 0;

    public void profcompletion(String message){
        //=======================
        checkIfProfanityEnable();

        if(profanityEnabled){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("NoOfWarning").child(currentUserID).child(currentGroupName);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if((dataSnapshot.exists()) && (dataSnapshot.hasChild("statement") && dataSnapshot.hasChild("warningCount"))){
                        Statement = dataSnapshot.child("statement").getValue().toString();
                        count = Integer.parseInt(dataSnapshot.child("warningCount").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            Map map = new HashMap<>();
            getUserInfo();
            map.put("username",currentUserName);
            map.put("warningCount", count+1);
            map.put("statement",Statement+", "+message);

            ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(count==2){
                        AlertDialog.Builder alert = new AlertDialog.Builder(ChatActivity.this);
                        alert.setTitle("Warning");
                        alert.setMessage("Wait!! check before writing, Profanity Detected,\n If continue, You will be blocked next time");
                        alert.create();
                        alert.show();
                    }
                    if (count>=3){
                        AlertDialog.Builder alert = new AlertDialog.Builder(ChatActivity.this);
                        alert.setTitle("Warning");
                        alert.setMessage("You are blocked and details are under review");
                        alert.create();
                        alert.show();
                        DatabaseReference referenceBlocked = FirebaseDatabase.getInstance().getReference().child("BlockUser").child(currentUserID);
                        Map map = new HashMap();
                        map.put(currentGroupName,currentUserID+" - "+currentUserName+" - "+currentGroupName+" - "+Statement);
                        referenceBlocked.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                            }
                        });
                    }

                }
            });
        }



        //======================
    }

    public boolean checkProfanityByModel(String message){
        new_message = message;
        modelprediction modelpred = new modelprediction(message,getApplicationContext());
        Boolean predict = modelpred.finalCheck(message,getApplicationContext());

        predict = modelpred.getFinalres();

        Log.d("Checkpp","12 :"+predict);
        return predict;
    }

    public  void checkIfProfanityEnable(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("GroupPolicy");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild(currentGroupName))){
                    if(dataSnapshot.child(currentGroupName).getValue().toString().equals("true")){
                        profanityEnabled= true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    StringBuilder sb = null;
    String result = "";
    boolean status;
    private boolean checkProfanityGoogle(String message){
        new_message = message;
        message = message.replaceAll("\\s", "");

        Log.d("ZZ","Message: "+message);
        String url = "http://www.wdylike.appspot.com/?q="+message;
        Log.d("ZZ",url);

        Ion.with(getApplicationContext()).load(url).asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String res) {
                Log.d("ZZ", "1. :" + res);
                result = res;
                if (result.contains("true")){

                    status = true;
                }else{
                    status = false;
                }
            }
        });


        return status;

    }



    //Retrieving the contents of the specified page
//        String out = "--";
//        try {
//            String str = "";
//            InputStream content = (InputStream)url.getContent();
//            str+=content.read();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Log.d("ZX",out);
//        new_message = message;
//        if (out.contains("true")){
//            return true;
//        }else{
//            return false;
//        }

//        Scanner sc = null;
//        try {
//            sc = new Scanner(url.openStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //Instantiating the StringBuffer class to hold the result
//        StringBuffer sb = new StringBuffer();
//        while(sc.hasNext()) {
//            sb.append(sc.next());
//            //System.out.println(sc.next());
//        }
//        //Retrieving the String from the String Buffer object
//        String result = sb.toString();
//        System.out.println(result);
//        //Removing the HTML tags
//        result = result.replaceAll("<[^>]*>", "");

//        new_message = message;
//        if (result.contains("true")){
//            return true;
//        }else{
//            return false;
//        }
//    }

    private boolean checkProfanityInApp(String message){
        String input=message;


        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("word_filter.csv")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String output = BadWordFilter.getCensoredText(input,reader);
        Log.d("InputOutputs",input+" "+output);
//        System.out.println(output);
        if (input.equals(output)){
            return false;
        }else{
            new_message = output;
            return true;
        }
//        return true;
    }

    private void displayMessage(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        MessageGroup message = new MessageGroup();

        while (iterator.hasNext()){
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

            message.setDate(chatDate);
            message.setTime(chatTime);
            message.setMessage(chatMessage);
            message.setName(chatName);

            RetrieveUserName();

            Log.d("Checkx",currentUserName+"vv");

            if (currentUserName.equals(chatName)){
                message.setCurrentUser(true);
            }else{
                message.setCurrentUser(false);
            }

//            displayTextMessage.append(chatName+":\n"+chatMessage+"\n"+chatTime+"   "+chatDate+"\n\n\n");
//            displayTextMessage.append("qwert");
            messages.add(message);
//            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.group_messages_list_of_users);
        MessageAdapterGroup adapter = new MessageAdapterGroup(messages);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.smoothScrollToPosition(messages.size()-1);
    }

    private void RetrieveUserName() {
        final String[] name = {""};
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef.child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                            name[0] = dataSnapshot.child("name").getValue().toString();
                            Log.d("Checky",name[0]);
                            Log.d("Checkz",dataSnapshot.child("name").getValue().toString());
                            Log.d("Checka",name[0]);
//                            return name[0];
                            currentUserName = name[0];

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

    }


}
