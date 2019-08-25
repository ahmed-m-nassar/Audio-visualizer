package com.example.android.audio_visualizer.models;

public class Audio {
    private String mName;
    private String mPath;
    private String mDate;
    private String mDuration;
    private int    mSize;

    public Audio(String mName, String mPath, String mDate, String mDuration, int mSize) {
        this.mName = mName;
        this.mPath = mPath;
        this.mDate = mDate;
        this.mDuration = mDuration;
        this.mSize = mSize;
    }

    public String getmName() {
        return mName;
    }

    public String getmPath() {
        return mPath;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmDuration() {
        return mDuration;
    }

    public int getmSize() {
        return mSize;
    }
}
