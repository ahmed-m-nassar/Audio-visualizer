package com.example.android.audio_visualizer.record_main.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
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

import omrecorder.AudioChunk;
import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;

import static com.example.android.audio_visualizer.base.notification_channels.RecordingNotificationChannel.CHANNEL_ID;

public class RecordMainService extends Service {
    static String TAG = "BoundService";
    final IBinder mBinder = new MyBinder();

    public static boolean isServiceRunning = false;

    private ArrayList<String>   mAudioFiles;  //all audio files paths recorded to be merged when user stops the record
    private Recorder            mRecorder;
    private boolean             mIsPaused; //checks if the audio is paused or not


    @Override
    public void onCreate() {
        super.onCreate();
        isServiceRunning = true;
        mAudioFiles = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        isServiceRunning = false;
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
            mAudioFiles.add(file.getPath());
            mRecorder = OmRecorder.wav(
                    new PullTransport.Default(new PullableSource.AutomaticGainControl(
                            new PullableSource.Default(
                                    new AudioRecordConfig.Default(
                                            MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                                            AudioFormat.CHANNEL_IN_MONO, 32000
                                    )
                            )
                    ), new PullTransport.OnAudioChunkPulledListener() {
                        @Override public void onAudioChunkPulled(AudioChunk audioChunk) {
                        }
                    }), file);

            mRecorder.startRecording();

            mIsPaused = false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void stopRecording () {
        try{
            mRecorder.stopRecording();
        } catch (Exception e) {
            //todo implement
        }

        for(int i = 1 ; i < mAudioFiles.size() ; i++) {
            FilesUtils.CombineWaveFile(mAudioFiles.get(0) , mAudioFiles.get(i));
        }

        //saving audio state
        mIsPaused = false;
        mAudioFiles.clear();
    }

    public void pauseRecording() {
        //stopping recorder to merge it when user stops the record
        try {
            mRecorder.stopRecording();
        } catch (Exception e) {

        }

        //saving audio state
        mIsPaused = true;
    }

    public void cancelRecording() {
        //stopping  record
        try {
            mRecorder.stopRecording();
        } catch (Exception e) {
            //todo implement
        }

        //deleting record
        for (int i = 0 ; i < mAudioFiles.size() ; i++) {
            File file = new File(mAudioFiles.get(i));
            if(file.exists()) {
                file.delete();
            }
        }

        //saving audio state
        mIsPaused = false;
        mAudioFiles.clear();
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
