package com.zahariev.dimitar.bindmodels;

public class UserBanknoteAmountBindModel {
    private String id;
    private String userId;
    private String banknoteType;
    private int banknoteAmount;

    public UserBanknoteAmountBindModel() {

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

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
