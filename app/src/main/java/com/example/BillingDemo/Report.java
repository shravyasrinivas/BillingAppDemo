package com.example.BillingDemo;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Report extends AppCompatActivity {

    private Button btnFilter1, btnFilter2, btnFilter3, btnFilter4, btnFilter5, btnFilter6;
    private Calendar startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Find views
        btnFilter1 = findViewById(R.id.btnSpecificDate);
        btnFilter2 = findViewById(R.id.btnDateRange);
        btnFilter3 = findViewById(R.id.btnToday);
        btnFilter4 = findViewById(R.id.btnThisWeek);
        btnFilter5 = findViewById(R.id.btnThisMonth);
        btnFilter6 = findViewById(R.id.btnCustomFilter);

        // Set click listeners
        btnFilter1.setOnClickListener(onClickListener);
        btnFilter2.setOnClickListener(onClickListener);
        btnFilter3.setOnClickListener(onClickListener);
        btnFilter4.setOnClickListener(onClickListener);
        btnFilter5.setOnClickListener(onClickListener);
        btnFilter6.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Reset all buttons to unselected state
            resetButtons();

            // Set the clicked button to selected state
            view.setBackgroundColor(getResources().getColor(R.color.filter_button_selected));

            // Display a toast message
            String filterName = ((Button) view).getText().toString();
            Toast.makeText(Report.this, "Selected filter: " + filterName, Toast.LENGTH_SHORT).show();

            // Handle date range filter
            if (view == btnFilter2) {
                openDatePickerDialog();
            }
        }
    };

    private void openDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                startDate = Calendar.getInstance();
                startDate.set(Calendar.YEAR, year);
                startDate.set(Calendar.MONTH, month);
                startDate.set(Calendar.DAY_OF_MONTH, day);

                DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        endDate = Calendar.getInstance();
                        endDate.set(Calendar.YEAR, year);
                        endDate.set(Calendar.MONTH, month);
                        endDate.set(Calendar.DAY_OF_MONTH, day);

                        // Perform actions with the selected date range
                        displayReportForDateRange(startDate.getTime(), endDate.getTime());
                    }
                };

                DatePickerDialog endDatePickerDialog = new DatePickerDialog(Report.this, endDateListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                endDatePickerDialog.getDatePicker().setMinDate(startDate.getTimeInMillis());
                endDatePickerDialog.show();
            }
        };

        DatePickerDialog startDatePickerDialog = new DatePickerDialog(Report.this, startDateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        startDatePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        startDatePickerDialog.show();
    }

    private void displayReportForDateRange(Date startDate, Date endDate) {
        DatabaseReference salesRef = FirebaseDatabase.getInstance().getReference().child("sales");
        Query query = salesRef.orderByChild("date").startAt(formatDate(startDate)).endAt(formatDate(endDate));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int numberOfSales = 0;
                    double amountReceived = 0;
                    double balanceAmount = 0;

                    for (DataSnapshot saleSnapshot : dataSnapshot.getChildren()) {
                        Sale sale = saleSnapshot.getValue(Sale.class);
                        if (sale != null) {
                            double amount = sale.getAmount();
                            double balance = sale.getBalance();

                            numberOfSales++;
                            amountReceived += amount;
                            balanceAmount += balance;
                        }
                    }

                    // Display the report
                    String report = "Report for " + formatDate(startDate) + " - " + formatDate(endDate) + ":\n"
                            + "Number of Sales: " + numberOfSales + "\n"
                            + "Amount Received: $" + amountReceived + "\n"
                            + "Balance Amount: $" + balanceAmount;

                    Toast.makeText(Report.this, report, Toast.LENGTH_LONG).show();
                } else {
                    // No data available for the selected date range
                    Toast.makeText(Report.this, "No data available for the selected date range", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during data retrieval
                Toast.makeText(Report.this, "Failed to retrieve data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class Sale {
        private double amount;
        private double balance;
        // Add any other necessary fields

        public Sale() {
            // Default constructor required for Firebase
        }

        public Sale(double amount, double balance) {
            this.amount = amount;
            this.balance = balance;
        }

        public double getAmount() {
            return amount;
        }

        public double getBalance() {
            return balance;
        }
        // Add setters and any other necessary methods
    }
    private void resetButtons() {
        btnFilter1.setBackgroundColor(getResources().getColor(R.color.filter_button_unselected));
        btnFilter2.setBackgroundColor(getResources().getColor(R.color.filter_button_unselected));
        btnFilter3.setBackgroundColor(getResources().getColor(R.color.filter_button_unselected));
        btnFilter4.setBackgroundColor(getResources().getColor(R.color.filter_button_unselected));
        btnFilter5.setBackgroundColor(getResources().getColor(R.color.filter_button_unselected));
        btnFilter6.setBackgroundColor(getResources().getColor(R.color.filter_button_unselected));
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return dateFormat.format(date);
    }
}
