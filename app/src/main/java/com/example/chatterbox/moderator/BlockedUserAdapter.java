package com.example.chatterbox.moderator;

import android.content.Context;
import android.content.DialogInterface;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AlertDialog;
//import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatterbox.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class BlockedUserAdapter extends RecyclerView.Adapter<BlockedUserAdapter.BlockedUserViewHolder> {
    private List<BlockedUser> userList;
    private Context context;

    public BlockedUserAdapter(Context context,List<BlockedUser> userList){
        this.userList = userList;
        this.context = context;

    }

    public class BlockedUserViewHolder extends RecyclerView.ViewHolder{

        public TextView userName, groupEnrolled,statement1,statement2,statement3;
        public Button reviewAccept,reviewCancel;

        public BlockedUserViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            groupEnrolled = itemView.findViewById(R.id.group_name);
            statement1 = itemView.findViewById(R.id.statement1);
            statement2 = itemView.findViewById(R.id.statement2);
            statement3 = itemView.findViewById(R.id.statement3);
            reviewAccept = itemView.findViewById(R.id.review_accept_button);
            reviewCancel = itemView.findViewById(R.id.review_cancel_button);
        }
    }


    @NonNull
    @Override
    public BlockedUserAdapter.BlockedUserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.blocked_user_single_list,viewGroup,false);

        return new BlockedUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BlockedUserViewHolder blockedUserViewHolder, final int i) {
        final BlockedUser users = userList.get(i);

        blockedUserViewHolder.userName.setText("Username : "+users.getUserName());
        blockedUserViewHolder.groupEnrolled.setText("Member of : "+users.getGroupEnrolled());
        blockedUserViewHolder.statement1.setText("1. "+users.getStatement1());
        blockedUserViewHolder.statement2.setText("2. "+users.getStatement2());
        blockedUserViewHolder.statement3.setText("3. "+users.getStatement3());

        Log.d("AS",users.getUserName()+"-"+users.getGroupEnrolled()+"-"+users.getStatement1()+"-"+users.getStatement2()+"-"+users.getStatement3());

        blockedUserViewHolder.reviewAccept.setVisibility(View.VISIBLE);
        blockedUserViewHolder.reviewCancel.setVisibility(View.VISIBLE);

        blockedUserViewHolder.reviewAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performAcceptOperation(users.getUserId(),users.getUserName(),users.getGroupEnrolled(),i);
            }
        });

        blockedUserViewHolder.reviewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("cancelrev","Step1");
                AlertDialog.Builder builder = new AlertDialog.Builder(blockedUserViewHolder.itemView.getContext());
                Log.d("cancelrev","Step2");
                builder.setTitle("Warning");
                builder.setMessage("This User will permanently blocked from given group\nDo you want to continue.");
                Log.d("cancelrev","Step3");
                builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("cancelrev","Stepfinal");
                        performCancelOperation(users.getUserId(),users.getUserName(),users.getGroupEnrolled(),i);
                        Toast.makeText(blockedUserViewHolder.itemView.getContext(), "Blocked Permanently", Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                Log.d("cancelrev","Step4");
                builder.create();
                builder.show();
                Log.d("cancelrev","Step5");
            }
        });



    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void performAcceptOperation(String userID, String userName, String groupEnrolled, final int pos){
        Log.d("perform",userID+"----"+userName+"----"+groupEnrolled);
        DatabaseReference dbref1 = FirebaseDatabase.getInstance().getReference().child("BlockUser").child(userID);
        dbref1.child(groupEnrolled).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Log.d("perform","complete1");
                notifyItemRemoved(pos);
            }
        });
        DatabaseReference dbref2 = FirebaseDatabase.getInstance().getReference().child("NoOfWarning").child(userID);
        dbref2.child(groupEnrolled).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Log.d("perform","complete2");
            }
        });
    }

    public void performCancelOperation(String userID, String userName, String groupEnrolled, final int pos){
        Log.d("perform",userID+"----"+userName+"----"+groupEnrolled);
        DatabaseReference dbref1 = FirebaseDatabase.getInstance().getReference().child("BlockUser").child(userID);
        dbref1.child(groupEnrolled).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Log.d("perform","complete1");
                notifyItemRemoved(pos);
            }
        });

    }




}
