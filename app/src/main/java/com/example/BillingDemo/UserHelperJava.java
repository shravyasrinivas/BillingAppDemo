package com.example.BillingDemo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserHelperJava {
    String billno,name,place, amount,balance;
    String date;

    public UserHelperJava() {

    }

    public UserHelperJava(String date,String billno, String name, String place, String amount, String balance) {
        this.date=date;
        this.billno = billno;
        this.name = name;
        this.place = place;
        this.amount = amount;
        this.balance = balance;
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

    public void setDate(String  date) {
        this.date = date;
    }
}