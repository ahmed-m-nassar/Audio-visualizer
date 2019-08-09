package com.example.android.audio_visualizer.RecordMain;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.audio_visualizer.R;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecordActivity extends AppCompatActivity implements RecordContract.View {

    private RecordPresenter     mPresenter;
    private CircleImageView     mRecordImageButton;
    private CircleImageView     mPauseImageButton;
    private CircleImageView     mStopImageButton;
    private CircleImageView     mCancelImageButton;
    private CircleImageView     mRecordsListImageButton;
    private ImageButton         mTakePictureImageButton;
    private ImageView           mMicImage;
    private TextView            mRecordNameTextView;
    private Chronometer         mTimerChronometer;
    private MediaRecorder       mRecorder;


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

        //take picture image buttn clicked
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
                mPresenter.recordsListButtonClicked();
            }
        });
        //////////////////////////////////////////////////////////////////////////


        //initializing UI
        initializeUI();
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

    @Override
    public void startTimer() {

    }

    @Override
    public void pauseTimer() {

    }

    @Override
    public void resetTimer() {

    }

    @Override
    public void resumeTimer() {

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
    public void prepareRecorder(File audioFile) throws IOException {


        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(audioFile.getAbsolutePath());
        mRecorder.prepare();
        mRecorder.start();
    }

    @Override
    public void pauseRecorder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mRecorder.pause();
        }
    }

    @Override
    public void stopRecorder() {
        mRecorder.stop();
    }

    @Override
    public void resumeRecorder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mRecorder.resume();
        }
    }


    //region utility functions

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


    //endregion

}
