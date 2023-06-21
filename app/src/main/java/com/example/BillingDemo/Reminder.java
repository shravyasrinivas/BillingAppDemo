package com.example.BillingDemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Reminder extends AppCompatActivity {
    RecyclerView recyclerView;
    MyAdapter adapter;
   DatabaseReference databaseReference;

    ArrayList<UserHelperJava> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        UserHelperJava userHelper = new UserHelperJava();
        recyclerView = findViewById(R.id.userRecyclerView);

        ArrayList<UserHelperJava> list;

        int daysPending = 1;
//
//        databaseReference = FirebaseDatabase.getInstance().getReference("Sales");
//
//        filterByDueDatePending(1);
//    }
//
//    private void filterByDueDatePending(int daysPending) {
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ArrayList<UserHelperJava> userList = new ArrayList<>();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    UserHelperJava user = dataSnapshot.getValue(UserHelperJava.class);
//                    if (user.getDaysPending() == daysPending) {
//                        userList.add(user);
//                    }
//                }
//                adapter.setList(userList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(Reminder.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
        databaseReference = FirebaseDatabase.getInstance().getReference("Sales");
        list = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserHelperJava user = snapshot.getValue(UserHelperJava.class);
                    if (user.getDaysPending() == daysPending){
                    list.add(user);
                    }
                }
                adapter = new MyAdapter(Reminder.this, list);
                recyclerView.setAdapter(adapter);

//                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Reminder.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }}
