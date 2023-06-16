package com.example.BillingDemo;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Report extends AppCompatActivity {


    private TextView tvSalesInfo;
    private TextView MiniHeading;
    private Button btnFilter1, btnFilter2, btnFilter3, btnFilter4, btnFilter5, btnFilter6;
    private Calendar startDate, endDate;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        tvSalesInfo = findViewById(R.id.tvSalesInfo);
        MiniHeading = findViewById(R.id.tvDateRange);


        // Find views
        btnFilter1 = findViewById(R.id.btnFilter1);
        btnFilter2 = findViewById(R.id.btnFilter2);
        btnFilter3 = findViewById(R.id.btnFilter3);
        btnFilter4 = findViewById(R.id.btnFilter4);
        btnFilter5 = findViewById(R.id.btnFilter5);
        btnFilter6 = findViewById(R.id.btnFilter6);
        recyclerView = findViewById(R.id.recyclerView);
// Add this code inside your onCreate() or any appropriate initialization method


        // Set click listeners
        btnFilter1.setOnClickListener(onClickListener);
        btnFilter2.setOnClickListener(onClickListener);
        btnFilter3.setOnClickListener(onClickListener);
        btnFilter4.setOnClickListener(onClickListener);
        btnFilter5.setOnClickListener(onClickListener);
        btnFilter6.setOnClickListener(onClickListener);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Set up Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Sales");

        // Filter by today's due date initially
//            filterByToday();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            resetButtons();
            resetButtons();
            // Set the clicked button to selected state
            v.setBackgroundColor(getResources().getColor(R.color.filter_button_selected));

            // Display a toast message
            String filterName = ((Button) v).getText().toString();
            Toast.makeText(Report.this, "Selected filter: " + filterName, Toast.LENGTH_SHORT).show();


            switch (v.getId()) {
                case R.id.btnFilter1:
                    showSpecificDatePickerDialog2();
                    // Handle specific date filter
                    break;
                case R.id.btnFilter2:
                    showDatePickerDialog();
                    // Handle date range filter
                    break;
                case R.id.btnFilter3:
                    filterByToday();
                    break;
                case R.id.btnFilter4:
                    filterByThisWeek();
                    // Handle this week filter
                    break;
                case R.id.btnFilter5:
                    // Handle this month filter
                    filterByThisMonth();
                    break;
                case R.id.btnFilter6:
                    // Handle custom filter
                    filterAll();
                    break;
            }
        }

    };

    private void resetButtons() {
        btnFilter1.setBackgroundColor(getResources().getColor(R.color.filter_button_unselected));
        btnFilter2.setBackgroundColor(getResources().getColor(R.color.filter_button_unselected));
        btnFilter3.setBackgroundColor(getResources().getColor(R.color.filter_button_unselected));
        btnFilter4.setBackgroundColor(getResources().getColor(R.color.filter_button_unselected));
        btnFilter5.setBackgroundColor(getResources().getColor(R.color.filter_button_unselected));
        btnFilter6.setBackgroundColor(getResources().getColor(R.color.filter_button_unselected));
    }

    private void filterByToday() {
        // Get today's date
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String todayDate = dateFormat.format(calendar.getTime());

        // Query the database for users with today's due date
        Query query = databaseReference.orderByChild("duedate").equalTo(todayDate);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<UserHelperJava> userList = new ArrayList<>();
                double totalAmount = 0.0;
                double totalBalance = 0.0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserHelperJava user = dataSnapshot.getValue(UserHelperJava.class);
                    userList.add(user);

                    // Calculate the total amount and balance
                    double amount = Double.parseDouble(user.getAmount());
                    double balance = Double.parseDouble(user.getBalance());
                    totalAmount += amount;
                    totalBalance += balance;
                }
                adapter.setList(userList);

                int salesCount = userList.size();
                String salesInfo = getString(R.string.sales_info, salesCount, totalAmount, totalBalance);
                String dateRangeText = getString(R.string.today);
                MiniHeading.setText(dateRangeText);
                tvSalesInfo.setText(salesInfo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Report.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterByThisWeek() {
        // Get the start and end dates of the current week
        Calendar calendar = Calendar.getInstance();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DAY_OF_WEEK, -(currentDayOfWeek - Calendar.SUNDAY));
        Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        Date endDate = calendar.getTime();
        long startMillis = startDate.getTime();
        long endMillis = endDate.getTime();

        // Format the start and end dates to match the date format in the database
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String startDateStr = dateFormat.format(startDate);
        String endDateStr = dateFormat.format(endDate);

        // Query the database for users within the current week's due dates
        Query query = databaseReference.orderByChild("duedateTimestamp").startAt(startMillis).endAt(endMillis);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<UserHelperJava> userList = new ArrayList<>();
                double totalAmount = 0.0;
                double totalBalance = 0.0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserHelperJava user = dataSnapshot.getValue(UserHelperJava.class);
                    userList.add(user);

                    // Calculate the total amount and balance
                    double amount = Double.parseDouble(user.getAmount());
                    double balance = Double.parseDouble(user.getBalance());
                    totalAmount += amount;
                    totalBalance += balance;
                }
                adapter.setList(userList);

                // Update the TextView with sales information
                int salesCount = userList.size();
                String salesInfo = getString(R.string.sales_info, salesCount, totalAmount, totalBalance);
                String dateRangeText = getString(R.string.current_week);
                MiniHeading.setText(dateRangeText);
                tvSalesInfo.setText(salesInfo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Report.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterByThisMonth() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = calendar.getTime();
        long startMillis = startDate.getTime();
        long endMillis = endDate.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String startDateString = dateFormat.format(startDate);
        String endDateString = dateFormat.format(endDate);


        // Query the database for users within the current month's due dates
        Query query = databaseReference.orderByChild("duedateTimestamp").startAt(startMillis).endAt(endMillis);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<UserHelperJava> userList = new ArrayList<>();
                double totalAmount = 0.0;
                double totalBalance = 0.0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserHelperJava user = dataSnapshot.getValue(UserHelperJava.class);
                    userList.add(user);

                    // Calculate the total amount and balance
                    double amount = Double.parseDouble(user.getAmount());
                    double balance = Double.parseDouble(user.getBalance());
                    totalAmount += amount;
                    totalBalance += balance;
                }
                adapter.setList(userList);

                // Update the TextView with sales information
                int salesCount = userList.size();
                String salesInfo = getString(R.string.sales_info, salesCount, totalAmount, totalBalance);
                String dateRangeText = getString(R.string.current_month);
                MiniHeading.setText(dateRangeText);
                tvSalesInfo.setText(salesInfo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Report.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener startDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Save the selected start date
                Calendar startCalendar = Calendar.getInstance();
                startCalendar.set(year, month, dayOfMonth);
                Date startDate = startCalendar.getTime();

                // Show the end date picker dialog
                showEndDatePickerDialog(startDate);
            }
        };

        // Show the start date picker dialog
        Calendar calendar = Calendar.getInstance();
        int startYear = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH);
        int startDay = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(Report.this, startDateSetListener, startYear, startMonth, startDay);
        datePickerDialog.setTitle("Select Start Date");
        datePickerDialog.show();
    }

    private void showEndDatePickerDialog(final Date startDate) {
        DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Save the selected end date
                Calendar endCalendar = Calendar.getInstance();
                endCalendar.set(year, month, dayOfMonth);
                Date endDate = endCalendar.getTime();

                // Filter the user list based on the selected date range
                filterByDateRange(startDate, endDate);
            }
        };

        // Show the end date picker dialog
        Calendar calendar = Calendar.getInstance();
        int endYear = calendar.get(Calendar.YEAR);
        int endMonth = calendar.get(Calendar.MONTH);
        int endDay = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(Report.this, endDateSetListener, endYear, endMonth, endDay);
        datePickerDialog.setTitle("Select End Date");

        datePickerDialog.show();
    }

    private void filterByDateRange(Date startDate, Date endDate) {
        // Convert the start and end dates to milliseconds
        long startMillis = startDate.getTime();
        long endMillis = endDate.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String startDateString = dateFormat.format(startDate);
        String endDateString = dateFormat.format(endDate);
        Query query = databaseReference.orderByChild("duedateTimestamp").startAt(startMillis).endAt(endMillis);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<UserHelperJava> userList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserHelperJava user = dataSnapshot.getValue(UserHelperJava.class);
                    userList.add(user);
                }
                adapter.setList(userList);

                // Calculate the number of sales, total amount, and total balance
                int numberOfSales = userList.size();
                double totalAmount = 0;
                double totalBalance = 0;
                for (UserHelperJava user : userList) {
                    double amount = Double.parseDouble(user.getAmount());
                    double balance = Double.parseDouble(user.getBalance());
                    totalAmount += amount;
                    totalBalance += balance;
                }

                // Update the TextView with the information
                String info = getString(R.string.sales_info, numberOfSales, totalAmount, totalBalance);

                String dateRangeText = getString(R.string.dat_range) +" "+ startDateString + " and " + endDateString;
                MiniHeading.setText(dateRangeText);
                tvSalesInfo.setText(info);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Report.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showSpecificDatePickerDialog2() {
        DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Save the selected end date
                Calendar calender = Calendar.getInstance();
                calender.set(year, month, dayOfMonth);
                Date selectedDate = calender.getTime();

                // Filter the user list based on the selected date range
                filterBySpecificDate(selectedDate);
            }
        };

        // Show the end date picker dialog
        Calendar calendar = Calendar.getInstance();
        int endYear = calendar.get(Calendar.YEAR);
        int endMonth = calendar.get(Calendar.MONTH);
        int endDay = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(Report.this, endDateSetListener, endYear, endMonth, endDay);
        datePickerDialog.setTitle("Select Date");

        datePickerDialog.show();
    }
    private void filterBySpecificDate(Date selectedDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String selectedDateString = dateFormat.format(selectedDate);
        long startMillis = selectedDate.getTime();
        Query query = databaseReference.orderByChild("duedateTimestamp").endAt(startMillis);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<UserHelperJava> userList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserHelperJava user = dataSnapshot.getValue(UserHelperJava.class);
                    userList.add(user);
                }
                adapter.setList(userList);

                // Calculate the number of sales, total amount, and total balance
                int numberOfSales = userList.size();
                double totalAmount = 0;
                double totalBalance = 0;
                for (UserHelperJava user : userList) {
                    double amount = Double.parseDouble(user.getAmount());
                    double balance = Double.parseDouble(user.getBalance());
                    totalAmount += amount;
                    totalBalance += balance;
                }

                // Update the TextView with the information
                String salesInfo = getString(R.string.sales_info, numberOfSales, totalAmount, totalBalance);

                String dateRangeText = getString(R.string.sp_date) + selectedDateString ;
                MiniHeading.setText(dateRangeText);
                tvSalesInfo.setText(salesInfo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Report.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void filterAll() {
        // Retrieve all users from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<UserHelperJava> userList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserHelperJava user = dataSnapshot.getValue(UserHelperJava.class);
                    userList.add(user);
                }
                adapter.setList(userList);

                // Calculate the number of sales, total amount, and total balance
                int numberOfSales = userList.size();
                double totalAmount = 0;
                double totalBalance = 0;
                for (UserHelperJava user : userList) {
                    double amount = Double.parseDouble(user.getAmount());
                    double balance = Double.parseDouble(user.getBalance());
                    totalAmount += amount;
                    totalBalance += balance;
                }

                // Update the TextView with the information
                String salesInfo = getString(R.string.sales_info, numberOfSales, totalAmount, totalBalance);
                String dateRangeText = getString(R.string.filter_all);
                MiniHeading.setText(dateRangeText);
                tvSalesInfo.setText(salesInfo);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Report.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}

