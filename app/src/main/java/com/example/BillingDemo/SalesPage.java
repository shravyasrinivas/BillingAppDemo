package com.example.BillingDemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SalesPage extends AppCompatActivity {
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
//    Map<String, String> question = dataSnapshot.child("Sales").getValue(Map.class);
    String[] name={"Apple","Banana","Grapes","guava"};
    FloatingActionButton fab ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_page);

        listView=findViewById(R.id.listview);
        fab=findViewById(R.id.fab);
        arrayAdapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name);
        listView.setAdapter(arrayAdapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    //Snackbar.make(view, "Pick your file", Snackbar.LENGTH_LONG)
                       //     .setAction("Action", null).show();
                Intent intent=new Intent(getApplicationContext(), UploadData.class);
                startActivity(intent);
                finish();
                }
            });
        }



    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
