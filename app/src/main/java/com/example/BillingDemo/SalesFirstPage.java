package com.example.BillingDemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.navigationdrawer.AboutFragment;
//import com.example.navigationdrawer.HomeFragment;
//import com.example.navigationdrawer.SettingsFragment;
//import com.example.navigationdrawer.ShareFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SalesFirstPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;

    RecyclerView recyclerView;
    ArrayList<UserHelperJava> list;
    DatabaseReference databaseReference;
    MyAdapter adapter;
    FloatingActionButton fab;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_first_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        //navigationView.inflateMenu(R.menu.nav_menu);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();
            View headerView = navigationView.getHeaderView(0);
            TextView textViewEmail = headerView.findViewById(R.id.textViewEmail);
            textViewEmail.setText(email);

        }
        recyclerView = findViewById(R.id.recycleview);
        fab = findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserHelperJava user = snapshot.getValue(UserHelperJava.class);
                    list.add(user);
                }
                adapter = new MyAdapter(SalesFirstPage.this, list);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SalesFirstPage.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
                UserHelperJava swipedUser = list.get(swipedPosition);
                String swipedUserKey = swipedUser.getBillno();

                AlertDialog.Builder builder = new AlertDialog.Builder(SalesFirstPage.this);
                builder.setTitle("Confirm Delete");
                builder.setMessage("Are you sure you want to delete this item?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        list.remove(swipedPosition);
                        adapter.notifyItemRemoved(swipedPosition);

                        DatabaseReference swipedUserRef = databaseReference.child(swipedUserKey);
                        swipedUserRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SalesFirstPage.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SalesFirstPage.this, "Failed to delete item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adapter.notifyItemChanged(swipedPosition);
                    }
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        adapter.notifyItemChanged(swipedPosition);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        if (savedInstanceState == null) {
            //Intent homeIntent = new Intent(this, SalesFirstPage.class);
            //startActivity(homeIntent);
            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                Toast.makeText(this, "home!", Toast.LENGTH_SHORT).show();
                Intent homeIntent = new Intent(this, MainActivity.class);
                startActivity(homeIntent);
                break;

            case R.id.nav_settings:
                Toast.makeText(this, "settings!", Toast.LENGTH_SHORT).show();
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;

            case R.id.nav_report:
                Toast.makeText(this, "report!", Toast.LENGTH_SHORT).show();
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ShareFragment()).commit();
                break;

            case R.id.nav_about:
                Toast.makeText(this, "about!", Toast.LENGTH_SHORT).show();
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                Intent intent3=new Intent(getApplicationContext(),Login.class);
                startActivity(intent3);
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}


//package com.example.BillingDemo;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.ItemTouchHelper;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.SearchView;
//import android.widget.Toast;
//import android.widget.Toolbar;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SalesFirstPage extends AppCompatActivity {
//
//    RecyclerView recyclerView;
//    ArrayList<UserHelperJava> list;
//    DatabaseReference databaseReference;
//    MyAdapter adapter;
//    FloatingActionButton fab;
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(SalesFirstPage.this, MainActivity.class));
//        finish();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sales_first_page);
//
//        recyclerView = findViewById(R.id.recycleview);
//        fab = findViewById(R.id.fab);
//
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), uploaddata.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        databaseReference = FirebaseDatabase.getInstance().getReference("Sales");
//        list = new ArrayList<>();
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                list.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    UserHelperJava user = snapshot.getValue(UserHelperJava.class);
//                    list.add(user);
//                }
//                adapter = new MyAdapter(SalesFirstPage.this, list);
//                recyclerView.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(SalesFirstPage.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                int swipedPosition = viewHolder.getAdapterPosition();
//                UserHelperJava swipedUser = list.get(swipedPosition);
//                String swipedUserKey = swipedUser.getBillno();
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(SalesFirstPage.this);
//                builder.setTitle("Confirm Delete");
//                builder.setMessage("Are you sure you want to delete this item?");
//                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        list.remove(swipedPosition);
//                        adapter.notifyItemRemoved(swipedPosition);
//
//                        DatabaseReference swipedUserRef = databaseReference.child(swipedUserKey);
//                        swipedUserRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(SalesFirstPage.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(SalesFirstPage.this, "Failed to delete item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        adapter.notifyItemChanged(swipedPosition);
//                    }
//                });
//                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialogInterface) {
//                        adapter.notifyItemChanged(swipedPosition);
//                    }
//                });
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//            }
//        };
//
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        MenuItem menuItem=menu.findItem(R.id.action_search);
//        android.widget.SearchView searchView=(android.widget.SearchView) menuItem.getActionView();
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//        searchView.setQueryHint("Search");
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
//                return false;
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//    }}
//
//
//


