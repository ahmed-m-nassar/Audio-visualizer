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
import com.example.android.audio_visualizer.record_main.RecordActivity;

import java.util.ArrayList;

import static com.example.android.audio_visualizer.base.notification_channels.RecordingNotificationChannel.CHANNEL_ID;

public class RecordMainService extends Service {
    static String LOG_TAG = "BoundService";
    final IBinder mBinder = new MyBinder();

    MediaRecorder recorder ;
    boolean mIsRecording = false;
    boolean mIsPaused = false;
    ArrayList<String> sourceFiles = new ArrayList<>();

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent (this , RecordActivity.class);
        notificationIntent.putExtra("IsRecording" , mIsRecording);
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

    public void startRecording(String path) {

        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(path);
            recorder.prepare();
            recorder.start();

            mIsRecording = true;
        } catch (Exception e) {
            Log.d("service", "startRecording: ");
        }


    }

    public void stopRecording () {
        //stopping recorder
        recorder.stop();
        recorder.release();
        mIsRecording = false;

        stopSelf();
    }

    public void pauseRecording() {

    }

    public boolean isRecording() {
        return mIsRecording;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        RecordMainService getService() {
            return RecordMainService.this;
        }
    }
}
