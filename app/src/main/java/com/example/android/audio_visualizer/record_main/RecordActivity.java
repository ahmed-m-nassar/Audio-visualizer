package com.example.android.audio_visualizer.record_main;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.audio_visualizer.audios_list.AudiosListActivity;
import com.example.android.audio_visualizer.R;
import com.example.android.audio_visualizer.models.Picture;
import com.example.android.audio_visualizer.record_main.service.RecordMainService;
import com.example.android.audio_visualizer.record_main.shared_preferences.RecordSharedPreferences;
import com.example.android.audio_visualizer.utils.FilesUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecordActivity extends AppCompatActivity implements RecordContract.View {
    private static final String TAG = "RecordActivity";
    
    //presenter
    private RecordPresenter     mPresenter;

    //views
    private CircleImageView     mRecordImageButton;
    private CircleImageView     mPauseImageButton;
    private CircleImageView     mStopImageButton;
    private CircleImageView     mCancelImageButton;
    private CircleImageView     mRecordsListImageButton;
    private ImageButton         mTakePictureImageButton;
    private ImageView           mMicImage;
    private TextView            mRecordNameTextView;
    private Chronometer         mTimerChronometer;


    //chronometer variables
    private long    mChronometerPauseOffset;
    private boolean mChronometerRunning;

    //requests
    static final int REQUEST_TAKE_PHOTO = 1;

    //service variables
    RecordMainService mService;
    boolean mServiceBound = false;

    boolean mRecordingRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing variables
        //////////////////////////////////////////////////////////////////////////////

        //presenter
        mPresenter = new RecordPresenter(this);

        //views
        mRecordImageButton = (CircleImageView)findViewById(R.id.Record_StartRecord_ImageButton);
        mPauseImageButton = (CircleImageView)findViewById(R.id.Record_PauseRecord_ImageButton);
        mStopImageButton = (CircleImageView)findViewById(R.id.Record_Stop_ImageButton);
        mCancelImageButton = (CircleImageView)findViewById(R.id.Record_Cancel_ImageButton);
        mRecordsListImageButton = (CircleImageView)findViewById(R.id.Record_RecordsList_ImageButton);
        mTakePictureImageButton = (ImageButton)findViewById(R.id.Record_AddPicture_ImageButton);
        mMicImage = (ImageView)findViewById(R.id.Record_Mic_ImageView);
        mRecordNameTextView = (TextView) findViewById(R.id.Record_RecordName_TextView);
        mTimerChronometer = (Chronometer) findViewById(R.id.Record_Timer_Chronometer);

        ///////////////////////////////////////////////////////////////////////////////

        //setting click listeners
        ///////////////////////////////////////////////////////////////////////

        //record image button click listener
        mRecordImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mPresenter.recordButtonClicked();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //pause image button click listener
        mPauseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.pauseButtonClicked();
            }
        });

        //stop image button click listener
        mStopImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.stopButtonClicked();
            }
        });

        //cancel image button clicked
        mCancelImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.cancelButtonClicked();
            }
        });

        //take picture image button clicked
        mTakePictureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.takePictureButtonClicked();
            }
        });

        //Records list Image clicked
        mRecordsListImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext() , AudiosListActivity.class);
                startActivity(intent);
            }
        });
        //////////////////////////////////////////////////////////////////////////

        //initializing Ui
        initializeUI();

        //load shared preferences
        loadSharedPreferences();

        //checking if the user deleted audio or picture picture file outside application
        mPresenter.checkFiles();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveSharedPreferences();
    }


    @Override
    public void showPauseButton() {
        mPauseImageButton.setVisibility(View.VISIBLE);
        mRecordImageButton.setVisibility(View.GONE);
    }

    @Override
    public void showRecordButton() {
        mPauseImageButton.setVisibility(View.GONE);
        mRecordImageButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCancelButton() {
        mCancelImageButton.setVisibility(View.VISIBLE);
        mRecordsListImageButton.setVisibility(View.GONE);

    }

    @Override
    public void showListButton() {
        mCancelImageButton.setVisibility(View.GONE);
        mRecordsListImageButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void enableTakePictureImage() {
        mTakePictureImageButton.setEnabled(true);
        mTakePictureImageButton.setImageResource(R.drawable.ic_add_a_photo);
    }

    @Override
    public void disableTakePictureImage() {
        mTakePictureImageButton.setEnabled(false);
        mTakePictureImageButton.setImageResource(R.drawable.ic_add_a_photo_disable);
    }

    @Override
    public void enableCancelButton() {
        mCancelImageButton.setEnabled(true);
        mCancelImageButton.setImageResource(R.drawable.ic_cancel);
    }

    @Override
    public void disableCancelButton() {
        mCancelImageButton.setEnabled(false);
        mCancelImageButton.setImageResource(R.drawable.ic_cancel_disable);
    }

    @Override
    public void enableStopButton() {
        mStopImageButton.setEnabled(true);
        mStopImageButton.setImageResource(R.drawable.ic_stop);
    }

    @Override
    public void disableStopButton() {
        mStopImageButton.setEnabled(false);
        mStopImageButton.setImageResource(R.drawable.ic_stop_disabled);
    }

    //todo implement
    @Override
    public int getCurrentTimerTime() {
        if(mChronometerRunning)
           return (int)(SystemClock.elapsedRealtime() - mTimerChronometer.getBase());
        else
            return (int)mChronometerPauseOffset;
    }

    @Override
    public void startTimer() {
        if (!mChronometerRunning) {
            mTimerChronometer.setBase(SystemClock.elapsedRealtime() - mChronometerPauseOffset);
            mTimerChronometer.start();
            mChronometerRunning = true;
        }
    }

    @Override
    public void pauseTimer() {
        if (mChronometerRunning) {
            mTimerChronometer.stop();
            mChronometerPauseOffset = SystemClock.elapsedRealtime() - mTimerChronometer.getBase();
            mChronometerRunning = false;
        }
    }

    @Override
    public void resetTimer() {
        pauseTimer();
        mTimerChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometerPauseOffset = 0;
    }

    @Override
    public void resumeTimer() {
        startTimer();
    }


    @Override
    public void showMicOnPicture() {
        mMicImage.setImageResource(R.drawable.ic_mic_on);
    }

    @Override
    public void showMicOffPicture() {
        mMicImage.setImageResource(R.drawable.ic_mic_off);
    }

    @Override
    public void hideRecordName() {
        mRecordNameTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showRecordName(String name) {
        mRecordNameTextView.setText(name);
        mRecordNameTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean checkRecordPermission() {
        int result = ContextCompat.checkSelfPermission(this , Manifest.permission.RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean checkWriteExternalStoragePermission() {
        int result = ContextCompat.checkSelfPermission(this , Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean checkReadExternalStoragePermission() {
        int result = ContextCompat.checkSelfPermission(this , Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestRecordPermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{Manifest.permission.RECORD_AUDIO},1000);
    }

    @Override
    public void requestWriteExternalStoragePermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
    }


    @Override
    public void requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1000);
    }


    @Override
    public void startCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

         if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //mPresenter.savePicture(data.getData().getPath());
             Bitmap photo = (Bitmap) data.getExtras().get("data");

             // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
             Uri tempUri = getImageUri(getApplicationContext(), photo);

             // CALL THIS METHOD TO GET THE ACTUAL PATH
             File finalFile = new File(getRealPathFromURI(tempUri));

             //saving the picture in the presenter
             mPresenter.savePicture(finalFile.getAbsolutePath());
         }
    }

    @Override
    public void startService() {
        Intent intent = new Intent(this, RecordMainService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void startRecordingInService(File file) {
        mService.startRecording(file);
    }

    @Override
    public void stopService() {
        mService.stopForeground(true);
        stopService(new Intent(this,RecordMainService.class));
        mRecordingRunning = false;
    }

    @Override
    public void unbindService() {
        this.unbindService(mServiceConnection);
        mServiceBound = false;
    }

    @Override
    public void pauseRecordingInService() {
        mService.pauseRecording();
    }

    @Override
    public void stopRecordingInService() {
        mService.stopRecording();
    }

    @Override
    public void cancelRecordingInService() {
        mService.cancelRecording();
    }



    //region utility functions

    private void saveSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(RecordSharedPreferences.RECORDING_SHARED_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(mServiceBound) { //if the service is bound we should save the state of the record

            //saving service Bound state , whether the recording is paused or not , recording path,
            //whether the chronometer is runnung or not and chronometer pause offset
            /////////////////////////////////////////////////////////////////////////////////////
            editor.putBoolean(RecordSharedPreferences.SERVICE_BOUND, true);
            editor.putBoolean(RecordSharedPreferences.IS_PAUSED, mService.isPaused());
            editor.putString(RecordSharedPreferences.RECORD_PATH , mService.filePath());
            editor.putLong(RecordSharedPreferences.CHRONOMETER_BASE_OFFSET , mTimerChronometer.getBase());
            editor.putLong(RecordSharedPreferences.CHRONOMETER_PAUSE_OFFSET , mChronometerPauseOffset);
            ///////////////////////////////////////////////////////////////////////////////////

            //saving pictures taken
            //////////////////////////////////////////////////////////////////////
            Gson picturesGson = new Gson();
            String picturesJson = picturesGson.toJson(mPresenter.getPicturesTaken());
            editor.putString(RecordSharedPreferences.PICTURES_TAKEN, picturesJson);
            /////////////////////////////////////////////////////////////////

            //saving snap times
            ///////////////////////////////////////////////////////////////////////
            Gson snapsGson = new Gson();
            String snapsJson = snapsGson.toJson(mPresenter.getSnapTimes());
            editor.putString(RecordSharedPreferences.SNAP_TIMES, snapsJson);
            ///////////////////////////////////////////////////////////////////////

        } else { //if the service wasnt bound we should save the service bound state only
            editor.putBoolean(RecordSharedPreferences.SERVICE_BOUND, false);
        }
        editor.apply();
    }


    private void loadSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(RecordSharedPreferences.RECORDING_SHARED_PREFERENCE
                , MODE_PRIVATE);
        mServiceBound = sharedPreferences.getBoolean(RecordSharedPreferences.SERVICE_BOUND, false);
       // mServiceBound = false;


        if(mServiceBound) {//if a service was bound we should load the other variables
            //if a service was bound to the activity it means that the service was started and we need to rebind to it
            Intent intent = new Intent(this, RecordMainService.class);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

            mRecordingRunning = true;

            //loading audio path, whether the recording is paused or not , whether the chronometer is running or not
            // and chronometer pause offset
            ///////////////////////////////////////////////////////////////////////////////////////////////////
            boolean isPaused = sharedPreferences.getBoolean(RecordSharedPreferences.IS_PAUSED , false);
            String audioPath = sharedPreferences.getString(RecordSharedPreferences.RECORD_PATH , "");
            String audioName = FilesUtils.getFileNameFromPath(audioPath);
            long chronometerBaseOffset = sharedPreferences.getLong(RecordSharedPreferences.CHRONOMETER_BASE_OFFSET ,0);
            mChronometerPauseOffset = sharedPreferences.getLong(RecordSharedPreferences.CHRONOMETER_PAUSE_OFFSET , 0);
            ////////////////////////////////////////////////////////////////////////////////////////////////////


            //loading pictures taken arraylist
            /////////////////////////////////////////////////////////////////////////
            Gson picturesGson = new Gson();
            String json = sharedPreferences.getString(RecordSharedPreferences.PICTURES_TAKEN, null);
            Type type = new TypeToken<ArrayList<Picture>>() {}.getType();
            ArrayList<Picture> picturesTaken = picturesGson.fromJson(json, type);
            if (picturesTaken == null) {
                picturesTaken = new ArrayList<>();
            }
            ///////////////////////////////////////////////////////////////////

            //loading snap times ArrayList
            ///////////////////////////////////////////////////////////////////////////
            Gson snapsGson = new Gson();
            json = sharedPreferences.getString(RecordSharedPreferences.SNAP_TIMES , null);
            type = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<String> snapTimes = snapsGson.fromJson(json, type);
            if (snapTimes == null) {
                snapTimes = new ArrayList<>();
            }
            /////////////////////////////////////////////////////////////////////////

            //showing appropriate views
            ///////////////////////////////////////////////////
            showRecordName(audioName);
            showCancelButton();
            enableCancelButton();
            enableStopButton();
            enableTakePictureImage();
            if(isPaused) {
                showRecordButton();
            } else {
                showPauseButton();
            }

           //chronometer
            if(isPaused) {
                mTimerChronometer.setBase(SystemClock.elapsedRealtime() - mChronometerPauseOffset);
                mChronometerRunning = false;
            } else {
                mTimerChronometer.setBase(chronometerBaseOffset);
                mTimerChronometer.start();
                mChronometerRunning = true;
            }
            ///////////////////////////////////////////////

            //sending data to presenter
            /////////////////////////////////////////////////
            mPresenter.setPicturesTaken(picturesTaken);
            mPresenter.setSnapTimes(snapTimes);
            mPresenter.setIsPaused(isPaused);
            mPresenter.setOriginalFile(audioPath);
            /////////////////////////////////////////////////
        }

    }

    private void initializeUI() {
        resetTimer();
        hideRecordName();
        disableCancelButton();
        disableStopButton();
        showMicOffPicture();
        showRecordButton();
        disableTakePictureImage();
        showListButton();
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    //endregion

    //region service connection
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: Entered");
            RecordMainService.MyBinder myBinder = (RecordMainService.MyBinder) service;
            mService = myBinder.getService();
            mServiceBound = true;

            if (!mRecordingRunning) { //if there is no recording running we should start a new one
                try {
                    File file = File.createTempFile("sound", ".3gp", mPresenter.getDirectory());
                    mService.startRecording(file);
                    mPresenter.setOriginalFile(file.getPath());
                    showRecordName(file.getName());
                    Log.d(TAG, "onServiceConnected: startRecording");
                } catch (Exception e) {

                }
            }
        }

    };
    //endregion

}
