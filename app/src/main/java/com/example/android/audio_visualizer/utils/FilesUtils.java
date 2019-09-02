package com.example.android.audio_visualizer.utils;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FilesUtils {
    private static final String TAG = "FilesUtils";
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



    /**
     * removing the restirected characters for naming a file and replacing them with '_'
     * @param name the file name
     * @return the new file name
     */
    public static String replacingInvalidFileNameCharacters(String name) {
        return name.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }

    /**
     * adds ".wav" to the end of the file name if it doesnt have
     * @param name the name of the file
     * @return the new file name
     */
    public static String addExtentionToFileName(String name){
        //checking if the .wav extention exists
        String extention = name.substring(name.length() - 4 ,name.length() );
        if(!(extention.toLowerCase().equals(".wav"))) {
            return name + ".wav";
        } else {
            return name;
        }
    }

    public static void CombineWaveFile(String inputFile1, String inputFile2 )
    {
        Log.d(TAG, "CombineWaveFile: start");
        FileInputStream in1 = null, in2 = null;
        final int RECORDER_BPP = 16; //8,16,32..etc
        int RECORDER_SAMPLERATE = 32000; //8000,11025,16000,32000,48000,96000,44100..et
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 1;  //mono=1,stereo=2
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;

        int bufferSize=1024;
        byte[] data = new byte[bufferSize];


        try {
            in1 = new FileInputStream(inputFile1);
            in2 = new FileInputStream(inputFile2);

            String outPath = AUDIO_STORAGE_PATH + "/out.wav";
            out = new FileOutputStream(outPath);

            totalAudioLen = in1.getChannel().size() + in2.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            Log.d(TAG, "CombineWaveFile: before write wave file header");
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate,RECORDER_BPP);
            Log.d(TAG, "CombineWaveFile: after write wave file header");

            while (in1.read(data) != -1)
            {

                out.write(data);

            }
            Log.d(TAG, "CombineWaveFile: after rading file 1 data");
            while (in2.read(data) != -1)
            {
                out.write(data);
            }
            Log.d(TAG, "CombineWaveFile: after reading file 2 data");
            out.close();
            in1.close();
            in2.close();


            bufferSize=1024;
            data = new byte[bufferSize];


            out.close();
            out.flush();

            //deleting input file 1
            File f1 = new File(inputFile1);
            if(f1.exists()) {
                f1.delete();
            } else {
            }

            //deleting input file 2
            File f2 = new File(inputFile2);
            if(f2.exists()) {
                f2.delete();
            } else {

            }

            //renaming output file to input file 1
            File oldFile = new File(outPath);
            if(oldFile.exists()) {
                try {
                    oldFile.renameTo(new File(inputFile1));
                } catch (Exception e) {
                }
            } else {
            }

        } catch (FileNotFoundException e) {
            Log.d(TAG, "CombineWaveFile: not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "CombineWaveFile: not found");
            e.printStackTrace();
        }
    }

    private static void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate, int RECORDER_BPP)
            throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte)(totalDataLen & 0xff);
        header[5] = (byte)((totalDataLen >> 8) & 0xff);
        header[6] = (byte)((totalDataLen >> 16) & 0xff);
        header[7] = (byte)((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte)(longSampleRate & 0xff);
        header[25] = (byte)((longSampleRate >> 8) & 0xff);
        header[26] = (byte)((longSampleRate >> 16) & 0xff);
        header[27] = (byte)((longSampleRate >> 24) & 0xff);
        header[28] = (byte)(byteRate & 0xff);
        header[29] = (byte)((byteRate >> 8) & 0xff);
        header[30] = (byte)((byteRate >> 16) & 0xff);
        header[31] = (byte)((byteRate >> 24) & 0xff);
        header[32] = (byte)(2 * 16 / 8);
        header[33] = 0;
        header[34] = (byte) RECORDER_BPP;
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte)(totalAudioLen & 0xff);
        header[41] = (byte)((totalAudioLen >> 8) & 0xff);
        header[42] = (byte)((totalAudioLen >> 16) & 0xff);
        header[43] = (byte)((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }





}