//    private void filterByDateRange(Date startDate, Date endDate) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        String startDateString = dateFormat.format(startDate);
//        String endDateString = dateFormat.format(endDate);
//
//        Query query = databaseReference.orderByChild("duedate").startAt(startDateString).endAt(endDateString);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ArrayList<UserHelperJava> userList = new ArrayList<>();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    UserHelperJava user = dataSnapshot.getValue(UserHelperJava.class);
//                    userList.add(user);
//                }
//                adapter.setList(userList);
//
//                // Calculate the number of sales, total amount, and total balance
//                int numberOfSales = userList.size();
//                double totalAmount = 0;
//                double totalBalance = 0;
//                for (UserHelperJava user : userList) {
//                    double amount = Double.parseDouble(user.getAmount());
//                    double balance = Double.parseDouble(user.getBalance());
//                    totalAmount += amount;
//                    totalBalance += balance;
//                }
//
//                String info = getString(R.string.sales_info, numberOfSales, totalAmount, totalBalance);
//                String dateRangeText = getString(R.string.dat_range) + startDateString+ " and " + endDateString;
//                tvDateRange.setText(dateRangeText);
//                tvSalesInfo.setText(info);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(Report.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }






//    private Button btnFilter1, btnFilter2, btnFilter3, btnFilter4, btnFilter5, btnFilter6;
//    private Calendar startDate, endDate;
//    private TextView tvReport;
//    private DatabaseReference databaseReference;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_report);
//
//        // Find views
//        btnFilter1 = findViewById(R.id.btnSpecificDate);
//        btnFilter2 = findViewById(R.id.btnDateRange);
//        btnFilter3 = findViewById(R.id.btnToday);
//        btnFilter4 = findViewById(R.id.btnThisWeek);
//        btnFilter5 = findViewById(R.id.btnThisMonth);
//        btnFilter6 = findViewById(R.id.btnCustomFilter);
//
//        // Set click listeners
//        databaseReference = FirebaseDatabase.getInstance().getReference("Sales");
//        tvReport = findViewById(R.id.tvReport);
//
//        btnFilter1.setOnClickListener(onClickListener);
//        btnFilter2.setOnClickListener(onClickListener);
//        btnFilter3.setOnClickListener(onClickListener);
////        btnFilter3.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                retrieveTodayData();
////            }
////        });
//
//        btnFilter4.setOnClickListener(onClickListener);
//        btnFilter5.setOnClickListener(onClickListener);
//        btnFilter6.setOnClickListener(onClickListener);
//    }
//
//    private View.OnClickListener onClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            // Reset all buttons to unselected state
//            resetButtons();
//
//            // Set the clicked button to selected state
//            view.setBackgroundColor(getResources().getColor(R.color.filter_button_selected));
//
//            // Display a toast message
//            String filterName = ((Button) view).getText().toString();
//            Toast.makeText(Report.this, "Selected filter: " + filterName, Toast.LENGTH_SHORT).show();
//
//            // Handle date range filter
//            if (view == btnFilter2) {
//                openDatePickerDialog();
//            }
//            if (view == btnFilter3) {
//                retrieveTodayData();
//            }
//        }
//    };
//    private void retrieveTodayData() {
//        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//        Query query = databaseReference.orderByChild("dueDate").equalTo(todayDate);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                StringBuilder report = new StringBuilder();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    UserHelperJava user = snapshot.getValue(UserHelperJava.class);
//                    if (user != null) {
//                        report.append("Name: ").append(user.getName()).append("\n");
//                        report.append("BillNo: ").append(user.getBillno()).append("\n\n");
//                        report.append("Due Date: ").append(user.getDuedate()).append("\n");
//
//                    }
//                }
//                tvReport.setText(report.toString());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(Report.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//
//    private void openDatePickerDialog() {
//        final Calendar calendar = Calendar.getInstance();
//        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
//
//        DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                startDate = Calendar.getInstance();
//                startDate.set(Calendar.YEAR, year);
//                startDate.set(Calendar.MONTH, month);
//                startDate.set(Calendar.DAY_OF_MONTH, day);
//
//                DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                        endDate = Calendar.getInstance();
//                        endDate.set(Calendar.YEAR, year);
//                        endDate.set(Calendar.MONTH, month);
//                        endDate.set(Calendar.DAY_OF_MONTH, day);
//
//                        // Perform actions with the selected date range
//                        displayReportForDateRange(startDate.getTime(), endDate.getTime());
//                    }
//                };
//
//                DatePickerDialog endDatePickerDialog = new DatePickerDialog(Report.this, endDateListener,
//                        calendar.get(Calendar.YEAR),
//                        calendar.get(Calendar.MONTH),
//                        calendar.get(Calendar.DAY_OF_MONTH));
//                endDatePickerDialog.getDatePicker().setMinDate(startDate.getTimeInMillis());
//                endDatePickerDialog.show();
//            }
//        };
//
//        DatePickerDialog startDatePickerDialog = new DatePickerDialog(Report.this, startDateListener,
//                calendar.get(Calendar.YEAR),
//                calendar.get(Calendar.MONTH),
//                calendar.get(Calendar.DAY_OF_MONTH));
//        startDatePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
//        startDatePickerDialog.show();
//    }
//
//    private void displayReportForDateRange(Date startDate, Date endDate) {
//        DatabaseReference salesRef = FirebaseDatabase.getInstance().getReference().child("sales");
//        Query query = salesRef.orderByChild("date").startAt(formatDate(startDate)).endAt(formatDate(endDate));
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    int numberOfSales = 0;
//                    double amountReceived = 0;
//                    double balanceAmount = 0;
//
//                    for (DataSnapshot saleSnapshot : dataSnapshot.getChildren()) {
//                        Sale sale = saleSnapshot.getValue(Sale.class);
//                        if (sale != null) {
//                            double amount = sale.getAmount();
//                            double balance = sale.getBalance();
//
//                            numberOfSales++;
//                            amountReceived += amount;
//                            balanceAmount += balance;
//                        }
//                    }
//
//                    // Display the report
//                    String report = "Report for " + formatDate(startDate) + " - " + formatDate(endDate) + ":\n"
//                            + "Number of Sales: " + numberOfSales + "\n"
//                            + "Amount Received: $" + amountReceived + "\n"
//                            + "Balance Amount: $" + balanceAmount;
//
//                    Toast.makeText(Report.this, report, Toast.LENGTH_LONG).show();
//                } else {
//                    // No data available for the selected date range
//                    Toast.makeText(Report.this, "No data available for the selected date range", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle any errors that occur during data retrieval
//                Toast.makeText(Report.this, "Failed to retrieve data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    public class Sale {
//        private double amount;
//        private double balance;
//        // Add any other necessary fields
//
//        public Sale() {
//            // Default constructor required for Firebase
//        }
//
//        public Sale(double amount, double balance) {
//            this.amount = amount;
//            this.balance = balance;
//        }
//
//        public double getAmount() {
//            return amount;
//        }
//
//        public double getBalance() {
//            return balance;
//        }
//        // Add setters and any other necessary methods
//    }
//    private void resetButtons() {
//        btnFilter1.setBackgroundColor(getResources().getColor(R.color.filter_button_unselected));
//        btnFilter2.setBackgroundColor(getResources().getColor(R.color.filter_button_unselected));
//        btnFilter3.setBackgroundColor(getResources().getColor(R.color.filter_button_unselected));
//        btnFilter4.setBackgroundColor(getResources().getColor(R.color.filter_button_unselected));
//        btnFilter5.setBackgroundColor(getResources().getColor(R.color.filter_button_unselected));
//        btnFilter6.setBackgroundColor(getResources().getColor(R.color.filter_button_unselected));
//    }
//
//    private String formatDate(Date date) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
//        return dateFormat.format(date);
//    }
