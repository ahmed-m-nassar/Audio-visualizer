package com.example.android.audio_visualizer.Utils;

import android.os.Environment;

public class FilesUtils {
    public static String AUDIO_FOLDER_NAME = "Audio_Visualizer";
    public static String AUDIO_STORAGE_PATH = Environment.getExternalStorageDirectory().toString()
                                                + "/" + AUDIO_FOLDER_NAME;
    public static String PICTURE_STORAGE_PATH = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES)
                                                .toString();


    public static String getFileNameFromPath(String path) {
        int index = path.lastIndexOf("\\");
        return path.substring(index + 1);
    }


}
