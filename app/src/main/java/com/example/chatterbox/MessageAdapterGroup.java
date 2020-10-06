package com.example.chatterbox;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapterGroup extends RecyclerView.Adapter<MessageAdapterGroup.MessageGroupViewHolder> {
    private List<MessageGroup> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;



    public MessageAdapterGroup(List<MessageGroup> userMessagesList){
        this.userMessagesList = userMessagesList;

    }

    public class MessageGroupViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMessageText, receiverMessageText;

        public MessageGroupViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = itemView.findViewById(R.id.group_message_text);
        }
    }


    @NonNull
    @Override
    public MessageAdapterGroup.MessageGroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.single_group_chat,viewGroup,false);

//        mAuth = FirebaseAuth.getInstance();

        return new MessageGroupViewHolder(view);

//        ViewHolder viewHolder = new ViewHolder(view);
//        return viewHolder;



//        return new MessageAdapterGroup.MessageGroupViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MessageGroupViewHolder messageGroupViewHolder, int i) {
        MessageGroup messages = userMessagesList.get(i);

        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString str1= new SpannableString(messages.getName());
        str1.setSpan(new ForegroundColorSpan(Color.RED), 0, str1.length(), 0);
        builder.append(str1);

        String text = "\n"+messages.getMessage();
        text+="\n\n"+messages.getDate()+" "+messages.getTime();
//        text+=MainActivity.currentUserName+"Hi";
        Log.d("CheckUser",MainActivity.currentUserName);

        SpannableString str2= new SpannableString(text);
        str2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, str2.length(), 0);
        builder.append(str2);

        if (messages.isCurrentUser()){
            messageGroupViewHolder.senderMessageText.setText( builder, TextView.BufferType.SPANNABLE);
            messageGroupViewHolder.receiverMessageText.setVisibility(View.INVISIBLE);
        }else{
            messageGroupViewHolder.receiverMessageText.setText( builder, TextView.BufferType.SPANNABLE);
            messageGroupViewHolder.senderMessageText.setVisibility(View.INVISIBLE);
        }

//        TextView tv = (TextView) view.findViewById(android.R.id.text1);




//        messageGroupViewHolder.receiverMessageText.setText(text);

//        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(view.getContext(),"click on item: "+myListData.getDescription(),Toast.LENGTH_LONG).show();
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
