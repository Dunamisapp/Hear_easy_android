package com.heareasy.model;

public class SubscriptionListModel {
    String month,offPrice,price,cutPrice;
    int backgroud;

    public SubscriptionListModel(String month, String offPrice, String price, String cutPrice, int backgroud) {
        this.month = month;
        this.offPrice = offPrice;
        this.price = price;
        this.cutPrice = cutPrice;
        this.backgroud = backgroud;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getOffPrice() {
        return offPrice;
    }

    public void setOffPrice(String offPrice) {
        this.offPrice = offPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCutPrice() {
        return cutPrice;
    }

    public void setCutPrice(String cutPrice) {
        this.cutPrice = cutPrice;
    }

    public int getBackgroud() {
        return backgroud;
    }

    public void setBackgroud(int backgroud) {
        this.backgroud = backgroud;
    }
}
