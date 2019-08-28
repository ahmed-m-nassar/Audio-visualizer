package com.example.android.audio_visualizer.record_main;

import android.media.MediaRecorder;


import com.example.android.audio_visualizer.models.Audio;
import com.example.android.audio_visualizer.models.Audio_Picture;
import com.example.android.audio_visualizer.models.Picture;
import com.example.android.audio_visualizer.record_main.data.RecordLocalServices;
import com.example.android.audio_visualizer.record_main.data.RecordLocalServicesImpl;
import com.example.android.audio_visualizer.utils.DateTimeUtils;
import com.example.android.audio_visualizer.utils.FilesUtils;


import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RecordPresenter implements RecordContract.Presenter {

    private RecordContract.View mView;
    private RecordLocalServices mModel;
    private File                mDirectory; //audio file directory
    private File                mOriginalAudioFile; //first audio file created (used to merge all recorded files into it)
    private boolean             mIsPaused; //checks if the audio is paused or not

    private ArrayList<Picture> mPicturesTaken; // all pictures taken paths to be saved in the database when user stops the record
    private ArrayList<String>   mPicsSnapTimes; //pictures snap time

    public RecordPresenter(RecordContract.View view) {
        //initializing variables
        mView = view;
        mModel = new RecordLocalServicesImpl();
        mIsPaused = false;
        mPicturesTaken = new ArrayList<>();
        mPicsSnapTimes = new ArrayList<>();


        //preparing file directory
        mDirectory = new File(FilesUtils.AUDIO_STORAGE_PATH, "");
        if (!mDirectory.exists()) {
            mDirectory.mkdirs();
        }

    }



    @Override
    public void recordButtonClicked() throws IOException {
        //if the record was already paused
        if(mIsPaused) {
            //dealing with views
            mView.showPauseButton();
            mView.resumeTimer();
            mView.showMicOnPicture();

            //resuming recorder
            //creating a new temp file to merge later
            try {
                File newTempFile = File.createTempFile("sound", ".3gp", mDirectory);
                mView.startRecordingInService(newTempFile); //creating a new media file to merge it later
            } catch (IOException e) {
                return;
            }

            mIsPaused=false;

        } else { // if we are starting a new record

            boolean permissionMissing = false;
            //check for permissions
            //////////////////////////////////////////////////////////////////
            if(!mView.checkRecordPermission()) {
                permissionMissing = true;
                mView.requestRecordPermission();
            }
            if(!mView.checkReadExternalStoragePermission()) {
                permissionMissing = true;
                mView.requestReadExternalStoragePermission();
            }
            if(!mView.checkWriteExternalStoragePermission()) {
                permissionMissing = true;
                mView.requestWriteExternalStoragePermission();
            }

            //if any permission is missing we shouldn`t record
            if(permissionMissing) {
                return;
            }
            /////////////////////////////////////////////////////////////////////

            //starting service and starting media recorder
            mView.startService();

            //dealing with views
            mView.startTimer();
            mView.enableTakePictureImage();
            mView.showMicOnPicture();
            mView.showPauseButton();
            mView.showCancelButton();
            mView.enableCancelButton();
            mView.enableStopButton();

            //clearing picturesTaken , snapTimes  arrays at the beggining of a new recording
            mPicturesTaken.clear();
            mPicsSnapTimes.clear();

            mIsPaused = false;
        }

    }

    @Override
    public void cancelButtonClicked() {

        //dealing with views
        mView.disableTakePictureImage();
        mView.pauseTimer();
        mView.resetTimer();
        mView.disableCancelButton();
        mView.disableStopButton();
        mView.showMicOffPicture();
        mView.showRecordButton();
        mView.hideRecordName();
        mView.showListButton();

        //cancelling recording in the service , stopping recording service as its not needed any more
        mView.cancelRecordingInService();
        mView.stopService();
        mView.unbindService();

        mIsPaused = false;

    }


    @Override
    public void pauseButtonClicked() {
        //dealing with views
        mView.pauseTimer();
        mView.showMicOffPicture();
        mView.showRecordButton();

        //pausing recorder in service
        mView.pauseRecordingInService();

        mIsPaused = true;
    }

    @Override
        public void stopButtonClicked() {

        //dealing with views
        mView.disableTakePictureImage();
        mView.pauseTimer();
        mView.showMicOffPicture();
        mView.showRecordButton();
        mView.disableCancelButton();
        mView.disableStopButton();
        mView.hideRecordName();
        mView.showListButton();

        //stopping the recording from service
        mView.stopRecordingInService();
        mView.stopService();
        mView.unbindService();


        //getting record duration
        String recordDuration =DateTimeUtils.createTimeLabel(mView.getCurrentTimerTime());

        //add the new audio to database
        Audio audio = new Audio(mOriginalAudioFile.getName() , mOriginalAudioFile.getPath()
                , DateTimeUtils.getCurrentDate() , recordDuration,(int) mOriginalAudioFile.length());
        mModel.addAudio(audio); //adding the new audio in data base


        //add the pictures in the database
        for(int i = 0 ; i < mPicturesTaken.size() ; i++) {
            //adding pictures in Picture Table
            mModel.addPicture(mPicturesTaken.get(i));

            //adding pictures and audio in Audio_Picture table
            Audio_Picture audio_picture = new Audio_Picture(mOriginalAudioFile.getPath()
                    ,mPicturesTaken.get(i).getmPath() , mPicsSnapTimes.get(i));
            mModel.addAudioPicture(audio_picture);
        }

        mView.resetTimer();
        mIsPaused = false;
    }

    @Override
    public void takePictureButtonClicked() {
        mView.startCameraIntent();
    }

    @Override
    public void savePicture(String fullPicPath) {
        String date = DateTimeUtils.getCurrentDate();
        String picName = FilesUtils.getFileNameFromPath(fullPicPath);

        //saving the picture taken and its snap time
        mPicturesTaken.add(new Picture(picName , fullPicPath ,date ));
        mPicsSnapTimes.add(DateTimeUtils.createTimeLabel(mView.getCurrentTimerTime()));
    }

    @Override
    public void checkFiles() {
        mModel.checkAudioFiles();
        mModel.checkPictureFiles();
    }

    //region setters and getters
    @Override
    public ArrayList<Picture> getPicturesTaken() {
        return mPicturesTaken;
    }

    @Override
    public void setPicturesTaken(ArrayList<Picture> pictures) {
        mPicturesTaken = pictures;
    }

    @Override
    public ArrayList<String> getSnapTimes() {
        return mPicsSnapTimes;
    }

    @Override
    public void setSnapTimes(ArrayList<String> snapTimes) {
        mPicsSnapTimes = snapTimes;
    }

    @Override
    public void setIsPaused(boolean isPaused) {
        mIsPaused = isPaused;
    }

    @Override
    public void setOriginalFile(String path) {
        mOriginalAudioFile = new File(path);
    }

    @Override
    public File getDirectory() {
        return mDirectory;
    }

    //endregion



}


