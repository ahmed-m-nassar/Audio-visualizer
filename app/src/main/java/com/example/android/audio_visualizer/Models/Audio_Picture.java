package com.example.android.audio_visualizer.Models;

public class Audio_Picture {
    private String mAudioName;
    private String mPictureName;
    private int    mSnapTime;

    public Audio_Picture(String mAudioName, String mPictureName, int mSnapTime) {
        this.mAudioName = mAudioName;
        this.mPictureName = mPictureName;
        this.mSnapTime = mSnapTime;
    }

    public String getmAudioName() {
        return mAudioName;
    }

    public String getmPictureName() {
        return mPictureName;
    }

    public int getmSnapTime() {
        return mSnapTime;
    }
}
