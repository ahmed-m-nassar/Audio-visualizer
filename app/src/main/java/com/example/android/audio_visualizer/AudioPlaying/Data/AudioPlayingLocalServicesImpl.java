package com.example.android.audio_visualizer.AudioPlaying.Data;

import android.database.Cursor;

import com.example.android.audio_visualizer.Base.DataBase.AudioVisualizerContract;
import com.example.android.audio_visualizer.Base.DataBase.DBHelper;
import com.example.android.audio_visualizer.Base.MyApp;
import com.example.android.audio_visualizer.Models.Audio;
import com.example.android.audio_visualizer.Models.Audio_Picture;

import java.util.ArrayList;

public class AudioPlayingLocalServicesImpl extends DBHelper implements  AudioPlayingLocalServices {
    public AudioPlayingLocalServicesImpl() {
        super(MyApp.getAppContext());
    }

    @Override
    public Audio getAudio(String audioPath) {
        return null;
    }

    @Override
    public ArrayList<Audio_Picture> getAudioPictures(String audioPath) {
        String query = "select  * from " + AudioVisualizerContract.Audio_Picture.Table_Name + " where " +
                AudioVisualizerContract.Audio_Picture.Column_AudioPath + " = '" + audioPath + "'" ;


        Cursor cursor = select(query ,null);

        ArrayList<Audio_Picture> audioPics = new ArrayList<>();

        int i = 0;
        for (cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext() , i++) {
            String picPath  = cursor.getString(cursor.getColumnIndex(AudioVisualizerContract.Audio_Picture.Column_PicturePath));
            String snapTime = cursor.getString(cursor.getColumnIndex(AudioVisualizerContract.Audio_Picture.Column_SnapTime));

            audioPics.add(new Audio_Picture(audioPath , picPath , snapTime ));
        }

        return audioPics;

    }
}
