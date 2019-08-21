package com.example.android.audio_visualizer.AudiosList;

import com.example.android.audio_visualizer.AudiosList.Data.AudiosListLocalServices;
import com.example.android.audio_visualizer.AudiosList.Data.AudiosListLocalServicesImpl;
import com.example.android.audio_visualizer.Models.Audio;

import java.util.ArrayList;

public class AudiosListPresenter implements AudiosListContract.Presenter {

    private AudiosListContract.View  mView;
    private AudiosListLocalServices  mModel;

    public AudiosListPresenter(AudiosListContract.View view)  {
        mView = view;
        mModel = new AudiosListLocalServicesImpl();
    }

    @Override
    public void getRecords() {
        ArrayList<Audio> audios = mModel.getAudios();
        mView.fillRecordsList(audios);
    }
}
