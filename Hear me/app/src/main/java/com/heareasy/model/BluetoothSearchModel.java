package com.heareasy.model;

public class BluetoothSearchModel {

    String b_name,b_address;

    public BluetoothSearchModel(String b_name, String b_address) {
        this.b_name = b_name;
        this.b_address = b_address;
    }

    public String getB_name() {
        return b_name;
    }

    public void setB_name(String b_name) {
        this.b_name = b_name;
    }

    public String getB_address() {
        return b_address;
    }

    public void setB_address(String b_address) {
        this.b_address = b_address;
    }
}
