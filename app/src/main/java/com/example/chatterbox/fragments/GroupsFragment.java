package com.example.chatterbox.fragments;


import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.app.Fragment;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.chatterbox.group_chat.ChatActivity;
import com.example.chatterbox.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private View groupFragmentView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_groups = new ArrayList<>();
    private DatabaseReference groupRef;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView  = inflater.inflate(R.layout.fragment_groups, container, false);
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        InitializeFields();
        RetrieveAndDisplayGroups();

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                String currentGroupName = adapterView.getItemAtPosition(pos).toString();
                Intent groupChatIntent = new Intent(getContext(), ChatActivity.class);
                groupChatIntent.putExtra("groupName",currentGroupName);
                startActivity(groupChatIntent);

            }
        });
        
        return groupFragmentView;
    }

    private void RetrieveAndDisplayGroups() {
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                list_of_groups.clear();
                list_of_groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitializeFields() {
        list_view = (ListView) groupFragmentView.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(),R.layout.list_textview,list_of_groups);
        list_view.setAdapter(arrayAdapter);
    }

}
