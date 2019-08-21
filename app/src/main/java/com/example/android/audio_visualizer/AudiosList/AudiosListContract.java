package com.example.android.audio_visualizer.AudiosList;

import com.example.android.audio_visualizer.Models.Audio;

import java.util.ArrayList;

public interface AudiosListContract {

    interface View {
        public void fillRecordsList(ArrayList<Audio> audios);
    }

    interface Presenter {
        public void getRecords();
    }

}
