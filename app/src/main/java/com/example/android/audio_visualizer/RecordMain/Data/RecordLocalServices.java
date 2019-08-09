package com.example.android.audio_visualizer.RecordMain.Data;
import com.example.android.audio_visualizer.Models.Audio;
import com.example.android.audio_visualizer.Models.Audio_Picture;
import com.example.android.audio_visualizer.Models.Picture;


public interface RecordLocalServices {
    public boolean addAudio(Audio audio);
    public boolean addPicture(Picture picture);
    public boolean addAudioPicture (Audio_Picture audioPicture);
}
