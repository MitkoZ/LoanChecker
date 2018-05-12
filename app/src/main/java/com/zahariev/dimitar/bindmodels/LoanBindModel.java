package com.zahariev.dimitar.bindmodels;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class LoanBindModel {
    private String loanerName;
    private int amount;
    private Calendar returnDate;
    private String userId;
    private String currency;

    public LoanBindModel(String loanerName, int amount, Calendar returnDate, String userId, String currency) {
        this.loanerName = loanerName;
        this.amount = amount;
        this.returnDate = returnDate;
        this.userId = userId;
        this.currency = currency;
    }

    public int getAmount() {
        return amount;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setLoanerName(String loanerName) {
        this.loanerName = loanerName;
    }

    public void setReturnDate(GregorianCalendar returnDate) {
        this.returnDate = returnDate;
    }

    public String getUserId() {
        return userId;
    }

    public String getReturnDate() {
        return Integer.toString(this.returnDate.get(Calendar.DAY_OF_MONTH)) + "/" + Integer.toString(this.returnDate.get(Calendar.MONTH) + 1) + "/" + Integer.toString(this.returnDate.get(Calendar.YEAR));
        // format {day}/{month}/year
    }

    public String getLoanerName() {
        return loanerName;
    }

    public String getCurrency() {
        return currency;
    }

}
