package com.example.android.audio_visualizer.record_main.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.android.audio_visualizer.base.data_base.AudioVisualizerContract;
import com.example.android.audio_visualizer.base.data_base.DBHelper;
import com.example.android.audio_visualizer.base.MyApp;
import com.example.android.audio_visualizer.models.Audio;
import com.example.android.audio_visualizer.models.Audio_Picture;
import com.example.android.audio_visualizer.models.Picture;

import java.io.File;

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
        content.put(AudioVisualizerContract.Audio_Picture.Column_AudioPath  , audioPicture.getmAudioPath());
        content.put(AudioVisualizerContract.Audio_Picture.Column_PicturePath, audioPicture.getmPicturePath());
        content.put(AudioVisualizerContract.Audio_Picture.Column_SnapTime, audioPicture.getmSnapTime());
        return insert(AudioVisualizerContract.Audio_Picture.Table_Name,content);
    }

    @Override
    public void checkAudioFiles() {
        String query = "Select " + AudioVisualizerContract.Audio.Column_Path +
                " From " + AudioVisualizerContract.Audio.Table_Name;
        Cursor cursor = select(query ,null);

        int i = 0;
        for (cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext() , i++) {
            String path  = cursor.getString(cursor.getColumnIndex(AudioVisualizerContract.Audio.Column_Path));

            //check if the file is deleted
            File file = new File(path);
            if(!file.exists()) {
                deleteAudio(path);
                deleteAudioPictureUsingAudioPath(path);
            }
        }
    }

    @Override
    public void checkPictureFiles() {
        String query = "Select " + AudioVisualizerContract.Picture.Column_Path +
                " From " + AudioVisualizerContract.Picture.Table_Name;
        Cursor cursor = select(query ,null);

        int i = 0;
        for (cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext() , i++) {
            String path  = cursor.getString(cursor.getColumnIndex(AudioVisualizerContract.Picture.Column_Path));

            //check if the file is deleted
            File file = new File(path);
            if(!file.exists()) {
                deletePicture(path);
                deleteAudioPictureUsingPicturePath(path);
            }
        }
    }

    private void deleteAudio(String path) {
        String whereClause = AudioVisualizerContract.Audio.Column_Path + " =?";
        String[] args = {path};
        deleteWithoutCascade(AudioVisualizerContract.Audio.Table_Name,args , whereClause);



    }

    private void deletePicture(String path) {
        String whereClause = AudioVisualizerContract.Picture.Column_Path + " =?";
        String[] args = {path};
        deleteWithoutCascade(AudioVisualizerContract.Picture.Table_Name,args , whereClause);
    }

    private void deleteAudioPictureUsingAudioPath(String audioPath) {
        String whereClause = AudioVisualizerContract.Audio_Picture.Column_AudioPath + " =?";
        String[] args = {audioPath};
        deleteWithoutCascade(AudioVisualizerContract.Audio_Picture.Table_Name,args , whereClause);
    }

    private void deleteAudioPictureUsingPicturePath(String picPath) {
        String whereClause =  AudioVisualizerContract.Audio_Picture.Column_PicturePath + " =?";
        String[] args = {picPath};
        deleteWithoutCascade(AudioVisualizerContract.Audio_Picture.Table_Name ,args , whereClause);
    }
}
