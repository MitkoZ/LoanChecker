package com.zahariev.dimitar.bindmodels;

import java.util.HashMap;
import java.util.Map;

public class UserCurrencyBindModel {

    public String userId;
    public String currency;

    public UserCurrencyBindModel(String userId, String currency) {
        this.userId = userId;
        this.currency = currency;
    }

}
