package com.example.android.audio_visualizer.audio_playing;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
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
import android.widget.Toast;

import com.example.android.audio_visualizer.audio_playing.adapter.PicturesListAdapter;
import com.example.android.audio_visualizer.audio_playing.adapter.PicuresListClickListeners;
import com.example.android.audio_visualizer.models.Audio_Picture;
import com.example.android.audio_visualizer.R;
import com.example.android.audio_visualizer.utils.DateTimeUtils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AudioPlayingActivity extends AppCompatActivity implements AudioPlayingContract.View , PicuresListClickListeners {

    //tag
    private static final String TAG = "AudioPlayingActivity";

    //presenter
    private AudioPlayingPresenter mPresenter;

    //views
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

    //audio path
    private String                mAudioPath;

    //media player
    private MediaPlayer           mMediaPlayer;

    //audio manager
    private AudioManager                                mAudioManager;
    private AudioManager.OnAudioFocusChangeListener     mAudioFocusChangeListener;


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

        //audio manager
        mAudioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);

        //preparing media player
        prepareMediaPlayer();

        //listeners
        ////////////////////////////////////////////////////////////////////////////////////////
        //audio focus change listener to react to audio focus changes
        mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if(focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    if(mMediaPlayer != null) {
                        mMediaPlayer.start();
                        showPauseButton();
                    }

                    Log.d(TAG, "onAudioFocusChange: focus gained");
                } else if(focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                        focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                        focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) { // loss of focus

                    mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
                    if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                        showPlayButton();
                    }
                }
            }
        };

        //play button click listener
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediaPlayer != null) {
                  playAudio();
                } else {
                    Toast.makeText(AudioPlayingActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //pause button click listener
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
                mMediaPlayer.pause();
                showPlayButton();
            }
        });

        //stop button click listener
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
                resetAudio();
                showPlayButton();
            }
        });

        //volume button click listener
        mVolumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showing the volume
                mAudioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC, // Stream type
                        mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC), // Index
                        AudioManager.FLAG_SHOW_UI // Flags
                );
            }
        });

        //seek bar change listener
        mSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        //if the user changed the progress of the seek bar we should change the progress
                        //of the media player
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

        //media player on completion listener
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
                resetAudio();
                showPlayButton();
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////

        //getting Audio pictures
        mPresenter.getAudioPictures(mAudioPath);

        //playing the audio
        playAudio();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //when back button is pressed we should stop the audio playing and abandon the audio focus
        if(mMediaPlayer != null) {
            mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void fillPicturesList(ArrayList<Audio_Picture> audio_pictures) {
        PicturesListAdapter adapter = new PicturesListAdapter(audio_pictures , getBaseContext() , this);
        mPicturesList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        mPicturesList.setAdapter(adapter);
    }



    @Override
    public View.OnClickListener pictureClicked(final Bitmap picBitmap) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showing the picture selected on mPictureChoosen image view
                mPictureChoosen.setImageBitmap(picBitmap);
            }
        };
    }

    //region utility functions

    /**
     * shows play button and hides pause button
     */
    private void showPlayButton() {
        mPauseButton.setVisibility(View.GONE);
        mPlayButton.setVisibility(View.VISIBLE);
    }


    /**
     * shows pause button and hides play button
     */
    private void showPauseButton() {
        mPauseButton.setVisibility(View.VISIBLE);
        mPlayButton.setVisibility(View.GONE);
    }


    /**
     * preparing MediaPlayer's object and preparing views
     */
    private void prepareMediaPlayer() {
        //preparing media player
        mMediaPlayer =  MediaPlayer.create(this, Uri.parse(mAudioPath));
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setVolume(1.0f , 1.0f);
        mMediaPlayer.seekTo(0);

        //preparing views
        mSeekBar.setMax(mMediaPlayer.getDuration());
        mAudioFullTime.setText(DateTimeUtils.createTimeLabel(mMediaPlayer.getDuration()));
    }

    /**
     * requesting the audio focus and playing the audio if the request was granted
     * @return
     */
    private boolean playAudio() {
        int audioFocusResult = mAudioManager.requestAudioFocus(mAudioFocusChangeListener ,
                AudioManager.STREAM_MUSIC ,
                AudioManager.AUDIOFOCUS_GAIN);
        //if the audio focus was granted we should play the voice
        if(audioFocusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //start playing the voice
            mMediaPlayer.start();

            //start changing the seek bar and timer in background thread
            startChangingUIThread();

            //showing the pause button as the audio started playing
            showPauseButton();

            return true;
        }

        return false;
    }


    /**
     * resets audio by restarting it from the beginning and resetting views
     */
    private void resetAudio() {
        //resetting audio
        mMediaPlayer.pause();
        mMediaPlayer.seekTo(0);

        //resetting views
        mSeekBar.setProgress(0);
        mAudioCurrentTime.setText("0:00");
    }


    /**
     * background thread to start moving the seek bar and setting the mAudioCurrentTime text view
     * according to the current progress of the media player
     */
    private void startChangingUIThread() {
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
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    //endregion
}