//package com.example.BillingDemo;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.ItemTouchHelper;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.SearchView;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SalesFirstPage extends AppCompatActivity {
//
//    RecyclerView recyclerView;
//    ArrayList<UserHelperJava> list;
//    DatabaseReference databaseReference;
//    MyAdapter adapter;
//    FloatingActionButton fab;
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(SalesFirstPage.this, MainActivity.class));
//        finish();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sales_first_page);
//
//        recyclerView = findViewById(R.id.recycleview);
//        fab = findViewById(R.id.fab);
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), uploaddata.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        databaseReference = FirebaseDatabase.getInstance().getReference("Sales");
//        list = new ArrayList<>();
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                list.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    UserHelperJava user = snapshot.getValue(UserHelperJava.class);
//                    list.add(user);
//                }
//                adapter = new MyAdapter(SalesFirstPage.this, list);
//                recyclerView.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(SalesFirstPage.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                int swipedPosition = viewHolder.getAdapterPosition();
//                UserHelperJava swipedUser = list.get(swipedPosition);
//                String swipedUserKey = swipedUser.getBillno();
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(SalesFirstPage.this);
//                builder.setTitle("Confirm Delete");
//                builder.setMessage("Are you sure you want to delete this item?");
//                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        list.remove(swipedPosition);
//                        adapter.notifyItemRemoved(swipedPosition);
//
//                        DatabaseReference swipedUserRef = databaseReference.child(swipedUserKey);
//                        swipedUserRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(SalesFirstPage.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(SalesFirstPage.this, "Failed to delete item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        adapter.notifyItemChanged(swipedPosition);
//                    }
//                });
//                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialogInterface) {
//                        adapter.notifyItemChanged(swipedPosition);
//                    }
//                });
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//            }
//        };
//
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        MenuItem menuItem=menu.findItem(R.id.action_search);
//        android.widget.SearchView searchView=(android.widget.SearchView) menuItem.getActionView();
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//        searchView.setQueryHint("Search");
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
//                return false;
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//    }}
//
//
//
//
////-----//
//
////package com.example.BillingDemo;
////
////import androidx.annotation.NonNull;
////import androidx.appcompat.app.AlertDialog;
////import androidx.appcompat.app.AppCompatActivity;
////import androidx.recyclerview.widget.ItemTouchHelper;
////import androidx.recyclerview.widget.LinearLayoutManager;
////import androidx.recyclerview.widget.RecyclerView;
////
////import android.content.DialogInterface;
////import android.content.Intent;
////import android.os.Bundle;
////import android.view.Menu;
////import android.view.MenuItem;
////import android.view.SearchEvent;
////import android.view.View;
////import android.widget.SearchView;
////import android.widget.Toast;
////
////import com.google.android.gms.tasks.OnFailureListener;
////import com.google.android.gms.tasks.OnSuccessListener;
////import com.google.android.material.floatingactionbutton.FloatingActionButton;
////import com.google.firebase.database.DataSnapshot;
////import com.google.firebase.database.DatabaseError;
////import com.google.firebase.database.DatabaseReference;
////import com.google.firebase.database.FirebaseDatabase;
////import com.google.firebase.database.ValueEventListener;
////
////import java.util.ArrayList;
////
////public class SalesFirstPage extends AppCompatActivity {
////    RecyclerView recyclerView;
////    ArrayList<UserHelperJava> list;
////    DatabaseReference databaseReference;
////    MyAdapter adapter;
////    FloatingActionButton fab;
////
////    @Override
////    public void onBackPressed() {
////        super.onBackPressed();
////        startActivity(new Intent(SalesFirstPage.this, MainActivity.class));
////        finish();
////    }
////
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_sales_first_page);
////        recyclerView = findViewById(R.id.recycleview);
////        fab = findViewById(R.id.fab);
////        fab.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                //Snackbar.make(view, "Pick your file", Snackbar.LENGTH_LONG)
////                //     .setAction("Action", null).show();
////                Intent intent = new Intent(getApplicationContext(), uploaddata.class);
////                startActivity(intent);
////                finish();
////            }
////        });
////
////        databaseReference = FirebaseDatabase.getInstance().getReference("Sales");
////        list = new ArrayList<>();
////        recyclerView.setLayoutManager(new LinearLayoutManager(this));
////        databaseReference.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                list.clear(); // Clear the list here
////                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
////                    UserHelperJava user = snapshot.getValue(UserHelperJava.class);
////                    list.add(user);
////                }
////                adapter = new MyAdapter(SalesFirstPage.this, list);
////                recyclerView.setAdapter(adapter);
//////                ItemTouchHelper itemTouchHelper=new ItemTouchHelper(simpleCallback);
//////                itemTouchHelper.attachToRecyclerView(recyclerView);
////                adapter.notifyDataSetChanged(); // Notify the adapter of the data change
////            }
////
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////                // Handle any database error
////                Toast.makeText(SalesFirstPage.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
////            }
////        });
////
////
////    }
////
////
////    @Override
////    public boolean onCreateOptionsMenu(Menu menu) {
////        getMenuInflater().inflate(R.menu.menu, menu);
////        MenuItem menuItem = menu.findItem(R.id.action_search);
////        android.widget.SearchView searchView = (android.widget.SearchView) menuItem.getActionView();
////        searchView.setMaxWidth(Integer.MAX_VALUE);
////        searchView.setQueryHint("Search");
////        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
////            @Override
////            public boolean onQueryTextSubmit(String query) {
////                return false;
////            }
////
////            @Override
////            public boolean onQueryTextChange(String newText) {
////                adapter.getFilter().filter(newText);
////                return false;
////            }
////        });
////        return super.onCreateOptionsMenu(menu);
////    }
////
////}
////
////
////
////
////
////
