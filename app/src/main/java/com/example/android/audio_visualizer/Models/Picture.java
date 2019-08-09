package com.example.android.audio_visualizer.Models;

public class Picture {
    private String mName;
    private String mPath;
    private String mDate;

    public Picture(String mName, String mPath, String mDate) {
        this.mName = mName;
        this.mPath = mPath;
        this.mDate = mDate;
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
}
