package com.zahariev.dimitar.bindmodels;

import java.util.GregorianCalendar;

public class LoanBindModel {
    private String loanerName;
    private int amount;
    private GregorianCalendar returnDate;
    private String userId;

    public LoanBindModel(String loanerName, int amount, GregorianCalendar returnDate, String userId) {
        this.loanerName = loanerName;
        this.amount = amount;
        this.returnDate = returnDate;
        this.userId = userId;
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

    public GregorianCalendar getReturnDate() {
        return returnDate;
    }

    public String getLoanerName() {
        return loanerName;
    }
}
