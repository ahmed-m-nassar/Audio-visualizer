package com.example.android.audio_visualizer.RecordMain;

import android.os.Environment;

import com.example.android.audio_visualizer.Models.Audio;
import com.example.android.audio_visualizer.RecordMain.Data.RecordLocalServices;
import com.example.android.audio_visualizer.RecordMain.Data.RecordLocalServicesImpl;
import com.example.android.audio_visualizer.Utils.FilesUtils;

import java.io.File;
import java.io.IOException;

public class RecordPresenter implements RecordContract.Presenter {

    private RecordContract.View mView;
    private RecordLocalServices mModel;
    private File                mDirectory;
    private File                mFullPath;
    private boolean             mIsPaused;

    public RecordPresenter(RecordContract.View view) {
        mView = view;
        mModel = new RecordLocalServicesImpl();
        mIsPaused = false;

        //preparing file path
        mDirectory = new File(Environment.getExternalStorageDirectory(), FilesUtils.FOLDER_NAME);
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
            mView.resumeRecorder();

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

            //dealing with views
            mView.startTimer();
            mView.enableTakePictureImage();
            mView.showMicOnPicture();
            mView.showPauseButton();
            mView.showCancelButton();
            mView.enableCancelButton();
            mView.enableStopButton();



            //preparing recorder
            try {
                mFullPath = File.createTempFile("sound", ".3gp", mDirectory);
                mView.prepareRecorder(mFullPath);
                mView.showRecordName(mFullPath.getName());
            } catch (IOException e) {
                return;
            }

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

        //stopping and deleting record
        mView.stopRecorder();
        mFullPath.delete();

        mIsPaused = false;

    }

    @Override
    public void pauseButtonClicked() {
        //dealing with views
        mView.pauseTimer();
        mView.showMicOffPicture();
        mView.showRecordButton();

        //pausing recorder
        mView.pauseRecorder();

        mIsPaused = true;
    }

    @Override
    public void stopButtonClicked() {

        //dealing with views
        mView.disableTakePictureImage();
        mView.pauseTimer();
        mView.resetTimer();
        mView.showMicOffPicture();
        mView.showRecordButton();
        mView.disableCancelButton();
        mView.disableStopButton();
        mView.hideRecordName();
        mView.showListButton();

        //saving audio
        mView.stopRecorder();

        //TODO get today`s date and the duration
        Audio audio = new Audio(mFullPath.getName() , mFullPath.getPath() , "1/1/1999" ,
                100,(int) mFullPath.length());

        //add the new audio to database
        mModel.addAudio(audio);

        mIsPaused = false;

    }

    @Override
    public void takePictureButtonClicked() {

    }

    @Override
    public void recordsListButtonClicked() {

    }
}
