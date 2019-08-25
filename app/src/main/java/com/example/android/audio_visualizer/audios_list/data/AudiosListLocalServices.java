package com.example.android.audio_visualizer.audios_list.data;

import com.example.android.audio_visualizer.models.Audio;

import java.util.ArrayList;

public interface AudiosListLocalServices {
    /**
     * getting audio files list
     * @return audios list
     */
    public ArrayList<Audio> getAudios();


    /**
     * deletes the audio row in database
     * @param path id of the audio to be removed
     */
    public void deleteAudio(String path);


    /**
     * updates the name column in database of the audio
     * @param path id of the audio to be updated
     * @param name the new name of the audio
     */
    public void updateAudio(String path , String name);


}
