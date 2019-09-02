package com.example.android.audio_visualizer.audio_playing;

import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.android.audio_visualizer.audio_playing.adapter.PicturesListAdapter;
import com.example.android.audio_visualizer.audio_playing.adapter.PicuresListClickListeners;
import com.example.android.audio_visualizer.models.Audio_Picture;
import com.example.android.audio_visualizer.R;
import com.example.android.audio_visualizer.utils.DateTimeUtils;

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

        //listeners
        ///////////////////////////////////////////////////////////////////////

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.start();
                startChangingUIThread();
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
               resetAudio();
               showPlayButton();
            }
        });

        mVolumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.volumeButtonPressed();
            }
        });

        mSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser && mMediaPlayer!= null) {
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

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                resetAudio();
                showPlayButton();
            }
        });
        ///////////////////////////////////////////////////////////////////////

        //getting Audio pictures
        mPresenter.getAudioPictures(mAudioPath);

        //starting media player
        mMediaPlayer.start();
        startChangingUIThread();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();k
        mMediaPlayer = null;
    }

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
    public void startChangingUIThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    final int currentPos = mMediaPlayer.getCurrentPosition();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSeekBar.setProgress(currentPos);
                            mAudioCurrentTime.setText(DateTimeUtils.createTimeLabel(currentPos));
                        }
                    });
                    try {
                        Thread.sleep(300);
                    } catch (Exception e) {

                    }
                }
            }
        }).start();
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

    public void prepareMediaPlayer() {
        mMediaPlayer =  MediaPlayer.create(this, Uri.parse(mAudioPath));
        mMediaPlayer.setLooping(true);
        mMediaPlayer.seekTo(0);
        mSeekBar.setMax(mMediaPlayer.getDuration());
        mAudioFullTime.setText(DateTimeUtils.createTimeLabel(mMediaPlayer.getDuration()));
    }



    private void resetAudio() {
        mMediaPlayer.pause();
        mSeekBar.setProgress(0);
        mMediaPlayer.seekTo(0);
        mAudioCurrentTime.setText("0:00");
    }
    //endregion
}
