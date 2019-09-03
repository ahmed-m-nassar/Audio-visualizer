package com.example.android.audio_visualizer.audio_playing;

import com.example.android.audio_visualizer.models.Audio_Picture;

import java.util.ArrayList;

public interface AudioPlayingContract {
    interface View {

        /**
         * fills the pictures horizontal list
         * @param audio_pictures pictures to be shown
         */
        void fillPicturesList(ArrayList<Audio_Picture> audio_pictures);

    }

    interface Presenter {
        /**
         * gets the pictures taken while recording an audio from data base and sending them to view
         * @param audioPath the path of the audio whose pictures we are trying to get
         */
        void getAudioPictures(String audioPath);
    }
}
