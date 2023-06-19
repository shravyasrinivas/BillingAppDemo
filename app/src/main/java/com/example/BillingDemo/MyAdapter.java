package com.example.BillingDemo;



import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements Filterable {
    Context context;
    ArrayList<UserHelperJava> list;
    ArrayList<UserHelperJava> listfull;
    DatabaseReference databaseReference;
    //private int[] tagIcons = {R.drawable.ic_baseline_done_24, R.drawable.ic_baseline_close_24};

    private static final long CHECK_INTERVAL = TimeUnit.DAYS.toMillis(1); // Interval to check daysPending (1 day)
    private static final int PERMISSION_REQUEST_SMS = 123;


    public MyAdapter(Context context, ArrayList<UserHelperJava> list) {
        this.context = context;
        this.listfull = list;
        this.list=new ArrayList<>(listfull);

    }
    public void setList(ArrayList<UserHelperJava> filteredList) {
        list.clear();
        list.addAll(filteredList);
        notifyDataSetChanged();
    }


        @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.userentry, parent,false);
        return new MyViewHolder(v);
    }

//    private void requestSmsPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SMS);
//        } else {
//            // Permission already granted, you can now send SMS
//            // Call the method to send SMS from your adapter here
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == PERMISSION_REQUEST_SMS) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, you can now send SMS
//            } else {
//                // Permission denied, handle accordingly
//            }
//        }
//    }


    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        UserHelperJava user = list.get(holder.getAdapterPosition());
        holder.name.setText(user.getName());
        holder.billno.setText(user.getBillno());

        holder.invoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserHelperJava user = list.get(holder.getAdapterPosition());
                String htmlTemplate = "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "    <title>Invoice</title>\n" +
                        "    <style>\n" +
                        "    body {\n" +
                        "      font-family: Arial, sans-serif;\n" +
                        "      margin: 0;\n" +
                        "      padding: 20px;\n" +
                        "    }\n" +
                        "    h1 {\n" +
                        "      text-align: center;\n" +
                        "    }\n" +
                        "    table {\n" +
                        "      width: 100%;\n" +
                        "      border-collapse: collapse;\n" +
                        "      margin-top: 20px;\n" +
                        "    }\n" +
                        "    th, td {\n" +
                        "      padding: 8px;\n" +
                        "      text-align: left;\n" +
                        "      border-bottom: 1px solid #ddd;\n" +
                        "    }\n" +
                        "    th {\n" +
                        "      background-color: #f2f2f2;\n" +
                        "    }\n" +
                        "  </style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<h1>SG AND SR JEWELLERY</h1>\n" +
                        "<table>\n" +
                        "<tr>\n" +
                        "        <th>Date</th>\n" +
                        "        <td>" + user.getDate() + "</td>\n" +
                        "    </tr>\n" +

                        "    <tr>\n" +
                        "        <th>Bill No</th>\n" +
                        "        <td>" + user.getBillno() + "</td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "        <th>Customer Name</th>\n" +
                        "        <td>" + user.getName() + "</td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "        <th>Father's Name</th>\n" +
                        "        <td>" + user.getFatherName() + "</td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "        <th>Aadhar Number</th>\n" +
                        "        <td>" + user.getAadharNum() + "</td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "        <th>Phone Number</th>\n" +
                        "        <td>" + user.getPhoneNum() + "</td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "        <th>Place</th>\n" +
                        "        <td>" + user.getPlace() + "</td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "        <th>Amount</th>\n" +
                        "        <td>" + user.getAmount() + "</td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "        <th>Balance</th>\n" +
                        "        <td>" + user.getBalance() + "</td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "        <th>Approx Due Date</th>\n" +
                        "        <td>" + user.getDuedate() + "</td>\n" +
                        "    </tr>\n" +
                        "</table>\n" +
                        "</body>\n" +
                        "</html>";

                Intent intent = new Intent(context, InvoiceActivity.class);
                intent.putExtra("htmlTemplate", htmlTemplate);
                context.startActivity(intent);
            }
        });


        String balance = user.getBalance();
        int tagIconResId = balance.equals("0") ? R.drawable.ic_baseline_check_circle_outline_24 : R.drawable.ic_baseline_cancel_24;
        holder.tagImageView.setImageResource(tagIconResId);

        int daysPending = user.getDaysPending();
        if (daysPending > 0) {
            holder.daysPending.setVisibility(View.VISIBLE);
            String daysPendingText = context.getString(R.string.days_pending, daysPending);
            holder.daysPending.setText(daysPendingText);
        } else {
            holder.daysPending.setVisibility(View.GONE);
        }


        Dialog dialog = new Dialog(context);
        databaseReference = FirebaseDatabase.getInstance().getReference("Sales");
        dialog.setContentView(R.layout.app_update_sales);
        EditText edtName = dialog.findViewById(R.id.editName);
        EditText edtFatherName = dialog.findViewById(R.id.editFatherName);
        EditText edtAadharNum = dialog.findViewById(R.id.editAadharNum);
        EditText edtPhoneNum = dialog.findViewById(R.id.editPhoneNum);
        EditText edtBillNumber = dialog.findViewById(R.id.editBillNum);
        EditText edtPlace = dialog.findViewById(R.id.editPlace);
        EditText edtAmt = dialog.findViewById(R.id.editAmt);
        EditText edtBal = dialog.findViewById(R.id.editBal);

        Button btnAction = dialog.findViewById(R.id.btnAction);
        TextView edtDate = dialog.findViewById(R.id.editDate);
        TextView edtDueDate = dialog.findViewById(R.id.editdueDate);
        edtName.setText(user.getName());
        edtFatherName.setText(user.getFatherName());
        edtAadharNum.setText(user.getAadharNum());
        edtPhoneNum.setText(user.getPhoneNum());
        edtBillNumber.setText(user.getBillno());
        edtPlace.setText(user.getPlace());
        edtAmt.setText(user.getAmount());
        edtBal.setText(user.getBalance());
        edtDate.setText(user.getDate());
        edtDueDate.setText(user.getDuedate());
        DatabaseReference userRef = databaseReference.child(user.getBillno());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperJava updatedData = snapshot.getValue(UserHelperJava.class);
                if (updatedData != null) {
                    edtName.setText(updatedData.getName());
                    edtFatherName.setText(updatedData.getFatherName());
                    edtAadharNum.setText(updatedData.getAadharNum());
                    edtPhoneNum.setText(updatedData.getPhoneNum());
                    edtBillNumber.setText(updatedData.getBillno());
                    edtPlace.setText(updatedData.getPlace());
                    edtAmt.setText(updatedData.getAmount());
                    edtBal.setText(updatedData.getBalance());
                    edtDate.setText(updatedData.getDate());
                    edtDueDate.setText(updatedData.getDuedate());// Update the date
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        userRef.addValueEventListener(valueEventListener);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString().trim();
                String fatherName = edtFatherName.getText().toString().trim();
                String aadharNum = edtAadharNum.getText().toString().trim();
                String phoneNum = edtPhoneNum.getText().toString().trim();
                String billno = edtBillNumber.getText().toString().trim();
                String place = edtPlace.getText().toString().trim();
                String amount = edtAmt.getText().toString().trim();
                String balance = edtBal.getText().toString().trim();
                String date = edtDate.getText().toString().trim();
                String duedate = edtDueDate.getText().toString().trim();
                // Get the updated date
                if (name.isEmpty() || billno.isEmpty() || place.isEmpty() || amount.isEmpty() || balance.isEmpty() || date.isEmpty() || duedate.isEmpty() || fatherName.isEmpty() || aadharNum.isEmpty() || phoneNum.isEmpty()) {
                    Toast.makeText(context, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserHelperJava updatedUser = new UserHelperJava(date, billno, name, fatherName, aadharNum, phoneNum, place, amount, balance, duedate);
                list.set(holder.getAdapterPosition(), updatedUser);
                notifyItemChanged(holder.getAdapterPosition());

                userRef.setValue(updatedUser);

                dialog.dismiss();
                int daysPending = updatedUser.getDaysPending();
                if (daysPending == 1) {
                    String smsMessage = "Dear " + updatedUser.getName() + ", this is a reminder from SG and SR Jewellery. Please note that you have a balance amount of " + updatedUser.getBalance() + " pending. Kindly clear the payment as soon as possible.Your due date is "+ updatedUser.getDuedate() + "Thank you!";
                    sendSms(updatedUser.getPhoneNum(), smsMessage);
                }
            }
        });




        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                userRef.removeEventListener(valueEventListener);
            }
        });

        holder.llrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });


        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(edtDate);
            }
        });
        edtDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(edtDueDate);
            }
        });
    }



    public void showDatePicker(TextView textView) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String selectedDate = dateFormat.format(calendar.getTime());
                textView.setText(selectedDate);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return listFilter;
    }
    private final Filter listFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<UserHelperJava> filteredList=new ArrayList<>();
            if(constraint ==null|| constraint.length()==0){
                filteredList.addAll(listfull);
            }
            else{
                String filterPattern=constraint.toString().toLowerCase().trim();

                for(UserHelperJava list:listfull){
                    if(list.name.toLowerCase().contains(filterPattern) || list.billno.toLowerCase().contains(filterPattern))
                        filteredList.add(list);

                }
            }
            FilterResults results=new FilterResults();
            results.values=filteredList;
            results.count=filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name,billno,date,balanceTextView,daysPending,duedate;
        LinearLayout llrow;
        ImageView tagImageView;
        ArrayList<UserHelperJava> list;
        Button invoiceButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.textName);
            billno=itemView.findViewById(R.id.textbillno);
            date=itemView.findViewById(R.id.Date);
            duedate=itemView.findViewById(R.id.duedate);
            llrow=itemView.findViewById(R.id.llrow);
//            balanceTextView=itemView.findViewById(R.id.balanceTextView);
            tagImageView=itemView.findViewById(R.id.tagImageView);
            daysPending = itemView.findViewById(R.id.daysPending);
            invoiceButton = itemView.findViewById(R.id.invoiceButton);
    }}


    private void sendSms(String phoneNum, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNum, null, message, null, null);
            Toast.makeText(context, "SMS notification sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Failed to send SMS notification", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}
//
//        }
//    }
//
//}



//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == PERMISSION_REQUEST_SMS) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, you can now send SMS
//            } else {
//                // Permission denied, handle accordingly
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
