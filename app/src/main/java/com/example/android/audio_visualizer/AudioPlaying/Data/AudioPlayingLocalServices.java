package com.example.android.audio_visualizer.AudioPlaying.Data;

import com.example.android.audio_visualizer.Models.Audio;
import com.example.android.audio_visualizer.Models.Audio_Picture;
import com.example.android.audio_visualizer.Models.Picture;

import java.util.ArrayList;

public interface AudioPlayingLocalServices {
    public  Audio getAudio(String audioPath);
    public  ArrayList<Audio_Picture> getAudioPictures(String audioPath);
}
