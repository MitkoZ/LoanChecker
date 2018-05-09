package com.zahariev.dimitar.bindmodels;

public class UserBanknoteAmountBindModel {
    public String id;
    public String userId;
    public String banknoteType;
    public int banknoteAmount;

    public UserBanknoteAmountBindModel(){

    }

    public int getBanknoteAmount() {
        return banknoteAmount;
    }

    public String getBanknoteType() {
        return banknoteType;
    }

    public String getUserId() {
        return userId;
    }

    public void setBanknoteAmount(int banknoteAmount) {
        this.banknoteAmount = banknoteAmount;
    }

    public void setBanknoteType(String banknoteType) {
        this.banknoteType = banknoteType;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
