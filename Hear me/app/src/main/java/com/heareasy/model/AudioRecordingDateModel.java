package com.heareasy.model;



public class AudioRecordingDateModel {
    String duration,date;

    public AudioRecordingDateModel() {
    }

    public AudioRecordingDateModel(String duration, String date) {
        this.duration = duration;
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
