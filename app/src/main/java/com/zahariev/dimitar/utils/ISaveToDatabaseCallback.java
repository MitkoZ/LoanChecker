package com.zahariev.dimitar.utils;

import com.zahariev.dimitar.bindmodels.UserBanknoteAmountBindModel;

import java.util.HashMap;

public interface ISaveToDatabaseCallback {
    void onCallback(HashMap<String, UserBanknoteAmountBindModel> myBanknotesDb);
}
