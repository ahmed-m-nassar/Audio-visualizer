package com.example.android.audio_visualizer.utils;

import android.os.Environment;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FilesUtils {
    public static String AUDIO_FOLDER_NAME = "Audio_Visualizer";
    public static String AUDIO_STORAGE_PATH = Environment.getExternalStorageDirectory().toString()
                                                + "/" + AUDIO_FOLDER_NAME;
    public static String PICTURE_STORAGE_PATH = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES)
                                                .toString();


    /**
     * returns the file name from a given full path
     * @param path the path of the file
     * @return the name of the file
     */
    public static String getFileNameFromPath(String path) {
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }

  // /**
  //  *
  //  * @param path
  //  * @param newName
  //  * @return
  //  */
  // public static String replaceFileNameGivenFullPath(String path , String newName) {
  //     int index = path.lastIndexOf("/");
  //     return path.substring(0 , index + 1) + newName;
  // }

    /**
     * removing the restirected characters for naming a file and replacing them with '_'
     * @param name the file name
     * @return the new file name
     */
    public static String replacingInvalidFileNameCharacters(String name) {
        return name.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }

    /**
     * adds ".3gp" to the end of the file name if it doesnt have
     * @param name the name of the file
     * @return the new file name
     */
    public static String addExtentionToFileName(String name){
        //checking if the .3gb extention exists
        String extention = name.substring(name.length() - 4 ,name.length() );
        if(!(extention.toLowerCase().equals(".3gp"))) {
            return name + ".3gp";
        } else {
            return name;
        }
    }


    /**
     * merges audio files(sourceFiles[]) into one file (targetFile)
     * @param isAudio audio or video boolean
     * @param sourceFiles path of files to be merged
     * @param targetFile the target file in which the product of merge will be in
     * @return success or failure
     */
    public static boolean mergeMediaFiles(boolean isAudio, String sourceFiles[], String targetFile) {
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
