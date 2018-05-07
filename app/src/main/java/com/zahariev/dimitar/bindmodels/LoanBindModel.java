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
}
