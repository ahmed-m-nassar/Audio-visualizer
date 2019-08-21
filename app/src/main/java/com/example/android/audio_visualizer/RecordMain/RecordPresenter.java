package com.example.android.audio_visualizer.RecordMain;

import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.example.android.audio_visualizer.Models.Audio;
import com.example.android.audio_visualizer.Models.Audio_Picture;
import com.example.android.audio_visualizer.Models.Picture;
import com.example.android.audio_visualizer.RecordMain.Data.RecordLocalServices;
import com.example.android.audio_visualizer.RecordMain.Data.RecordLocalServicesImpl;
import com.example.android.audio_visualizer.Utils.FilesUtils;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class RecordPresenter implements RecordContract.Presenter {

    private RecordContract.View mView;
    private RecordLocalServices mModel;
    private File                mDirectory;
    private File                mOriginalAudioFile;
    private boolean             mIsPaused;

    private ArrayList<String>   mAudioFiles;
    private ArrayList<Picture>  mPicturesTaken;
    private ArrayList<String>   mPicsSnapTimes;

    private MediaRecorder       mRecorder;

    public RecordPresenter(RecordContract.View view) {
        mView = view;
        mModel = new RecordLocalServicesImpl();
        mIsPaused = false;
        mPicturesTaken = new ArrayList<>();
        mPicsSnapTimes = new ArrayList<>();
        mAudioFiles = new ArrayList<>();
        //preparing file path
        //Todo use utils
        mDirectory = new File(FilesUtils.AUDIO_STORAGE_PATH, "");
        if (!mDirectory.exists()) {
            mDirectory.mkdirs();
        }
    }

    @Override
    public void recordButtonClicked() throws IOException {
        //if the record was already paused
        if(mIsPaused) {
            //dealing with views
            mView.showPauseButton();
            mView.resumeTimer();
            mView.showMicOnPicture();

            //resuming recorder
            //creating a new temp file to merge later
            try {
                File newTempFile = File.createTempFile("sound", ".3gp", mDirectory);
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile(newTempFile.getPath());
                mRecorder.prepare();
                mRecorder.start();

                //adding the new temp file to merge it later
                mAudioFiles.add(newTempFile.getPath());
                mView.showRecordName(mOriginalAudioFile.getName());
            } catch (IOException e) {
                return;
            }

            mIsPaused=false;

        } else { // if we are starting a new record

            boolean permissionMissing = false;
            //check for permissions
            //////////////////////////////////////////////////////////////////
            if(!mView.checkRecordPermission()) {
                permissionMissing = true;
                mView.requestRecordPermission();
            }
            if(!mView.checkReadExternalStoragePermission()) {
                permissionMissing = true;
                mView.requestReadExternalStoragePermission();
            }
            if(!mView.checkWriteExternalStoragePermission()) {
                permissionMissing = true;
                mView.requestWriteExternalStoragePermission();
            }

            //if any permission is missing we shouldn`t record
            if(permissionMissing) {
                return;
            }
            /////////////////////////////////////////////////////////////////////

            //dealing with views
            mView.startTimer();
            mView.enableTakePictureImage();
            mView.showMicOnPicture();
            mView.showPauseButton();
            mView.showCancelButton();
            mView.enableCancelButton();
            mView.enableStopButton();

            //making a new picture paths array to be added to the database at the end of the recording
            mPicturesTaken.clear();
            mPicsSnapTimes.clear();
            mAudioFiles.clear();

            //preparing recorder
            try {
                mOriginalAudioFile = File.createTempFile("sound", ".3gp", mDirectory);
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile(mOriginalAudioFile.getPath());
                mRecorder.prepare();
                mRecorder.start();

                mAudioFiles.add(mOriginalAudioFile.getPath());
                mView.showRecordName(mOriginalAudioFile.getName());
            } catch (IOException e) {
                return;
            }

            mIsPaused = false;
        }

    }

    @Override
    public void cancelButtonClicked() {

        //dealing with views
        mView.disableTakePictureImage();
        mView.pauseTimer();
        mView.resetTimer();
        mView.disableCancelButton();
        mView.disableStopButton();
        mView.showMicOffPicture();
        mView.showRecordButton();
        mView.hideRecordName();
        mView.showListButton();

        //stopping and deleting record
        mRecorder.stop();
        mOriginalAudioFile.delete();
        mAudioFiles.clear();

        mIsPaused = false;

    }


    @Override
    public void pauseButtonClicked() {
        //dealing with views
        mView.pauseTimer();
        mView.showMicOffPicture();
        mView.showRecordButton();

        //pausing recorder
        mRecorder.stop();

        mIsPaused = true;
    }

    @Override
    public void stopButtonClicked() {

        //dealing with views
        mView.disableTakePictureImage();
        mView.pauseTimer();
        mView.resetTimer();
        mView.showMicOffPicture();
        mView.showRecordButton();
        mView.disableCancelButton();
        mView.disableStopButton();
        mView.hideRecordName();
        mView.showListButton();

        //saving audio
        if(!mIsPaused) {
            mRecorder.stop();
        }

        //merging audio files
        String[] sourceFiles = new String[mAudioFiles.size()];
        sourceFiles = mAudioFiles.toArray(sourceFiles);
        mergeMediaFiles(true , sourceFiles , sourceFiles[0]);

        //TODO get today`s date and the duration
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Audio audio = new Audio(mOriginalAudioFile.getName() , mOriginalAudioFile.getPath() , date ,
                100,(int) mOriginalAudioFile.length());

        //add the new audio to database
        mModel.addAudio(audio);

        //add the pictures in the database
        for(int i = 0 ; i < mPicturesTaken.size() ; i++) {
            mModel.addPicture(mPicturesTaken.get(i));

            Audio_Picture audio_picture = new Audio_Picture(mOriginalAudioFile.getPath()
                    ,mPicturesTaken.get(i).getmPath() , mPicsSnapTimes.get(i));

            mModel.addAudioPicture(audio_picture);
        }

        mIsPaused = false;
        mAudioFiles.clear();


    }

    @Override
    public void takePictureButtonClicked() {
        mView.startCameraIntent();
    }

    @Override
    public void savePicture(String fullPicPath) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String picName = FilesUtils.getFileNameFromPath(fullPicPath);
        mPicturesTaken.add(new Picture(picName , fullPicPath ,date ));
        //getting the snap time
        mPicsSnapTimes.add(mView.getCurrentTimerTime());
    }

    private static boolean mergeMediaFiles(boolean isAudio, String sourceFiles[], String targetFile) {
        try {
            String mediaKey = isAudio ? "soun" : "vide";
            List<Movie> listMovies = new ArrayList<>();
            for (String filename : sourceFiles) {
                listMovies.add(MovieCreator.build(filename));
            }
            List<Track> listTracks = new LinkedList<>();
            for (Movie movie : listMovies) {
                for (Track track : movie.getTracks()) {
                    if (track.getHandler().equals(mediaKey)) {
                        listTracks.add(track);
                    }
                }
            }
            Movie outputMovie = new Movie();
            if (!listTracks.isEmpty()) {
                outputMovie.addTrack(new AppendTrack(listTracks.toArray(new Track[listTracks.size()])));
            }
            Container container = new DefaultMp4Builder().build(outputMovie);
            FileChannel fileChannel = new RandomAccessFile(String.format(targetFile), "rw").getChannel();
            container.writeContainer(fileChannel);
            fileChannel.close();
            return true;
        }
        catch (IOException e) {
            Log.e("RecordPResenter", "Error merging media files. exception: "+e.getMessage());
            return false;
        }
    }
}


