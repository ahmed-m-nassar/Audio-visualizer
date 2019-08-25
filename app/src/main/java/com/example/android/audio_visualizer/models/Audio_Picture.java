package com.example.android.audio_visualizer.models;

public class Audio_Picture {
    private String mAudioPath;
    private String mPicturePath;
    private String mSnapTime;

    public Audio_Picture(String mAudioPath, String mPicturePath, String mSnapTime) {
        this.mAudioPath = mAudioPath;
        this.mPicturePath = mPicturePath;
        this.mSnapTime = mSnapTime;
    }

    public String getmAudioPath() {
        return mAudioPath;
    }

    public String getmPicturePath() {
        return mPicturePath;
    }

    public String getmSnapTime() {
        return mSnapTime;
    }
}
