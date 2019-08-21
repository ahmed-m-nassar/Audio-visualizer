package com.example.android.audio_visualizer.AudioPlaying;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.audio_visualizer.AudioPlaying.Adapter.PicturesListAdapter;
import com.example.android.audio_visualizer.AudioPlaying.Adapter.PicuresListClickListeners;
import com.example.android.audio_visualizer.Models.Audio;
import com.example.android.audio_visualizer.Models.Audio_Picture;
import com.example.android.audio_visualizer.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AudioPlayingActivity extends AppCompatActivity implements AudioPlayingContract.View , PicuresListClickListeners {

    private static final String TAG = "AudioPlayingActivity";

    private AudioPlayingPresenter mPresenter;

    private ImageView             mPictureChoosen;
    private TextView              mAudioName;
    private TextView              mAudioCurrentTime;
    private TextView              mAudioFullTime;
    private SeekBar               mSeekBar;
    private CircleImageView       mPauseButton;
    private CircleImageView       mPlayButton;
    private CircleImageView       mVolumeButton;
    private CircleImageView       mStopButton;
    private RecyclerView          mPicturesList;

    private String                mAudioPath;

    private MediaPlayer           mMediaPlayer;

    private Thread                mAudioThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_playing);

        //initializing variables
        ///////////////////////////////////////////////////////////////////////////////////////

        //presenter
        mPresenter = new AudioPlayingPresenter(this);

        //views
        mPictureChoosen = (ImageView)findViewById(R.id.AudioPlaying_Image_ImageView);
        mAudioName = (TextView)findViewById(R.id.AudioPlaying_RecordName_TextView);
        mAudioCurrentTime = (TextView)findViewById(R.id.AudioPlaying_CurrentTime_TextView);
        mAudioFullTime = (TextView)findViewById(R.id.AudioPlaying_FullTime_TextView);
        mSeekBar  = (SeekBar)findViewById(R.id.AudioPlaying_SeekBar);
        mPauseButton = (CircleImageView)findViewById(R.id.AudioPlaying_Pause_imageButton);
        mPlayButton = (CircleImageView)findViewById(R.id.AudioPlaying_Play_ImageButton);
        mVolumeButton =(CircleImageView)findViewById(R.id.AudioPlaying_volume_imageButton);
        mStopButton = (CircleImageView)findViewById(R.id.AudioPlaying_Stop_imageButton);
        mPicturesList = (RecyclerView)findViewById(R.id.AudioPlaying_RecyclerView);

        //extras
        mAudioPath = getIntent().getStringExtra(getString(R.string.AudioPathExtra));
        ////////////////////////////////////////////////////////////////////////////////////////

        //preparing media player
        prepareMediaPlayer();

        // Thread (Update positionBar & timeLabel)
        ///////////////////////////////////////////////////////////////////////
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mMediaPlayer != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mMediaPlayer.getCurrentPosition();
                        Log.d(TAG, "run: " + mMediaPlayer.getCurrentPosition());
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                }
            }
        }).start();
        /////////////////////////////////////////////////////////////////////

        //listeners
        ///////////////////////////////////////////////////////////////////////

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.start();
                showPauseButton();
            }
        });

        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.pause();
                showPlayButton();
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                mMediaPlayer.stop();
                mSeekBar.setProgress(0);
                mMediaPlayer.seekTo(0);
                mAudioCurrentTime.setText("0:00");

                showPlayButton();
            }
        });

        mVolumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.volumeButtonClicked();
            }
        });

        mSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mMediaPlayer.seekTo(progress);
                            mSeekBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        //todo implement
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.stop();
                mSeekBar.setProgress(0);
                mMediaPlayer.seekTo(0);
                mAudioCurrentTime.setText("0:00");

                showPlayButton();
            }
        });
        ///////////////////////////////////////////////////////////////////////

        //getting Audio pictures
        mPresenter.getAudioPictures(mAudioPath);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Update positionBar.
            mSeekBar.setProgress(currentPosition);

            // Update current time text view
            String elapsedTime = createTimeLabel(currentPosition);
            mAudioCurrentTime.setText(elapsedTime);

        }
    };


    @Override
    public void fillPicturesList(ArrayList<Audio_Picture> audio_pictures) {
        PicturesListAdapter adapter = new PicturesListAdapter(audio_pictures , getBaseContext() , this);
        mPicturesList.setAdapter(adapter);
        mPicturesList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
    }

    @Override
    public void showVolume() {
        //todo implement
    }

    @Override
    public void showPlayButton() {
        mPauseButton.setVisibility(View.GONE);
        mPlayButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPauseButton() {
        mPauseButton.setVisibility(View.VISIBLE);
        mPlayButton.setVisibility(View.GONE);
    }

    @Override
    public View.OnClickListener pictureClicked(final Bitmap picBitmap) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPictureChoosen.setImageBitmap(picBitmap);
               // Toast.makeText(getBaseContext(), , Toast.LENGTH_SHORT).show();
            }
        };
    }

    //region utility functions
    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    public void prepareMediaPlayer() {
        mMediaPlayer =  MediaPlayer.create(this, Uri.parse(mAudioPath));
        mMediaPlayer.setLooping(true);
        mMediaPlayer.seekTo(0);
        mMediaPlayer.start();
        mSeekBar.setMax(mMediaPlayer.getDuration());
        mAudioFullTime.setText(createTimeLabel(mMediaPlayer.getDuration()));
    }
    //endregion
}
