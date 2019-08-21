package com.example.android.audio_visualizer.RecordMain;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.audio_visualizer.AudiosList.AudiosListActivity;
import com.example.android.audio_visualizer.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecordActivity extends AppCompatActivity implements RecordContract.View {

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
                Intent intent = new Intent(getBaseContext() , AudiosListActivity.class);
                startActivity(intent);
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

    //todo implement
    @Override
    public String getCurrentTimerTime() {
        return "100";
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

             mPresenter.savePicture(finalFile.getAbsolutePath());
         }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

}
