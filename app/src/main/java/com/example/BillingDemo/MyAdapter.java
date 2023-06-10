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
import android.widget.ImageView;
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
    //private int[] tagIcons = {R.drawable.ic_baseline_done_24, R.drawable.ic_baseline_close_24};

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
        dialog.setContentView(R.layout.app_update);
        EditText edtName = dialog.findViewById(R.id.editName);
        EditText edtBillNumber = dialog.findViewById(R.id.editBillNum);
        EditText edtPlace = dialog.findViewById(R.id.editPlace);
        EditText edtAmt = dialog.findViewById(R.id.editAmt);
        EditText edtBal = dialog.findViewById(R.id.editBal);

        Button btnAction = dialog.findViewById(R.id.btnAction);
        TextView edtDate = dialog.findViewById(R.id.editDate);
        TextView edtDueDate=dialog.findViewById(R.id.editdueDate);
        edtName.setText(user.getName());
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
                String billno = edtBillNumber.getText().toString().trim();
                String place = edtPlace.getText().toString().trim();
                String amount = edtAmt.getText().toString().trim();
                String balance = edtBal.getText().toString().trim();
                String date = edtDate.getText().toString().trim();
                String duedate = edtDueDate.getText().toString().trim();
                // Get the updated date
                if (name.isEmpty() || billno.isEmpty() || place.isEmpty() || amount.isEmpty() || balance.isEmpty() || date.isEmpty() || duedate.isEmpty()) {
                    Toast.makeText(context, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserHelperJava updatedUser = new UserHelperJava(date, billno, name, place, amount, balance,duedate);
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


        }
    }

}



