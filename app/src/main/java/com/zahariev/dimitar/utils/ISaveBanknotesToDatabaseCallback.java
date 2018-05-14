package com.zahariev.dimitar.utils;

import com.zahariev.dimitar.bindmodels.UserBanknoteAmountBindModel;

import java.util.HashMap;

public interface ISaveBanknotesToDatabaseCallback {
    void onCallback(HashMap<String, UserBanknoteAmountBindModel> myBanknotesDb);
}
