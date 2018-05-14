package com.zahariev.dimitar.bindmodels;

public class UserCurrencyBindModel {

    private String userId;
    private String currency;

    public UserCurrencyBindModel(String userId, String currency) {
        this.userId = userId;
        this.currency = currency;
    }

    public  UserCurrencyBindModel(){

    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }
}
