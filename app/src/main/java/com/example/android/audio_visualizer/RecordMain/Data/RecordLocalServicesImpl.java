package com.example.android.audio_visualizer.RecordMain.Data;

import android.content.ContentValues;

import com.example.android.audio_visualizer.Base.DataBase.AudioVisualizerContract;
import com.example.android.audio_visualizer.Base.DataBase.DBHelper;
import com.example.android.audio_visualizer.Base.MyApp;
import com.example.android.audio_visualizer.Models.Audio;
import com.example.android.audio_visualizer.Models.Audio_Picture;
import com.example.android.audio_visualizer.Models.Picture;

public class RecordLocalServicesImpl extends DBHelper implements RecordLocalServices {

    public RecordLocalServicesImpl() {
        super(MyApp.getAppContext());
    }

    @Override
    public boolean addAudio(Audio audio) {
        ContentValues content = new ContentValues();
        content.put(AudioVisualizerContract.Audio.Column_Name     , audio.getmName());
        content.put(AudioVisualizerContract.Audio.Column_Date     , audio.getmDate());
        content.put(AudioVisualizerContract.Audio.Column_Path     , audio.getmPath());
        content.put(AudioVisualizerContract.Audio.Column_Duration , audio.getmDuration());
        content.put(AudioVisualizerContract.Audio.Column_Size     , audio.getmSize());

        return insert(AudioVisualizerContract.Audio.Table_Name,content);
    }

    @Override
    public boolean addPicture(Picture picture) {
        ContentValues content = new ContentValues();
        content.put(AudioVisualizerContract.Picture.Column_Name     , picture.getmName());
        content.put(AudioVisualizerContract.Picture.Column_Date     , picture.getmDate());
        content.put(AudioVisualizerContract.Picture.Column_Path     , picture.getmPath());

       return insert(AudioVisualizerContract.Picture.Table_Name,content);
    }

    @Override
    public boolean addAudioPicture(Audio_Picture audioPicture) {
        ContentValues content = new ContentValues();
        content.put(AudioVisualizerContract.Audio_Picture.Column_AudioName  , audioPicture.getmAudioName());
        content.put(AudioVisualizerContract.Audio_Picture.Column_PictureName, audioPicture.getmPictureName());
        content.put(AudioVisualizerContract.Audio_Picture.Column_SnapTime   , audioPicture.getmSnapTime());

        return insert(AudioVisualizerContract.Picture.Table_Name,content);
    }
}
