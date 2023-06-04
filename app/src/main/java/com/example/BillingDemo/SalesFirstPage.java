package com.example.BillingDemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SalesFirstPage extends AppCompatActivity {
RecyclerView recyclerView;
ArrayList<UserHelperJava> list;
DatabaseReference databaseReference;
MyAdapter adapter;
    FloatingActionButton fab ;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SalesFirstPage.this,MainActivity.class));
  finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_first_page);
        recyclerView = findViewById(R.id.recycleview);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Pick your file", Snackbar.LENGTH_LONG)
                //     .setAction("Action", null).show();
                Intent intent = new Intent(getApplicationContext(), uploaddata.class);
                startActivity(intent);
                finish();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Sales");
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear(); // Clear the list here
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserHelperJava user = snapshot.getValue(UserHelperJava.class);
                    list.add(user);
                }
                adapter = new MyAdapter(SalesFirstPage.this, list);
                recyclerView.setAdapter(adapter);
//                ItemTouchHelper itemTouchHelper=new ItemTouchHelper(simpleCallback);
//                itemTouchHelper.attachToRecyclerView(recyclerView);
                adapter.notifyDataSetChanged(); // Notify the adapter of the data change
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any database error
                Toast.makeText(SalesFirstPage.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        android.widget.SearchView searchView=(android.widget.SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }}
//    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0 , ItemTouchHelper.RIGHT ) {
//        @Override
//        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//            return false;
//        }
//
//        @Override
//        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(SalesFirstPage.this);
//            builder.setTitle("Delete Task");
//            builder.setMessage("Confirm delete?");
//
//            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    int position = viewHolder.getAdapterPosition();
//
//                    // Get the reference to the item in the database
//                    String itemId = list.get(position).getBillno(); // Assuming you have a getId() method in UserHelperJava class
//                    DatabaseReference itemRef = databaseReference.child(itemId);
//
//                    // Remove the item from the RecyclerView's list
//                    list.remove(position);
//                    adapter.notifyItemRemoved(position);
//
//                    // Delete the item from the Firebase database
//                    itemRef.removeValue()
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    // Item successfully deleted from the database
//                                    Toast.makeText(SalesFirstPage.this, "Deleted successfully ", Toast.LENGTH_SHORT).show();
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    // Failed to delete item from the database
//                                    Toast.makeText(SalesFirstPage.this, "Not deleted ", Toast.LENGTH_SHORT).show();
//
//                                }
//                            });
//                }
//            });
//
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
//                }
//            });
//
//            builder.show();
//        }
//    };}

//        @Override
//        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(SalesFirstPage.this);
//            builder.setTitle("Delete Task");
//            builder.setMessage("Confirm delete ?");
//           builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                   int position = viewHolder.getAdapterPosition();
//                    list.remove(position);
//                    adapter.notifyItemRemoved(position);
//                }
//          });
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//               @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
//               }
//            });
//            builder.show();
//        }
//
//    };


