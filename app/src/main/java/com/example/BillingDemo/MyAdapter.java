package com.example.BillingDemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements Filterable {
    Context context;
    ArrayList<UserHelperJava> list;
    ArrayList<UserHelperJava> listfull;


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
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    UserHelperJava user= list.get(position);
    holder.name.setText(user.getName());
        holder.billno.setText(user.getBillno());
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
TextView name,billno;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.textName);
            billno=itemView.findViewById(R.id.textbillno);
        }
    }
}
