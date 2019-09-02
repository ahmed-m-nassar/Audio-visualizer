package com.example.android.audio_visualizer.audio_playing;

import com.example.android.audio_visualizer.models.Audio_Picture;

import java.util.ArrayList;

public interface AudioPlayingContract {
    interface View {

        void fillPicturesList(ArrayList<Audio_Picture> audio_pictures);

        void showVolume();
        void showPlayButton();
        void showPauseButton();



        void startChangingUIThread();
    }

    interface Presenter {
        void getAudioPictures(String audioPath);

        void playButtonPressed();
        void pauseButtonPressed();
        void stopButtonPressed();
        void volumeButtonPressed();
    }
}
