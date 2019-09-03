package com.example.android.audio_visualizer.audio_playing;

import com.example.android.audio_visualizer.audio_playing.data.AudioPlayingLocalServices;
import com.example.android.audio_visualizer.audio_playing.data.AudioPlayingLocalServicesImpl;

public class AudioPlayingPresenter implements AudioPlayingContract.Presenter {

    private AudioPlayingContract.View mView;
    private AudioPlayingLocalServices mModel;



    public AudioPlayingPresenter(AudioPlayingContract.View view) {
        mView = view;
        mModel =  new AudioPlayingLocalServicesImpl();
    }

    @Override
    public void getAudioPictures(String audioPath) {
        mView.fillPicturesList(mModel.getAudioPictures(audioPath));
    }

}
