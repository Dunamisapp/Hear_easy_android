package com.heareasy.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class GetSubscriptionListModel {

    @SerializedName("responseMessage")
    @Expose
    private String responseMessage;
    @SerializedName("responseCode")
    @Expose
    private String responseCode;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public class Datum {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("subtitle")
        @Expose
        private String subtitle;
        @SerializedName("expiry_date")
        @Expose
        private String expiryDate;
        @SerializedName("months")
        @Expose
        private String months;
        @SerializedName("original_price")
        @Expose
        private double originalPrice;
        @SerializedName("selling_amount")
        @Expose
        private double sellingAmount;
        @SerializedName("percentage_off")
        @Expose
        private Integer percentageOff;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        public String getMonths() {
            return months;
        }

        public void setMonths(String months) {
            this.months = months;
        }

        public double getOriginalPrice() {
            return originalPrice;
        }

        public void setOriginalPrice(double originalPrice) {
            this.originalPrice = originalPrice;
        }

        public double getSellingAmount() {
            return sellingAmount;
        }

        public void setSellingAmount(double sellingAmount) {
            this.sellingAmount = sellingAmount;
        }

        public Integer getPercentageOff() {
            return percentageOff;
        }

        public void setPercentageOff(Integer percentageOff) {
            this.percentageOff = percentageOff;
        }

    }
}
