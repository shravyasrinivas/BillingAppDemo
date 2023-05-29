
package com.example.BillingDemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.BillingDemo.databinding.ActivityUploaddataBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class uploaddata extends AppCompatActivity {

    ActivityUploaddataBinding binding;
    String billno, name, place,amount,balance ;

    FirebaseDatabase db;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploaddataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                billno = binding.billno.getText().toString();
                name = binding.name.getText().toString();
                place = binding.place.getText().toString();
                amount = binding.amount.getText().toString();
                balance = binding.balance.getText().toString();

                if (!billno.isEmpty() && !name.isEmpty() &&!place.isEmpty()&&  !amount.isEmpty() && !balance.isEmpty()){

                    UserHelperJava users = new UserHelperJava(billno,name,place,amount,balance);
                    db = FirebaseDatabase.getInstance();
                    reference = db.getReference("Sales");
                    reference.child(billno).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            binding.billno.setText("");
                            binding.name.setText("");
                            binding.place.setText("");
                            binding.amount.setText("");
                            binding.balance.setText("");
                            Toast.makeText(uploaddata.this,"Successfully Uploaded",Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            }
        });
    }
}



//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.example.BillingDemo.databinding.ActivityMainBinding;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//public class uploaddata extends AppCompatActivity {
//    private EditText rname, rbillno, ramount,rbalance,rplace;
//    private Button savebtn;
//    ActivityMainBinding binding;
//    String firstName, lastName, age, userName;
////    FirebaseDatabase db;
////    DatabaseReference reference;
//    // creating a variable for our
//    // Firebase Database.
//    FirebaseDatabase firebaseDatabase;
//
//    // creating a variable for our Database
//    // Reference for Firebase.
//    DatabaseReference databaseReference;
//
//    // creating a variable for
//    // our object class
//    UserHelperJava sales;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_uploaddata);
//
//        // initializing our edittext and button
//       rname = findViewById(R.id.name);
//        rplace = findViewById(R.id.place);
//        ramount = findViewById(R.id.amount);
//        rbalance =findViewById(R.id.balance);
//        rbillno=findViewById(R.id.billno);
//
//        // below line is used to get the
//        // instance of our FIrebase database.
//        firebaseDatabase = FirebaseDatabase.getInstance();
//
//        // below line is used to get reference for our database.
//        databaseReference = firebaseDatabase.getReference("Sales");
//
//        // initializing our object
//        // class variable.
//        sales = new UserHelperJava();
//
//        savebtn = findViewById(R.id.save);
//
//        // adding on click listener for our button.
//        savebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                // getting text from our edittext fields.
//                String name = rname.getText().toString();
//                String place = rplace.getText().toString();
//                String billno = rbillno.getText().toString();
//                String amount=ramount.getText().toString();
//                String balance=rbalance.getText().toString();
//                // below line is for checking whether the
//                // edittext fields are empty or not.
//                if (TextUtils.isEmpty(name) && TextUtils.isEmpty(place) && TextUtils.isEmpty(billno) && TextUtils.isEmpty(amount) && TextUtils.isEmpty(balance)) {
//                    // if the text fields are empty
//                    // then show the below message.
//                    Toast.makeText(uploaddata.this, "Please add some data.", Toast.LENGTH_SHORT).show();
//                } else {
//                    // else call the method to add
//                    // data to our database.
//                    addDatatoFirebase(name, billno, place,amount,balance);
//                }
//            }
//        });
//    }
//
//    private void addDatatoFirebase(String name, String billno, String place, String amount, String balance) {
//        // below 3 lines of code is used to set
//        // data in our object class.
//        sales.setName(name);
//        sales.setAmount(amount);
//        sales.setBillno(billno);
//        sales.setPlace(place);
//        sales.setBalance(balance);
//
//        // we are use add value event listener method
//        // which is called with database reference.
//        databaseReference.child(billno).setValue(sales).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//
//                // inside the method of on Data change we are setting
//                // our object class to our database reference.
//                // data base reference will sends data to firebase.
//                databaseReference.setValue(sales);
//
//                // after adding this data we are showing toast message.
//                Toast.makeText(uploaddata.this, "data added", Toast.LENGTH_SHORT).show();
//            }
//
//
//            public void onCancelled(@NonNull DatabaseError error) {
//                // if the data is not added or it is cancelled then
//                // we are displaying a failure toast message.
//                Toast.makeText(uploaddata.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}