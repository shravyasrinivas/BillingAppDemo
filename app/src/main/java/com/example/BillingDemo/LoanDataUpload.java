package com.example.BillingDemo;

import java.util.Calendar;
import java.util.Date;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;
import com.example.BillingDemo.databinding.ActivityUploaddataBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoanDataUpload extends AppCompatActivity {

    ActivityUploaddataBinding binding;
    String billno, name, place, amount, balance, selectedDate, duedate;

    FirebaseDatabase db;
    DatabaseReference reference;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoanDataUpload.this, SalesFirstPage.class));
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploaddataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
                name = binding.name.getText().toString();
                place = binding.place.getText().toString();
                amount = binding.amount.getText().toString();
                balance = binding.balance.getText().toString();
                selectedDate = binding.Date.getText().toString();
                duedate = binding.duedate.getText().toString();


                if (!billno.isEmpty() && !name.isEmpty() && !place.isEmpty() && !amount.isEmpty() && !balance.isEmpty()) {
                    UserHelperJava users = new UserHelperJava(selectedDate, billno, name, place, amount, balance, duedate);
                    db = FirebaseDatabase.getInstance();

                    reference = db.getReference("Loans");


                    reference.child(billno).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            binding.billno.setText("");
                            binding.name.setText("");
                            binding.place.setText("");
                            binding.amount.setText("");
                            binding.balance.setText("");
                            binding.Date.setText("");
                            binding.duedate.setText("");
                            Toast.makeText(LoanDataUpload.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                        }

                    });
                }
            }
        });

    }
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