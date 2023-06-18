package com.example.BillingDemo;



import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder> implements Filterable {
    Context context;
    ArrayList<UserHelperJava2> list;
    ArrayList<UserHelperJava2> listfull;
    DatabaseReference databaseReference;
    //private int[] tagIcons = {R.drawable.ic_baseline_done_24, R.drawable.ic_baseline_close_24};

    public MyAdapter2(Context context, ArrayList<UserHelperJava2> list) {
        this.context = context;
        this.listfull = list;
        this.list=new ArrayList<>(listfull);

    }
    public void setList(ArrayList<UserHelperJava2> filteredList) {
        list.clear();
        list.addAll(filteredList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MyAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.userentry2, parent,false);
        return new MyViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull MyAdapter2.MyViewHolder holder, int position) {
        UserHelperJava2 user = list.get(holder.getAdapterPosition());
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
        databaseReference = FirebaseDatabase.getInstance().getReference("Loans");
        dialog.setContentView(R.layout.app_update);
        EditText edtName = dialog.findViewById(R.id.editName2);
        EditText edtBillNumber = dialog.findViewById(R.id.editBillNum2);
        EditText edtPlace = dialog.findViewById(R.id.editPlace2);
        EditText edtAmt = dialog.findViewById(R.id.editAmt2);
        EditText edtBal = dialog.findViewById(R.id.editBal2);
        Button btnAction = dialog.findViewById(R.id.btnAction2);
        TextView edtDate = dialog.findViewById(R.id.editDate2);
        TextView edtDueDate=dialog.findViewById(R.id.editdueDate2);

        Spinner edtloanTypeSpinner=dialog.findViewById(R.id.edtloanTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.loan_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtloanTypeSpinner.setAdapter(adapter);

        Spinner edtmetalSpinner=dialog.findViewById(R.id.edtmetalSpinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(context, R.array.metal_types, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtmetalSpinner.setAdapter(adapter1);

       // Spinner edtjewelSpinner = dialog.findViewById(R.id.edtjewelSpinner);

        Spinner edtJewelTypeSpinner = dialog.findViewById(R.id.edtjewelSpinner);
        final ArrayAdapter<CharSequence>[] jewelAdapter = new ArrayAdapter[]{null};

        edtmetalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String metalType = parent.getItemAtPosition(position).toString();

                if (metalType.equals("Gold")) {
                    jewelAdapter[0] = ArrayAdapter.createFromResource(context, R.array.gold_jewel_types, android.R.layout.simple_spinner_item);
                } else if (metalType.equals("Silver")) {
                    jewelAdapter[0] = ArrayAdapter.createFromResource(context, R.array.silver_jewel_types, android.R.layout.simple_spinner_item);
                } else {
                    return;
                }

                jewelAdapter[0].setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                edtJewelTypeSpinner.setAdapter(jewelAdapter[0]);
                edtJewelTypeSpinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        edtName.setText(user.getName());
        edtBillNumber.setText(user.getBillno());
        edtPlace.setText(user.getPlace());
        edtAmt.setText(user.getAmount());
        edtBal.setText(user.getBalance());
        edtDate.setText(user.getDate());
        edtDueDate.setText(user.getDuedate());
        edtloanTypeSpinner.setSelection(adapter.getPosition(user.getLoanType()));
        edtmetalSpinner.setSelection(adapter1.getPosition(user.getMetalType()));


        DatabaseReference userRef = databaseReference.child(user.getBillno());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperJava2 updatedData = snapshot.getValue(UserHelperJava2.class);
                if (updatedData != null) {
                    edtName.setText(updatedData.getName());
                    edtBillNumber.setText(updatedData.getBillno());
                    edtPlace.setText(updatedData.getPlace());
                    edtAmt.setText(updatedData.getAmount());
                    edtBal.setText(updatedData.getBalance());
                    edtDate.setText(updatedData.getDate());
                    edtDueDate.setText(updatedData.getDuedate());// Update the date
                    edtloanTypeSpinner.setSelection(adapter.getPosition(updatedData.getLoanType()));
                    edtmetalSpinner.setSelection(adapter1.getPosition(updatedData.getMetalType()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
//        String metalType = user.getMetalType();
//        ArrayAdapter<CharSequence> jewelAdapter;
//
//        if (metalType != null && metalType.equals("Gold")) {
//            jewelAdapter = ArrayAdapter.createFromResource(
//                    context,
//                    R.array.gold_jewel_types,
//                    android.R.layout.simple_spinner_item
//            );
//        } else {
//            jewelAdapter = ArrayAdapter.createFromResource(
//                    context,
//                    R.array.silver_jewel_types,
//                    android.R.layout.simple_spinner_item
//            );}
//
//        jewelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        edtjewelSpinner.setAdapter(jewelAdapter);
//        String metalType = user.getMetalType();
//        ArrayAdapter<CharSequence> jewelAdapter;
//        String jewelType;
//
//        if (metalType != null && metalType.equals("Gold")) {
//            jewelAdapter = ArrayAdapter.createFromResource(
//                    context,
//                    R.array.gold_jewel_types,
//                    android.R.layout.simple_spinner_item
//            );
//        } else {
//            jewelAdapter = ArrayAdapter.createFromResource(
//                    context,
//                    R.array.silver_jewel_types,
//                    android.R.layout.simple_spinner_item
//            );
//        }
//
//        jewelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        edtJewelTypeSpinner.setAdapter(jewelAdapter);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

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
                        String loanType = edtloanTypeSpinner.getSelectedItem().toString().trim();
                        String metalType = edtmetalSpinner.getSelectedItem().toString().trim();
                        String jewelType = edtJewelTypeSpinner.getSelectedItem().toString().trim();

                        // Get the updated date
                        if (name.isEmpty() || billno.isEmpty() || place.isEmpty() || amount.isEmpty() || balance.isEmpty() || date.isEmpty() || duedate.isEmpty()) {
                            Toast.makeText(context, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        UserHelperJava2 updatedUser = new UserHelperJava2(date, billno, name, place, amount, balance, duedate, loanType, metalType, jewelType);
                        list.set(holder.getAdapterPosition(), updatedUser);
                        notifyItemChanged(holder.getAdapterPosition());


                        userRef.setValue(updatedUser)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Data updated successfully", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Failed to update data", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
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
            ArrayList<UserHelperJava2> filteredList=new ArrayList<>();
            if(constraint ==null|| constraint.length()==0){
                filteredList.addAll(listfull);
            }
            else{
                String filterPattern=constraint.toString().toLowerCase().trim();

                for(UserHelperJava2 list:listfull){
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
 //         balanceTextView=itemView.findViewById(R.id.balanceTextView);
            tagImageView=itemView.findViewById(R.id.tagImageView);
            daysPending = itemView.findViewById(R.id.daysPending);

        }
    }

}



