package com.example.android.audio_visualizer.record_main;

import com.example.android.audio_visualizer.models.Audio_Picture;
import com.example.android.audio_visualizer.models.Picture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public interface RecordContract {
    interface View {

        //dealing with views
        ////////////////////////////////////////
        void showPauseButton();
        void showRecordButton();
        void showCancelButton();
        void showListButton();

        void showMicOnPicture();
        void showMicOffPicture();


        void enableTakePictureImage();
        void disableTakePictureImage();

        void enableCancelButton();
        void disableCancelButton();

        void enableStopButton();
        void disableStopButton();

        int    getCurrentTimerTime();
        void   startTimer();
        void   pauseTimer();
        void   resetTimer();
        void   resumeTimer();

        void hideRecordName();
        void showRecordName(String name);
        /////////////////////////////////////////

        //dealing with permissions
        //////////////////////////////////////////
        boolean checkRecordPermission();
        boolean checkWriteExternalStoragePermission();
        boolean checkReadExternalStoragePermission();


        void requestRecordPermission();
        void requestWriteExternalStoragePermission();
        void requestReadExternalStoragePermission();
        ///////////////////////////////////////////////

        //camera intent
        void startCameraIntent();


        //dealing with service
        ///////////////////////////////////////////////////////////////////
        /**
         * starts the RecordMainService and bind to it
         */
        void startService();

        /**
         * stops the RecordMainService
         */
        void stopService();

        /**
         * unbinds from RecordMainService
         */
        void unbindService();

        /**
         * calls the start recording function in RecordMainService
         * @param file the file in which the record will be saved
         */
        void startRecordingInService(File file);

        /**
         * calls the pause recording function in RecordMainService
         */
        void pauseRecordingInService();

        /**
         * calls the stop recording function in RecordMainService
         */
        void stopRecordingInService();

        /**
         * calls the cancel recording function in RecordMainService
         */
        void cancelRecordingInService();
        ///////////////////////////////////////////////////////////////////////////////



    }

    interface Presenter {

        /**
         * adjusts the UI and starts a recording service
         * @throws IOException
         */
         void recordButtonClicked() throws IOException;

        /**
         * Adjusts the UI , cancels the record in service , stops and unbinds the service from the view
         */
         void cancelButtonClicked();

        /**
         * adjusts te UI ,pauses the record in service
         */
        void pauseButtonClicked();

        /**
         * adjusts the UI ,stops the record in service ,stops ,unbinds the service from the view
         * adds the record and the saved pictures and snap times in to the database
         */
        void stopButtonClicked();

        /**
         * calls the camera intent function in the view
         */
        void takePictureButtonClicked();

        /**
         * saves the picture and its snap time in array lists to save them in the database if the
         * record was saved successfully
         * @param path the full path of the picture
         */
        void savePicture(String path);

        /**
         * used to check if the user deleted any audio files or pictures outside the application
         * and updating the database to the changes happened
         */
        void checkFiles();

         //setters and getters
        ///////////////////////////////////////////////////////////////
         ArrayList<Picture> getPicturesTaken();
         void setPicturesTaken(ArrayList<Picture> pictures);

         ArrayList<String> getSnapTimes();
         void setSnapTimes(ArrayList<String> snapTimes);

         void setIsPaused(boolean isPaused);

         void setOriginalFile(String path);

         File getDirectory();
        ////////////////////////////////////////////////////////////////


    }
}
