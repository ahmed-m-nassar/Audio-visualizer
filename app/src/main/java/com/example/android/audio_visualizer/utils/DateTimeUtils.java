package com.example.android.audio_visualizer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
  public  static String getCurrentDate () {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

   public  static String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }
}
