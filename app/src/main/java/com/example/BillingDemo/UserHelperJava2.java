package com.example.BillingDemo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class UserHelperJava2 {
    String billno, name, place, amount, balance;
    String date, duedate;
    private long duedateTimestamp; // New field for storing timestamp value of the due date

     String loanType;// New field for loan type
     String metalType;

    public UserHelperJava2() {

    }

    public UserHelperJava2(String date, String billno, String name, String place, String amount, String balance, String duedate,String loanType,String metalType) {
        this.date = date;
        this.billno = billno;
        this.name = name;
        this.place = place;
        this.amount = amount;
        this.balance = balance;
        this.duedate = duedate;
        this.loanType= loanType;
        this.metalType= metalType;

        // Calculate and set the timestamp value of the due date
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date dueDate = dateFormat.parse(duedate);
            this.duedateTimestamp = dueDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }
    public String getMetalType(){
        return metalType;
    }
    public void setMetalType(String metalType){
        this.metalType=metalType;
    }
    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuedate() {
        return duedate;
    }

    public void setDuedate(String duedate) {
        this.duedate = duedate;

        // Calculate and set the timestamp value of the due date
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date dueDate = dateFormat.parse(duedate);
            this.duedateTimestamp = dueDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public long getDuedateTimestamp() {
        return duedateTimestamp;
    }

    public int getDaysPending() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date currentDate = new Date();
            Date dueDate = dateFormat.parse(duedate);
            long diffInMilliseconds = dueDate.getTime() - currentDate.getTime();
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMilliseconds);
            return (int) diffInDays;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
