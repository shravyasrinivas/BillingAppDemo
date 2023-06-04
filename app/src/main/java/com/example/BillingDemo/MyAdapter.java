package com.example.BillingDemo;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements Filterable {
    Context context;
    ArrayList<UserHelperJava> list;
    ArrayList<UserHelperJava> listfull;
    DatabaseReference databaseReference;


    public MyAdapter(Context context, ArrayList<UserHelperJava> list) {
        this.context = context;
        this.listfull = list;
        this.list=new ArrayList<>(listfull);

    }



    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.userentry, parent,false);
        return new MyViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        UserHelperJava user = list.get(holder.getAdapterPosition());
        holder.name.setText(user.getName());
        holder.billno.setText(user.getBillno());

        Dialog dialog = new Dialog(context);
        databaseReference = FirebaseDatabase.getInstance().getReference("Sales");
        dialog.setContentView(R.layout.app_update);
        EditText edtName = dialog.findViewById(R.id.editName);
        EditText edtBillNumber = dialog.findViewById(R.id.editBillNum);
        EditText edtPlace = dialog.findViewById(R.id.editPlace);
        EditText edtAmt = dialog.findViewById(R.id.editAmt);
        EditText edtBal = dialog.findViewById(R.id.editBal);
        Button btnAction = dialog.findViewById(R.id.btnAction);
        TextView edtDate = dialog.findViewById(R.id.editDate);
        edtName.setText(user.getName());
        edtBillNumber.setText(user.getBillno());
        edtPlace.setText(user.getPlace());
        edtAmt.setText(user.getAmount());
        edtBal.setText(user.getBalance());
        edtDate.setText(user.getDate());
        DatabaseReference userRef = databaseReference.child(user.getBillno());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperJava updatedData = snapshot.getValue(UserHelperJava.class);
                if (updatedData != null) {
                    edtName.setText(updatedData.getName());
                    edtBillNumber.setText(updatedData.getBillno());
                    edtPlace.setText(updatedData.getPlace());
                    edtAmt.setText(updatedData.getAmount());
                    edtBal.setText(updatedData.getBalance());
                    edtDate.setText(updatedData.getDate()); // Update the date
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
                String billno = edtBillNumber.getText().toString().trim();
                String place = edtPlace.getText().toString().trim();
                String amount = edtAmt.getText().toString().trim();
                String balance = edtBal.getText().toString().trim();
                String date = edtDate.getText().toString().trim(); // Get the updated date
                if (name.isEmpty() || billno.isEmpty() || place.isEmpty() || amount.isEmpty() || balance.isEmpty() || date.isEmpty()) {
                    Toast.makeText(context, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserHelperJava updatedUser = new UserHelperJava(date, billno, name, place, amount, balance);
                list.set(holder.getAdapterPosition(), updatedUser);
                notifyItemChanged(holder.getAdapterPosition());

                userRef.setValue(updatedUser);

                dialog.dismiss();
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

//    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
//        UserHelperJava user = list.get(holder.getAdapterPosition());
//        holder.name.setText(user.getName());
//        holder.billno.setText(user.getBillno());
//
//
//
//        Dialog dialog = new Dialog(context);
//        databaseReference = FirebaseDatabase.getInstance().getReference("Sales");
//        dialog.setContentView(R.layout.app_update);
//        EditText edtName = dialog.findViewById(R.id.editName);
//        EditText edtBillNumber = dialog.findViewById(R.id.editBillNum);
//        EditText edtPlace = dialog.findViewById(R.id.editPlace);
//        EditText edtAmt = dialog.findViewById(R.id.editAmt);
//
//        EditText edtBal = dialog.findViewById(R.id.editBal);
//        Button btnAction = dialog.findViewById(R.id.btnAction);
//
//
//        edtName.setText(user.getName());
//        edtBillNumber.setText(user.getBillno());
//        edtPlace.setText(user.getPlace());
//        edtAmt.setText(user.getAmount());
//        edtBal.setText(user.getBalance());
//
//
//        // Add a ValueEventListener to update the UI when data changes in the database
//        DatabaseReference userRef = databaseReference.child(user.getBillno()); // Assuming you have the ID field in the UserHelperJava class
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                // Retrieve the updated user data
//                UserHelperJava updatedData = snapshot.getValue(UserHelperJava.class);
//                if (updatedData != null) {
//                    // Update the UI with the updated data
//                    edtName.setText(updatedData.getName());
//
//                    edtBillNumber.setText(updatedData.getBillno());
//                    edtPlace.setText(updatedData.getPlace());
//                    edtAmt.setText(updatedData.getAmount());
//                    edtBal.setText(updatedData.getBalance());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle any database error
//                Toast.makeText(context, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        };
//        userRef.addValueEventListener(valueEventListener);
//
//        btnAction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String name = edtName.getText().toString().trim();
//                String billno = edtBillNumber.getText().toString().trim();
//                String place = edtPlace.getText().toString().trim();
//                String amount = edtAmt.getText().toString().trim();
//                String balance = edtBal.getText().toString().trim();
//String currentDate="";
//                if (name.isEmpty()) {
//                    Toast.makeText(context, "Enter name", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (billno.isEmpty()) {
//                    Toast.makeText(context, "Enter bill number", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (amount.isEmpty()) {
//                    Toast.makeText(context, "Enter amount", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (balance.isEmpty()) {
//                    Toast.makeText(context, "Enter balance", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (place.isEmpty()) {
//                    Toast.makeText(context, "Enter place", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                UserHelperJava updatedUser = new UserHelperJava(currentDate,billno, name, place, amount, balance);
//                list.set(holder.getAdapterPosition(), updatedUser);
//                notifyItemChanged(holder.getAdapterPosition());
//
//
//                userRef.setValue(updatedUser);
//
//                dialog.dismiss();
//            }
//        });
//
//        // Add dismiss listener to remove the ValueEventListener when the dialog is dismissed
//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialogInterface) {
//                userRef.removeEventListener(valueEventListener);
//            }
//        });
//
//        holder.llrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.show();
//            }
//        });
//    }





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
        TextView name,billno,date;
        LinearLayout llrow;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.textName);
            billno=itemView.findViewById(R.id.textbillno);
            date=itemView.findViewById(R.id.Date);

            llrow=itemView.findViewById(R.id.llrow);


        }
    }
}
