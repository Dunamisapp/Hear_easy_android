package com.heareasy.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubscriptionListResponser {

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
        @SerializedName("offer_start_date")
        @Expose
        private String offer_start_date;
        @SerializedName("offer_end_date")
        @Expose
        private String offer_end_date;
        @SerializedName("original_price")
        @Expose
        private Double originalPrice;
        @SerializedName("offer_price")
        @Expose
        private Double offerPrice;
        @SerializedName("discount_type")
        @Expose
        private String discountType;
        @SerializedName("discount_price")
        @Expose
        private Double discountPrice;

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

        public String getOffer_start_date() {
            return offer_start_date;
        }

        public void setOffer_start_date(String offer_start_date) {
            this.offer_start_date = offer_start_date;
        }

        public String getOffer_end_date() {
            return offer_end_date;
        }

        public void setOffer_end_date(String offer_end_date) {
            this.offer_end_date = offer_end_date;
        }

        public String getDiscountType() {
            return discountType;
        }

        public void setDiscountType(String discountType) {
            this.discountType = discountType;
        }

        public String getMonths() {
            return months;
        }

        public void setMonths(String months) {
            this.months = months;
        }

        public Double getOriginalPrice() {
            return originalPrice;
        }

        public void setOriginalPrice(Double originalPrice) {
            this.originalPrice = originalPrice;
        }

        public Double getOfferPrice() {
            return offerPrice;
        }

        public void setOfferPrice(Double offerPrice) {
            this.offerPrice = offerPrice;
        }

        public Double getDiscountPrice() {
            return discountPrice;
        }

        public void setDiscountPrice(Double discountPrice) {
            this.discountPrice = discountPrice;
        }
    }

}
