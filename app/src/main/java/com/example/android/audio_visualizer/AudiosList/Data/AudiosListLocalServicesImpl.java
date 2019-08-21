package com.example.android.audio_visualizer.AudiosList.Data;

import android.content.Context;
import android.database.Cursor;

import com.example.android.audio_visualizer.Base.DataBase.AudioVisualizerContract;
import com.example.android.audio_visualizer.Base.DataBase.DBHelper;
import com.example.android.audio_visualizer.Base.MyApp;
import com.example.android.audio_visualizer.Models.Audio;


import java.util.ArrayList;

public class AudiosListLocalServicesImpl extends DBHelper implements AudiosListLocalServices {

    public AudiosListLocalServicesImpl() {
        super(MyApp.getAppContext());
    }

    @Override
    public ArrayList<Audio> getAudios() {
        String query = "Select * From " + AudioVisualizerContract.Audio.Table_Name;
        Cursor cursor = select(query ,null);

        ArrayList<Audio> audios = new ArrayList<>();

        int i = 0;
        for (cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext() , i++) {
            String audioName = cursor.getString(cursor.getColumnIndex(AudioVisualizerContract.Audio.Column_Name));
            String audioDate = cursor.getString(cursor.getColumnIndex(AudioVisualizerContract.Audio.Column_Date));
            String path      = cursor.getString(cursor.getColumnIndex(AudioVisualizerContract.Audio.Column_Path));
            int    duration  = cursor.getInt(cursor.getColumnIndex(AudioVisualizerContract.Audio.Column_Duration));
            int    size      = cursor.getInt(cursor.getColumnIndex(AudioVisualizerContract.Audio.Column_Size));

            audios.add(new Audio(audioName , path , audioDate , duration , size));
        }

        return audios;
    }
}
