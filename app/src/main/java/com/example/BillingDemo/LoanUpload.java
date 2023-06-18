package com.example.BillingDemo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.BillingDemo.databinding.ActivityLoanUploadBinding;
import com.example.BillingDemo.databinding.ActivityUploaddataBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoanUpload extends AppCompatActivity {

    ActivityLoanUploadBinding binding;
    String billno, name, place, amount, balance, selectedDate, duedate,loanType,metalType,jewelType;

    FirebaseDatabase db;
    DatabaseReference reference;

    private ArrayList<String> spinnerItems, spinnerItems1;
    private ArrayList<String> goldJewelItems, silverJewelItems;
    private ArrayAdapter<String> spinnerAdapter, spinnerAdapter1, jewelAdapter;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoanUpload.this, LoanFirstPage.class));
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoanUploadBinding.inflate(getLayoutInflater());
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

        spinnerItems = new ArrayList<>();
        spinnerItems.add("SR");
        spinnerItems.add("SG");

        spinnerItems1 = new ArrayList<>();
        spinnerItems1.add("Gold");
        spinnerItems1.add("Silver");

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        binding.loanTypeSpinner.setAdapter(spinnerAdapter);

        spinnerAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerItems1);
        binding.metalSpinner.setAdapter(spinnerAdapter1);

        goldJewelItems = new ArrayList<>();
        goldJewelItems.add("Jewel 1");
        goldJewelItems.add("Jewel 2");
        goldJewelItems.add("Jewel 3");
        goldJewelItems.add("Jewel 4");
        goldJewelItems.add("Jewel 5");

        silverJewelItems = new ArrayList<>();
        silverJewelItems.add("Jewel A");
        silverJewelItems.add("Jewel B");
        silverJewelItems.add("Jewel C");
        silverJewelItems.add("Jewel D");
        silverJewelItems.add("Jewel E");

        jewelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        binding.jewelSpinner.setAdapter(jewelAdapter);

        binding.metalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMetalType = parent.getItemAtPosition(position).toString();

                if (selectedMetalType.equals("Gold")) {
                    jewelAdapter.clear();
                    jewelAdapter.addAll(goldJewelItems);
                } else if (selectedMetalType.equals("Silver")) {
                    jewelAdapter.clear();
                    jewelAdapter.addAll(silverJewelItems);
                }

                jewelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle when nothing is selected
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billno = binding.billno1.getText().toString();
                name = binding.name.getText().toString();
                place = binding.place.getText().toString();
                amount = binding.amount.getText().toString();
                balance = binding.balance.getText().toString();
                selectedDate = binding.Date.getText().toString();
                duedate = binding.duedate.getText().toString();
                loanType = binding.loanTypeSpinner.getSelectedItem().toString();
                metalType = binding.metalSpinner.getSelectedItem().toString();
                jewelType = binding.jewelSpinner.getSelectedItem().toString();


                if (!billno.isEmpty() && !name.isEmpty() && !place.isEmpty() && !amount.isEmpty() && !balance.isEmpty()) {
                    UserHelperJava2 users = new UserHelperJava2(selectedDate, billno, name, place, amount, balance, duedate,loanType,metalType,jewelType);
                    db = FirebaseDatabase.getInstance();

                    reference = db.getReference("Loans");


                    reference.child(billno).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            binding.billno1.setText("");
                            binding.name.setText("");
                            binding.place.setText("");
                            binding.amount.setText("");
                            binding.balance.setText("");
                            binding.Date.setText("");
                            binding.duedate.setText("");
                            binding.loanTypeSpinner.setSelection(0);
                            binding.metalSpinner.setSelection(0);
                            binding.jewelSpinner.setSelection(0);

                            Toast.makeText(LoanUpload.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
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