package com.zahariev.dimitar.bindmodels;

import com.google.firebase.database.Exclude;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.zahariev.dimitar.utils.Utils.stringArrayToIntArray;

public class LoanBindModel {
    private String loaneeName;
    private int amount;
    private Calendar returnDate;
    private String userId;
    private String currency;

    public  LoanBindModel(){

    }

    public LoanBindModel(String loaneeName, int amount, Calendar returnDate, String userId, String currency) {
        this.loaneeName = loaneeName;
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

    public void setLoaneeName(String loanerName) {
        this.loaneeName = loanerName;
    }

    @Exclude
    public void setReturnDate(GregorianCalendar returnDate) {
        this.returnDate = returnDate;
    }

    public void setReturnDate(String returnDate) {
        // format {day}/{month}/year
        String[] returnDateStringArray = returnDate.split("/");
        int[] returnDateIntArray = stringArrayToIntArray(returnDateStringArray);
        this.returnDate = new GregorianCalendar(returnDateIntArray[2], returnDateIntArray[1] - 1, returnDateIntArray[0]);
    }

    public String getUserId() {
        return userId;
    }

    public String getReturnDate() {
        return Integer.toString(this.returnDate.get(Calendar.DAY_OF_MONTH)) + "/" + Integer.toString(this.returnDate.get(Calendar.MONTH) + 1) + "/" + Integer.toString(this.returnDate.get(Calendar.YEAR));
        // format {day}/{month}/year
    }

    public String getLoaneeName() {
        return loaneeName;
    }

    public String getCurrency() {
        return currency;
    }

}
