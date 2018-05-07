package com.zahariev.dimitar.bindmodels;

import java.util.HashMap;
import java.util.Map;

public class UserCurrencyBindModel {

    public String userId;
    public String currency;

    //
//    public UserCurrencyBindModel() {
//
//    }
//todo remove?
    public UserCurrencyBindModel(String userId, String currency) {
        this.userId = userId;
        this.currency = currency;
    }

//    public Map<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("userId", userId);
//        result.put("currency", currency);
//
//        return result;
//    }
//todo remove?
}
