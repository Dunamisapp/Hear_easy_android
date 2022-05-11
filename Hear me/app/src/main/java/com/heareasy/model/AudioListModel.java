package com.heareasy.model;

import java.io.File;

public class AudioListModel {

    String audioName, audio_duration, audio_date;
    File file;
    boolean isFavourite;

    public AudioListModel(File file, String audioName, String audio_duration, String audio_date,boolean isFavourite) {
        this.audioName = audioName;
        this.audio_duration = audio_duration;
        this.audio_date = audio_date;
        this.file = file;
        this.isFavourite = isFavourite;
    }

    public AudioListModel() {
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public String getAudio_duration() {
        return audio_duration;
    }

    public void setAudio_duration(String audio_duration) {
        this.audio_duration = audio_duration;
    }

    public String getAudio_date() {
        return audio_date;
    }

    public void setAudio_date(String audio_date) {
        this.audio_date = audio_date;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
