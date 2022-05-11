package com.heareasy.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecordingReportModel {

    @SerializedName("responseMessage")
    @Expose
    private String responseMessage;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("responseCode")
    @Expose
    private String responseCode;

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public class Datum {

        @SerializedName("recording_date_time")
        @Expose
        private String recordingDateTime;
        @SerializedName("total_recording_time")
        @Expose
        private String totalRecordingTime;

        public String getRecordingDateTime() {
            return recordingDateTime;
        }

        public void setRecordingDateTime(String recordingDateTime) {
            this.recordingDateTime = recordingDateTime;
        }

        public String getTotalRecordingTime() {
            return totalRecordingTime;
        }

        public void setTotalRecordingTime(String totalRecordingTime) {
            this.totalRecordingTime = totalRecordingTime;
        }

    }

}
