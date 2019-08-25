package com.example.android.audio_visualizer.record_main;

import android.media.MediaRecorder;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.example.android.audio_visualizer.models.Audio;
import com.example.android.audio_visualizer.models.Audio_Picture;
import com.example.android.audio_visualizer.models.Picture;
import com.example.android.audio_visualizer.record_main.data.RecordLocalServices;
import com.example.android.audio_visualizer.record_main.data.RecordLocalServicesImpl;
import com.example.android.audio_visualizer.utils.DateTimeUtils;
import com.example.android.audio_visualizer.utils.FilesUtils;
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
    private File                mDirectory; //audio file directory
    private File                mOriginalAudioFile; //first audio file created (used to merge all recorded files into it)
    private boolean             mIsPaused; //checks if the audio is paused or not

    private ArrayList<String>   mAudioFiles;  //all audio files paths recorded to be merged when user stops the record
    private ArrayList<Picture>  mPicturesTaken; // all pictures taken paths to be saved in the database when user stops the record
    private ArrayList<String>   mPicsSnapTimes; //pictures snap time

    private MediaRecorder       mRecorder;

    public RecordPresenter(RecordContract.View view) {
        mView = view;
        mModel = new RecordLocalServicesImpl();
        mIsPaused = false;
        mPicturesTaken = new ArrayList<>();
        mPicsSnapTimes = new ArrayList<>();
        mAudioFiles = new ArrayList<>();
        //preparing file path
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

                //adding the new temp file to the audio list to merge it later
                mAudioFiles.add(newTempFile.getPath());
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

            //clearing picturesTaken , snapTimes , audioFiles arrays at the beggining of a new recording
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
        if(!mIsPaused) //if the record is On -> stop it
            mRecorder.stop();

        for(int i = 0 ; i < mAudioFiles.size() ; i++) { //deleting all recorded files
            File audioFile = new File (mAudioFiles.get(i));
            if(audioFile.exists()) {
                audioFile.delete();
            }
        }

        mAudioFiles.clear();

        mIsPaused = false;

    }


    @Override
    public void pauseButtonClicked() {
        //dealing with views
        mView.pauseTimer();
        mView.showMicOffPicture();
        mView.showRecordButton();

        //stopping recorder to merge it when user stops the record
        mRecorder.stop();

        mIsPaused = true;
    }

    @Override
        public void stopButtonClicked() {
        //saving audio
        if(!mIsPaused) {
            mRecorder.stop();
        }

        //getting date and duration
        String recordDuration =DateTimeUtils.createTimeLabel(mView.getCurrentTimerTime());
        String recordDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

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



        //merging audio files
        String[] sourceFiles = new String[mAudioFiles.size()];
        sourceFiles = mAudioFiles.toArray(sourceFiles);
        mergeMediaFiles(true , sourceFiles , sourceFiles[0]);

        //deleting unneeded files
        for(int i = 1 ; i < mAudioFiles.size() ; i++) {
            File audioFile = new File (mAudioFiles.get(i));
            if(audioFile.exists()) {
                audioFile.delete();
            }
        }


        //add the new audio to database
        Audio audio = new Audio(mOriginalAudioFile.getName() , mOriginalAudioFile.getPath() , recordDate ,
                recordDuration,(int) mOriginalAudioFile.length());
        mModel.addAudio(audio);


        //add the pictures in the database
        for(int i = 0 ; i < mPicturesTaken.size() ; i++) {
            //adding pictures in Picture Table
            mModel.addPicture(mPicturesTaken.get(i));

            //adding pictures and audio in Audio_Picture table
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

        String date = DateTimeUtils.getCurrentDate();
        String picName = FilesUtils.getFileNameFromPath(fullPicPath);
        mPicturesTaken.add(new Picture(picName , fullPicPath ,date ));
        //getting the snap time
        mPicsSnapTimes.add(DateTimeUtils.createTimeLabel(mView.getCurrentTimerTime()));
    }

    @Override
    public void checkFiles() {
        mModel.checkAudioFiles();
        mModel.checkPictureFiles();
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


