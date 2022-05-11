package com.heareasy.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class MySubscriptionModel {

    @SerializedName("responseMessage")
    @Expose
    private String responseMessage;
    @SerializedName("responseCode")
    @Expose
    private String responseCode;
    @SerializedName("data")
    @Expose
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("customer_id")
        @Expose
        private Integer customerId;
        @SerializedName("subscription_package_id")
        @Expose
        private Integer subscriptionPackageId;
        @SerializedName("starting_date")
        @Expose
        private String startingDate;
        @SerializedName("expiry_date")
        @Expose
        private String expiryDate;
        @SerializedName("package_amount")
        @Expose
        private Double packageAmount;
        @SerializedName("payable_amount")
        @Expose
        private Double payableAmount;
        @SerializedName("total_discount")
        @Expose
        private Double totalDiscount;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("created_by")
        @Expose
        private Integer createdBy;
        @SerializedName("updated_by")
        @Expose
        private String updatedBy;
        @SerializedName("deleted_at")
        @Expose
        private String deletedAt;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("coupon_id")
        @Expose
        private Integer couponId;
        @SerializedName("subscription")
        @Expose
        private Subscription subscription;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Integer customerId) {
            this.customerId = customerId;
        }

        public Integer getSubscriptionPackageId() {
            return subscriptionPackageId;
        }

        public void setSubscriptionPackageId(Integer subscriptionPackageId) {
            this.subscriptionPackageId = subscriptionPackageId;
        }

        public String getStartingDate() {
            return startingDate;
        }

        public void setStartingDate(String startingDate) {
            this.startingDate = startingDate;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        public Double getPackageAmount() {
            return packageAmount;
        }

        public void setPackageAmount(Double packageAmount) {
            this.packageAmount = packageAmount;
        }

        public Double getPayableAmount() {
            return payableAmount;
        }

        public void setPayableAmount(Double payableAmount) {
            this.payableAmount = payableAmount;
        }

        public Double getTotalDiscount() {
            return totalDiscount;
        }

        public void setTotalDiscount(Double totalDiscount) {
            this.totalDiscount = totalDiscount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Integer createdBy) {
            this.createdBy = createdBy;
        }

        public String getUpdatedBy() {
            return updatedBy;
        }

        public void setUpdatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
        }

        public String getDeletedAt() {
            return deletedAt;
        }

        public void setDeletedAt(String deletedAt) {
            this.deletedAt = deletedAt;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Integer getCouponId() {
            return couponId;
        }

        public void setCouponId(Integer couponId) {
            this.couponId = couponId;
        }

        public Subscription getSubscription() {
            return subscription;
        }

        public void setSubscription(Subscription subscription) {
            this.subscription = subscription;
        }
        public class Subscription {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("title")
            @Expose
            private String title;
            @SerializedName("subtitle")
            @Expose
            private String subtitle;
            @SerializedName("months")
            @Expose
            private Integer months;
            @SerializedName("package_created_date")
            @Expose
            private String packageCreatedDate;
            @SerializedName("original_price")
            @Expose
            private Double originalPrice;
            @SerializedName("selling_amount")
            @Expose
            private Double sellingAmount;
            @SerializedName("sort_desc")
            @Expose
            private String sortDesc;
            @SerializedName("descriptions")
            @Expose
            private String descriptions;
            @SerializedName("created_by")
            @Expose
            private Integer createdBy;
            @SerializedName("status")
            @Expose
            private String status;
            @SerializedName("updated_by")
            @Expose
            private String updatedBy;
            @SerializedName("deleted_at")
            @Expose
            private String deletedAt;
            @SerializedName("created_at")
            @Expose
            private String createdAt;
            @SerializedName("updated_at")
            @Expose
            private String updatedAt;

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

            public Integer getMonths() {
                return months;
            }

            public void setMonths(Integer months) {
                this.months = months;
            }

            public String getPackageCreatedDate() {
                return packageCreatedDate;
            }

            public void setPackageCreatedDate(String packageCreatedDate) {
                this.packageCreatedDate = packageCreatedDate;
            }

            public Double getOriginalPrice() {
                return originalPrice;
            }

            public void setOriginalPrice(Double originalPrice) {
                this.originalPrice = originalPrice;
            }

            public Double getSellingAmount() {
                return sellingAmount;
            }

            public void setSellingAmount(Double sellingAmount) {
                this.sellingAmount = sellingAmount;
            }

            public String getSortDesc() {
                return sortDesc;
            }

            public void setSortDesc(String sortDesc) {
                this.sortDesc = sortDesc;
            }

            public String getDescriptions() {
                return descriptions;
            }

            public void setDescriptions(String descriptions) {
                this.descriptions = descriptions;
            }

            public Integer getCreatedBy() {
                return createdBy;
            }

            public void setCreatedBy(Integer createdBy) {
                this.createdBy = createdBy;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getUpdatedBy() {
                return updatedBy;
            }

            public void setUpdatedBy(String updatedBy) {
                this.updatedBy = updatedBy;
            }

            public String getDeletedAt() {
                return deletedAt;
            }

            public void setDeletedAt(String deletedAt) {
                this.deletedAt = deletedAt;
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public String getUpdatedAt() {
                return updatedAt;
            }

            public void setUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
            }

        }
    }
}