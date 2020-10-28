package com.example.chatterbox.peer_to_peer_chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
//import android.support.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatterbox.MainActivity;
import com.example.chatterbox.R;
import com.example.chatterbox.helper.imageViewerActivity;
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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;



    public MessageAdapter(List<Messages> userMessagesList){
        this.userMessagesList = userMessagesList;

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMessageText, receiverMessageText;
        public CircleImageView receiverProfileImage;
        public ImageView messageSenderPicture, messageReceiverPicture;




        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = itemView.findViewById(R.id.message_profile_image);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
        }
    }








    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View view = LayoutInflater.from(viewGroup.getContext())
               .inflate(R.layout.custom_messages_layout,viewGroup,false);

       mAuth = FirebaseAuth.getInstance();



       return new MessageViewHolder(view);

    }





    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, final int i) {
        final String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(i);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image")){
                    String receiverImage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image).into(messageViewHolder.receiverProfileImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        messageViewHolder.receiverMessageText.setVisibility(View.GONE);
        messageViewHolder.receiverProfileImage.setVisibility(View.GONE);
        messageViewHolder.senderMessageText.setVisibility(View.GONE);
        messageViewHolder.messageSenderPicture.setVisibility(View.GONE);
        messageViewHolder.messageReceiverPicture.setVisibility(View.GONE);



        if (fromMessageType.equals("text")){


            if (fromUserID.equals(messageSenderId)){
                messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                messageViewHolder.senderMessageText.setTextColor(Color.BLACK);
                messageViewHolder.senderMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
            }else{
//
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);

                messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.receiver_message_layout);
                messageViewHolder.receiverMessageText.setTextColor(Color.BLACK);
                messageViewHolder.receiverMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());



            }

        }
        else if (fromMessageType.equals("image")){
            if (fromUserID.equals(messageSenderId)){
                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageSenderPicture);
            }else{
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageReceiverPicture);

            }
        }
        else if (fromMessageType.equals("pdf")|| fromMessageType.equals("docx")){
            if (fromUserID.equals(messageSenderId)){
                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                messageViewHolder.messageSenderPicture.setBackgroundResource(R.drawable.file);
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatterbox-80b5b.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=7b87eec2-88fc-4454-b12e-b3485962f268").into(messageViewHolder.messageSenderPicture);

            }else{
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);

                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatterbox-80b5b.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=7b87eec2-88fc-4454-b12e-b3485962f268").into(messageViewHolder.messageReceiverPicture);


            }

        }


        if (fromUserID.equals(messageSenderId)){
            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userMessagesList.get(i).getType().equals("pdf")||userMessagesList.get(i).getType().equals("docx")){
                        CharSequence options[] = new CharSequence[]{
                                "Delete For me",
                                "Download and View This Document",
                                "Cancel",
                                "Delete For Everyone"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                if (position ==0){
                                    deleteSentMessage(position,messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }else if (position==1){
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(i).getMessage()));
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                }else if (position ==3){
                                    deleteMessageForEveryOne(position,messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                    else if (userMessagesList.get(i).getType().equals("text")){
                        CharSequence options[] = new CharSequence[]{
                                "Delete For me",
                                "Cancel",
                                "Delete For Everyone"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                if (position ==0){
                                    deleteSentMessage(position,messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }else if (position ==2){
                                    deleteMessageForEveryOne(position,messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                }
                            }
                        });
                        builder.show();
                    }
                    else if (userMessagesList.get(i).getType().equals("image")){
                        CharSequence options[] = new CharSequence[]{
                                "Delete For me",
                                "View This Image",
                                "Cancel",
                                "Delete For Everyone"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                if (position ==0){
                                    deleteSentMessage(position,messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                }else if (position==1){
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), imageViewerActivity.class);
                                    intent.putExtra("url",userMessagesList.get(position).getMessage());
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }else if (position ==3){
                                    deleteMessageForEveryOne(position,messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        }
        else{

            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userMessagesList.get(i).getType().equals("pdf")||userMessagesList.get(i).getType().equals("docx")){
                        CharSequence options[] = new CharSequence[]{
                                "Delete For me",
                                "Download and View This Document",
                                "Cancel"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                if (position ==0){
                                    deleteReceiveMessage(position,messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                }else if (position==1){
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(i).getMessage()));
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                }
                            }
                        });
                        builder.show();
                    }
                    else if (userMessagesList.get(i).getType().equals("text")){
                        CharSequence options[] = new CharSequence[]{
                                "Delete For me",
                                "Cancel"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                if (position ==0){
                                    deleteReceiveMessage(position,messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                }
                            }
                        });
                        builder.show();
                    }
                    else if (userMessagesList.get(i).getType().equals("image")){
                        CharSequence options[] = new CharSequence[]{
                                "Delete For me",
                                "View This Image",
                                "Cancel"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                if (position ==0){
                                    deleteReceiveMessage(position,messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                }else if (position==1){
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(),imageViewerActivity.class);
                                    intent.putExtra("url",userMessagesList.get(position).getMessage());
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
            
        }


    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


    private void deleteSentMessage(final int position, final MessageViewHolder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Message")
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(),"Deleted Successfully",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(holder.itemView.getContext(),"Error Occurred",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void deleteReceiveMessage(final int position, final MessageViewHolder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Message")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(),"Deleted Successfully",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(holder.itemView.getContext(),"Error Occurred",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void deleteMessageForEveryOne(final int position, final MessageViewHolder holder){
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Message")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    rootRef.child("Message")
                            .child(userMessagesList.get(position).getFrom())
                            .child(userMessagesList.get(position).getTo())
                            .child(userMessagesList.get(position).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(holder.itemView.getContext(),"Deleted Successfully",Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    Toast.makeText(holder.itemView.getContext(),"Error Occurred",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }




}
