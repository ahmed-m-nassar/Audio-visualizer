package com.example.android.audio_visualizer.record_main;

import java.io.IOException;

public interface RecordContract {
    interface View {
        public void showPauseButton();
        public void showRecordButton();
        public void showCancelButton();
        public void showListButton();


        public void showMicOnPicture();
        public void showMicOffPicture();

        public void enableTakePictureImage();
        public void disableTakePictureImage();

        public void enableCancelButton();
        public void disableCancelButton();

        public void enableStopButton();
        public void disableStopButton();

        public int    getCurrentTimerTime();
        public void   startTimer();
        public void   pauseTimer();
        public void   resetTimer();
        public void   resumeTimer();

        public void hideRecordName();
        public void showRecordName(String name);

        public boolean checkRecordPermission();
        public boolean checkWriteExternalStoragePermission();
        public boolean checkReadExternalStoragePermission();

        public void requestRecordPermission();
        public void requestWriteExternalStoragePermission();
        public void requestReadExternalStoragePermission();

        public void startCameraIntent();

        void showMessage(String message);
    }

    interface Presenter {
        public void recordButtonClicked() throws IOException;
        public void cancelButtonClicked();
        public void pauseButtonClicked();
        public void stopButtonClicked();
        public void takePictureButtonClicked();
        public void savePicture(String path);

        public void checkFiles();

    }
}
