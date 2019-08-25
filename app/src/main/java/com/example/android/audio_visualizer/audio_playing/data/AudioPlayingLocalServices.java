package com.example.android.audio_visualizer.audio_playing.data;

import com.example.android.audio_visualizer.models.Audio;
import com.example.android.audio_visualizer.models.Audio_Picture;

import java.util.ArrayList;

public interface AudioPlayingLocalServices {
    public  Audio getAudio(String audioPath);

    /**
     * getting pictures of a particular audio file
     * @param audioPath path of the audio file
     * @return pictures belonging to to the audio file
     */
    public  ArrayList<Audio_Picture> getAudioPictures(String audioPath);
}
