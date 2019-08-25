package com.example.android.audio_visualizer.audios_list;

import com.example.android.audio_visualizer.models.Audio;

import java.util.ArrayList;

public interface AudiosListContract {

    interface View {
        public void fillRecordsList(ArrayList<Audio> audios);
        public void showMessage(String message);
    }

    interface Presenter {
        public void getRecords();
        public void deleteRecord(String path);
        public void updateRecordName(String path , String name);
    }

}
