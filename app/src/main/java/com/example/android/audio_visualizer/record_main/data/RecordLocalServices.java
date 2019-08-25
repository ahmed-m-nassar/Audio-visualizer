package com.example.android.audio_visualizer.record_main.data;
import com.example.android.audio_visualizer.models.Audio;
import com.example.android.audio_visualizer.models.Audio_Picture;
import com.example.android.audio_visualizer.models.Picture;


public interface RecordLocalServices {
    /**
     * adding new audio file to the database
     * @param audio audio to be added
     * @return success or fail
     */
    public boolean addAudio(Audio audio);

    /**
     * adding new picture file to the database
     * @param picture picture to be added
     * @return success or fail
     */
    public boolean addPicture(Picture picture);

    /**
     * adding new Audio_Picture file to the database (connecting pictures with its audio)
     * @param audioPicture  audioPicture to be added
     * @return success or fail
     */
    public boolean addAudioPicture (Audio_Picture audioPicture);

    /**
     * checking if the user deleted any audio files outside the application
     */
    public void    checkAudioFiles();

    /**
     * checking if the user deleted any picture files outside the application
     */
    public void    checkPictureFiles();
}
