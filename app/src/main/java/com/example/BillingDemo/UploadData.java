package com.example.BillingDemo;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.BillingDemo.databinding.ActivityUploaddataBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UploadData extends AppCompatActivity {

    ActivityUploaddataBinding binding;
    String billno, name1, place, amount1, balance1, selectedDate, duedate1, fatherName, aadharNum, phoneNum;

    FirebaseDatabase db;
    DatabaseReference reference;

    private static final long CHECK_INTERVAL = TimeUnit.DAYS.toMillis(1); // Interval to check daysPending (1 day)
    private static final int PERMISSION_REQUEST_SMS = 123;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(UploadData.this, SalesFirstPage.class));
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploaddataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SMS);
        }




        binding.Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }

        });
        binding.duedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDueDatePicker();
            }

        });


        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billno = binding.billno.getText().toString();
                name1 = binding.name.getText().toString();
                fatherName = binding.fatherName.getText().toString();
                aadharNum = binding.aadharNumber.getText().toString();
                phoneNum = binding.phoneNumber.getText().toString();
                place = binding.place.getText().toString();
                amount1= binding.amount.getText().toString();
                balance1 = binding.balance.getText().toString();
                selectedDate = binding.Date.getText().toString();
                duedate1 = binding.duedate.getText().toString();


                if (!billno.isEmpty() && !name1.isEmpty() && !place.isEmpty() && !amount1.isEmpty() && !balance1.isEmpty()) {
                    UserHelperJava users = new UserHelperJava(selectedDate, billno, name1,fatherName,aadharNum,phoneNum ,place, amount1, balance1, duedate1);
                    db = FirebaseDatabase.getInstance();

                    reference = db.getReference("Sales");


                    reference.child(billno).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            binding.billno.setText("");
                            binding.name.setText("");
                            binding.fatherName.setText("");
                            binding.place.setText("");
                            binding.aadharNumber.setText("");
                            binding.phoneNumber.setText("");
                            binding.amount.setText("");
                            binding.balance.setText("");
                            binding.Date.setText("");
                            binding.duedate.setText("");


                            Toast.makeText(UploadData.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                            if (isDueDateTomorrow(duedate1) || isDueDateOneDayDifference(duedate1)) {
                                sendReminderSMS(phoneNum,name1,balance1,duedate1);
                            }
                        }

                    });
                }
            }
        });

    }
    public boolean isDueDateTomorrow(String dueDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dueDateObj = dateFormat.parse(dueDate);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Add 1 day to the current date
            Date tomorrow = calendar.getTime();
            return dateFormat.format(dueDateObj).equals(dateFormat.format(tomorrow));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isDueDateOneDayDifference(String dueDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dueDateObj = dateFormat.parse(dueDate);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Add 1 day to the current date
            Date tomorrow = calendar.getTime();
            long differenceInDays = TimeUnit.DAYS.convert(dueDateObj.getTime() - tomorrow.getTime(), TimeUnit.MILLISECONDS);
            return differenceInDays == 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void sendReminderSMS(String phoneNumber,String name, String balance, String dueDate) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String message = "Dear " + name + ", this is a reminder from SG and SR Jewellery.Your due is tomorrow.Please note that you have a balance amount of " + balance;
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(UploadData.this, "SMS notification sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(UploadData.this, "Failed to send SMS notification", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
//    private void scheduleNotification() {
//        // Parse the selected due date
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        try {
//            Date dueDate = dateFormat.parse(duedate);
//            Date currentDate = new Date();
//
//            // Calculate the difference between current date and due date in days
//            long differenceInDays = TimeUnit.DAYS.convert(dueDate.getTime() - currentDate.getTime(), TimeUnit.MILLISECONDS);
//
//            // Check if the difference is equal to 1 day
//            if (differenceInDays == 1) {
//                // Send notification with the balance amount to the customer's phone number
//                sendNotification(phoneNum, "Dear " + name + ", this is a reminder from SG and SR Jewellery. Please note that you have a balance amount of " + balance + " pending. Kindly clear the payment as soon as possible.Your due date is "+ duedate + "Thank you!");
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void sendNotification(String phoneNumber, String message) {
//        try {
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
//            Toast.makeText(UploadData.this, "SMS notification sent", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            Toast.makeText(UploadData.this, "Failed to send SMS notification", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == PERMISSION_REQUEST_SMS) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, you can now send SMS
//            } else {
//                // Permission denied, handle accordingly
//                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }


    public void showDatePicker() {
        // Create a dialog for selecting the date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Handle the selected date here
                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                binding.Date.setText(selectedDate);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        // Show the date picker dialog for selecting the date
        datePickerDialog.show();
    }

    public void showDueDatePicker() {
        // Create a dialog for selecting the due date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Handle the selected due date here
                String dueDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                binding.duedate.setText(dueDate);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        // Show the date picker dialog for selecting the due date
        datePickerDialog.show();
    }


}
//    public void showDatePicker(){
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                // Handle the selected date here
//                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//             //   String dueDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                binding.Date.setText(selectedDate);
////                binding.duedate.setText(dueDate);
//            }
//        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
//
//        // Show the date picker dialog
//        datePickerDialog.show();
//    }








//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityUploaddataBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        binding.save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                billno = binding.billno.getText().toString();
//                name = binding.name.getText().toString();
//                place = binding.place.getText().toString();
//                amount = binding.amount.getText().toString();
//                balance = binding.balance.getText().toString();
//
//                if (!billno.isEmpty() && !name.isEmpty() &&!place.isEmpty()&&  !amount.isEmpty() && !balance.isEmpty()){
//
//                    UserHelperJava users = new UserHelperJava(billno,name,place,amount,balance);
//                    db = FirebaseDatabase.getInstance();
//                    reference = db.getReference("Sales");
//                    reference.child(billno).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//
//                            binding.billno.setText("");
//                            binding.name.setText("");
//                            binding.place.setText("");
//                            binding.amount.setText("");
//                            binding.balance.setText("");
//                            Toast.makeText(uploaddata.this,"Successfully Uploaded",Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
//
//                }
//
//            }
//        });
//    }
//}
//
//

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


//package com.example.BillingDemo;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.concurrent.TimeUnit;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.telephony.SmsManager;
//import android.app.DatePickerDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.DatePicker;
//import android.widget.Toast;
//
//import com.example.BillingDemo.databinding.ActivityUploaddataBinding;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class UploadData extends AppCompatActivity {
//
//    ActivityUploaddataBinding binding;
//    String billno, name, place, amount, balance, selectedDate, duedate, fatherName, aadharNum, phoneNum;
//
//    FirebaseDatabase db;
//    DatabaseReference reference;
//
//    private static final long CHECK_INTERVAL = TimeUnit.DAYS.toMillis(1); // Interval to check daysPending (1 day)
//    private static final int PERMISSION_REQUEST_SMS = 123;
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(UploadData.this, SalesFirstPage.class));
//        finish();
//    }
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityUploaddataBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//
////        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
////            // Request the permission
////            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SMS);
////        } else {
////            // Permission already granted, you can now send SMS
////        }
//
//
//
//        binding.Date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDatePicker();
//            }
//
//        });
//        binding.duedate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDueDatePicker();
//            }
//
//        });
//
//
//        binding.save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                billno = binding.billno.getText().toString();
//                name = binding.name.getText().toString();
//                fatherName = binding.fatherName.getText().toString();
//                aadharNum = binding.aadharNumber.getText().toString();
//                phoneNum = binding.phoneNumber.getText().toString();
//                place = binding.place.getText().toString();
//                amount = binding.amount.getText().toString();
//                balance = binding.balance.getText().toString();
//                selectedDate = binding.Date.getText().toString();
//                duedate = binding.duedate.getText().toString();
//
//
//                if (!billno.isEmpty() && !name.isEmpty() && !place.isEmpty() && !amount.isEmpty() && !balance.isEmpty()) {
//                    UserHelperJava users = new UserHelperJava(selectedDate, billno, name,fatherName,aadharNum,phoneNum ,place, amount, balance, duedate);
//                    db = FirebaseDatabase.getInstance();
//
//                    reference = db.getReference("Sales");
//
//
//                    reference.child(billno).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            binding.billno.setText("");
//                            binding.name.setText("");
//                            binding.fatherName.setText("");
//                            binding.place.setText("");
//                            binding.aadharNumber.setText("");
//                            binding.phoneNumber.setText("");
//                            binding.amount.setText("");
//                            binding.balance.setText("");
//                            binding.Date.setText("");
//                            binding.duedate.setText("");
//
//
//                            Toast.makeText(UploadData.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
//                           // scheduleNotification();
//                             }
//
//                    });
//                }
//            }
//        });
//
//    }
//
//    public void showDatePicker() {
//        // Create a dialog for selecting the date
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                // Handle the selected date here
//                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                binding.Date.setText(selectedDate);
//            }
//        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
//
//        // Show the date picker dialog for selecting the date
//        datePickerDialog.show();
//    }
//
//    public void showDueDatePicker() {
//        // Create a dialog for selecting the due date
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                // Handle the selected due date here
//                String dueDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                binding.duedate.setText(dueDate);
//            }
//        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
//
//        // Show the date picker dialog for selecting the due date
//        datePickerDialog.show();
//    }
//
//
//}


//    private void sendSmsNotification() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
//            // Permission granted, send the SMS
//
//            String message = "Dear " + name + ", this is a reminder from SG and SR Jewellery. Please note that you have a balance amount of " + balance + " pending. Kindly clear the payment as soon as possible. Your due date is " + duedate + ". Thank you!";
//            try {
//                SmsManager smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage(phoneNum, null, message, null, null);
//                Toast.makeText(UploadData.this, "SMS notification sent", Toast.LENGTH_SHORT).show();
//            } catch (Exception e) {
//                Toast.makeText(UploadData.this, "Failed to send SMS notification", Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
//        } else {
//            // Permission not granted, request the SMS permission
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SMS);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == PERMISSION_REQUEST_SMS) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, send the SMS
//                sendSmsNotification();
//            } else {
//                // Permission denied, handle accordingly (e.g., show an error message)
//                Toast.makeText(UploadData.this, "SMS permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Call super method
//    }

//    private void requestSmsPermission () {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//            // Request the permission
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SMS);
//        } else {
//            // Permission already granted, you can now send SMS
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions,
//                                            @NonNull int[] grantResults)
//    {
//        if (requestCode == PERMISSION_REQUEST_SMS) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, you can now send SMS
//            } else {
//                // Permission denied, handle accordingly (e.g., show an error message)
//            }
//        }
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//    }
//
//    private void scheduleNotification() {
//        // Parse the selected due date
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        try {
//            Date dueDate = dateFormat.parse(duedate);
//            Date currentDate = new Date();
//
//            // Calculate the difference between current date and due date in days
//            long differenceInDays = TimeUnit.DAYS.convert(dueDate.getTime() - currentDate.getTime(), TimeUnit.MILLISECONDS);
//
//            // Check if the difference is equal to 1 day
//            if (differenceInDays == 0) {
//                // Send notification with the balance amount to the customer's phone number
//                sendNotification(phoneNum, "Dear " + name + ", this is a reminder from SG and SR Jewellery. Please note that you have a balance amount of " + balance + " pending. Kindly clear the payment as soon as possible.Your due date is "+ duedate + "Thank you!");
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void sendNotification(String phoneNumber, String message) {
//        try {
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
//            Toast.makeText(UploadData.this, "SMS notification sent", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            Toast.makeText(UploadData.this, "Failed to send SMS notification", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//    }










//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityUploaddataBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        binding.save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                billno = binding.billno.getText().toString();
//                name = binding.name.getText().toString();
//                place = binding.place.getText().toString();
//                amount = binding.amount.getText().toString();
//                balance = binding.balance.getText().toString();
//
//                if (!billno.isEmpty() && !name.isEmpty() &&!place.isEmpty()&&  !amount.isEmpty() && !balance.isEmpty()){
//
//                    UserHelperJava users = new UserHelperJava(billno,name,place,amount,balance);
//                    db = FirebaseDatabase.getInstance();
//                    reference = db.getReference("Sales");
//                    reference.child(billno).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//
//                            binding.billno.setText("");
//                            binding.name.setText("");
//                            binding.place.setText("");
//                            binding.amount.setText("");
//                            binding.balance.setText("");
//                            Toast.makeText(uploaddata.this,"Successfully Uploaded",Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
//
//                }
//
//            }
//        });
//    }
//}
//
//

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