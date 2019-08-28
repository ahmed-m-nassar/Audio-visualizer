package com.example.android.audio_visualizer.record_main.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.android.audio_visualizer.R;
import com.example.android.audio_visualizer.models.Audio;
import com.example.android.audio_visualizer.models.Audio_Picture;
import com.example.android.audio_visualizer.models.Picture;
import com.example.android.audio_visualizer.record_main.RecordActivity;
import com.example.android.audio_visualizer.utils.DateTimeUtils;
import com.example.android.audio_visualizer.utils.FilesUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.android.audio_visualizer.base.notification_channels.RecordingNotificationChannel.CHANNEL_ID;

public class RecordMainService extends Service {
    static String LOG_TAG = "BoundService";
    final IBinder mBinder = new MyBinder();


    ArrayList<String> sourceFiles = new ArrayList<>();

    private ArrayList<String>   mAudioFiles;  //all audio files paths recorded to be merged when user stops the record

    private MediaRecorder       mRecorder;
    private boolean             mIsPaused; //checks if the audio is paused or not

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAudioFiles = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent (this , RecordActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this , CHANNEL_ID)
                .setContentTitle("Example service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_background).setContentIntent(pendingIntent)
                .build();

        startForeground(1 , notification);

        return START_NOT_STICKY;
    }

    public boolean startRecording(File file) {

        try {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(file.getPath());
            mRecorder.prepare();
            mRecorder.start();

            //saving the file path to merge it later with the rest of Audio files
            mAudioFiles.add(file.getPath());

            mIsPaused = false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void stopRecording () {
        //saving audio
        if(!mIsPaused) {
            mRecorder.stop();
        }

        //merging audio files
        String[] sourceFiles = new String[mAudioFiles.size()];
        sourceFiles = mAudioFiles.toArray(sourceFiles);
        FilesUtils.mergeMediaFiles(true , sourceFiles , sourceFiles[0]);

        //deleting unneeded files
        for(int i = 1 ; i < mAudioFiles.size() ; i++) {
            File audioFile = new File (mAudioFiles.get(i));
            if(audioFile.exists()) {
                audioFile.delete();
            }
        }
        //clearing audio files
        mAudioFiles.clear();

        //saving audio state
        mIsPaused = false;


    }

    public void pauseRecording() {
        //stopping recorder to merge it when user stops the record
        mRecorder.stop();

        //saving audio state
        mIsPaused = true;
    }

    public void cancelRecording() {
        //stopping and deleting record
        if(!mIsPaused) //if the record is On -> stop it
            mRecorder.stop();

        for(int i = 0 ; i < mAudioFiles.size() ; i++) { //deleting all recorded files
            File audioFile = new File (mAudioFiles.get(i));
            if(audioFile.exists()) {
                audioFile.delete();
            }
        }
        mAudioFiles.clear();

        //saving audio state
        mIsPaused = false;
    }
    public boolean isPaused() {
        return mIsPaused;
    }

    public String filePath() {
        return mAudioFiles.get(0);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        public RecordMainService getService() {
            return RecordMainService.this;
        }
    }
}
