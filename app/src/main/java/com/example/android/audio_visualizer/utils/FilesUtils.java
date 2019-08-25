package com.example.android.audio_visualizer.utils;

import android.os.Environment;

public class FilesUtils {
    public static String AUDIO_FOLDER_NAME = "Audio_Visualizer";
    public static String AUDIO_STORAGE_PATH = Environment.getExternalStorageDirectory().toString()
                                                + "/" + AUDIO_FOLDER_NAME;
    public static String PICTURE_STORAGE_PATH = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES)
                                                .toString();


    public static String getFileNameFromPath(String path) {
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }

    public static String replaceFileNameGivenFullPath(String path , String newName) {
        int index = path.lastIndexOf("/");
        return path.substring(0 , index + 1) + newName;
    }

    public static String replacingInvalidFileNameCharacters(String name) {
        return name.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }

    public static String addExtentionToFileName(String name){
        //checking if the .3gb extention exists
        String extention = name.substring(name.length() - 4 ,name.length() );
        if(!(extention.toLowerCase().equals(".3gp"))) {
            return name + ".3gp";
        } else {
            return name;
        }
    }


}
