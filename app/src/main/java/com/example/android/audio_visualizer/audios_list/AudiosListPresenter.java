package com.example.android.audio_visualizer.audios_list;

import com.example.android.audio_visualizer.audios_list.data.AudiosListLocalServices;
import com.example.android.audio_visualizer.audios_list.data.AudiosListLocalServicesImpl;
import com.example.android.audio_visualizer.models.Audio;
import com.example.android.audio_visualizer.utils.FilesUtils;

import java.io.File;
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

    @Override
    public void deleteRecord(String path) {
        //checking if the file exists
        File tempFile = new File(path);
        if(tempFile.exists()) {
            tempFile.delete();
            mModel.deleteAudio(path);
        } else {
            mView.showMessage("File does not exist");
        }
        //update shown list
        mView.fillRecordsList(mModel.getAudios());
    }

    @Override
    public void updateRecordName(String path, String newName) {
        //checking if the file exists
        File tempFile = new File(path);
        if(tempFile.exists()) {
            try {
                newName = FilesUtils.replacingInvalidFileNameCharacters(newName);
                newName = FilesUtils.addExtentionToFileName(newName);
                tempFile.renameTo(new File(newName));
                mModel.updateAudio(path,newName);
            } catch (Exception e) {
                mView.showMessage("Cant rename file");
            }
         } else {
            mView.showMessage("File does not exist");
        }
        //update shown list
        mView.fillRecordsList(mModel.getAudios());
    }
}
