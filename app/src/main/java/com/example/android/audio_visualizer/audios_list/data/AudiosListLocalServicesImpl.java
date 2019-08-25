package com.example.android.audio_visualizer.audios_list.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.android.audio_visualizer.base.data_base.AudioVisualizerContract;
import com.example.android.audio_visualizer.base.data_base.DBHelper;
import com.example.android.audio_visualizer.base.MyApp;
import com.example.android.audio_visualizer.models.Audio;


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
            String duration  = cursor.getString(cursor.getColumnIndex(AudioVisualizerContract.Audio.Column_Duration));
            int    size      = cursor.getInt(cursor.getColumnIndex(AudioVisualizerContract.Audio.Column_Size));

            audios.add(new Audio(audioName , path , audioDate , duration , size));
        }

        return audios;
    }

    @Override
    public void deleteAudio(String path) {
        //deleting from audio table
        String audioTableWhereClause = AudioVisualizerContract.Audio.Column_Path + " =?";
        String[] audioTableArguments = {path};
        deleteWithoutCascade(AudioVisualizerContract.Audio.Table_Name,audioTableArguments,audioTableWhereClause);

        //deleting the connection between the audio and pictures
        String audioPictureTableWhereClause = AudioVisualizerContract.Audio_Picture.Column_AudioPath + " =?";
        String[] audioPictureTableArguments = {path};
        deleteWithoutCascade(AudioVisualizerContract.Audio_Picture.Table_Name,audioPictureTableArguments
                            ,audioPictureTableWhereClause);

    }

    @Override
    public void updateAudio(String path, String name) {

        //updating in Audio Table
        ContentValues contentValues = new ContentValues();
        contentValues.put(AudioVisualizerContract.Audio.Column_Name,name);
        String audioTableWhereClause = AudioVisualizerContract.Audio.Column_Path + " =?";
        String[] audioTableArguments = {path};
        update(AudioVisualizerContract.Audio.Table_Name,contentValues,audioTableArguments,audioTableWhereClause);

    }
}
