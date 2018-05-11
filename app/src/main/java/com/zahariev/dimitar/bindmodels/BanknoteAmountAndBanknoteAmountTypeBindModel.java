package com.zahariev.dimitar.bindmodels;

public class BanknoteAmountAndBanknoteAmountTypeBindModel {

    private int banknoteAmountType;
    private int banknoteAmount;
    private String id;

    public void setBanknoteAmount(int banknoteAmount) {
        this.banknoteAmount = banknoteAmount;
    }

    public void setBanknoteAmountType(int banknoteAmountType) {
        this.banknoteAmountType = banknoteAmountType;
    }

    public int getBanknoteAmount() {
        return banknoteAmount;
    }

    public int getBanknoteAmountType() {
        return banknoteAmountType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}