package com.example.android.audio_visualizer.AudioPlaying;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import com.example.android.audio_visualizer.AudioPlaying.Data.AudioPlayingLocalServices;
import com.example.android.audio_visualizer.AudioPlaying.Data.AudioPlayingLocalServicesImpl;
import com.example.android.audio_visualizer.R;

public class AudioPlayingPresenter implements AudioPlayingContract.Presenter {

    private AudioPlayingContract.View mView;
    private AudioPlayingLocalServices mModel;



    public AudioPlayingPresenter(AudioPlayingContract.View view) {
        mView = view;
        mModel =  new AudioPlayingLocalServicesImpl();
    }



    @Override
    public void volumeButtonClicked() {

    }

    @Override
    public void getAudioPictures(String audioPath) {
        mView.fillPicturesList(mModel.getAudioPictures(audioPath));
    }
}
