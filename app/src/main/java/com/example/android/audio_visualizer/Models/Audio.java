package com.example.android.audio_visualizer.Models;

public class Audio {
    private String mName;
    private String mPath;
    private String mDate;
    private int    mDuration;
    private int    mSize;

    public Audio(String mName, String mPath, String mDate, int mDuration, int mSize) {
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

    public int getmDuration() {
        return mDuration;
    }

    public int getmSize() {
        return mSize;
    }
}
