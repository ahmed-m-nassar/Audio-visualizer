package com.example.android.audio_visualizer.AudioPlaying;

import com.example.android.audio_visualizer.Models.Audio_Picture;

import java.util.ArrayList;

public interface AudioPlayingContract {
    interface View {

        void fillPicturesList(ArrayList<Audio_Picture> audio_pictures);

        void showVolume();

        //todo check if u need these fns
        void showPlayButton();
        void showPauseButton();


    }

    interface Presenter {

        void volumeButtonClicked();
        void getAudioPictures(String audioPath);
    }
}
