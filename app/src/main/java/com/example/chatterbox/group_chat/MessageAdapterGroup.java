package com.example.chatterbox.group_chat;

import android.graphics.Color;
//import android.support.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatterbox.MainActivity;
import com.example.chatterbox.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

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
        Log.d("CheckUser", MainActivity.currentUserName);

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
