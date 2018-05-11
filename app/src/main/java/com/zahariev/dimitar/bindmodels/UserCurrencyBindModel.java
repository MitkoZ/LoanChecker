package com.zahariev.dimitar.bindmodels;

import java.util.HashMap;
import java.util.Map;

public class UserCurrencyBindModel {

    private String userId;
    private String currency;

    public UserCurrencyBindModel(String userId, String currency) {
        this.userId = userId;
        this.currency = currency;
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
