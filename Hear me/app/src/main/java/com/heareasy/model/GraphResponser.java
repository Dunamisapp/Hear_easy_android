package com.heareasy.model;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GraphResponser {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("time")
    @Expose
    private List<Time> time = null;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Time> getTime() {
        return time;
    }

    public void setTime(List<Time> time) {
        this.time = time;
    }
    public class Time {

        @SerializedName("duration")
        @Expose
        private String duration;

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

    }
}