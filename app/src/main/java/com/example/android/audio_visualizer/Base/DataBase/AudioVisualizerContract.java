package com.example.android.audio_visualizer.Base.DataBase;

import android.provider.BaseColumns;

public class AudioVisualizerContract {

    private AudioVisualizerContract(){}

    public static class Audio implements BaseColumns {
        public static final String Table_Name       = "Audio";
        public static final String Column_Name      = "Name";
        public static final String Column_Date      = "Date";
        public static final String Column_Duration  = "Duration";
        public static final String Column_Size      = "Size";
        public static final String Column_Path      = "Path";

    }

    public static class Picture implements BaseColumns {
        public static final String Table_Name  = "Picture";
        public static final String Column_Name = "Name";
        public static final String Column_Path = "Path";
        public static final String Column_Date = "Date";

    }

    public static class Audio_Picture implements BaseColumns {
        public static final String Table_Name         = "Audio_Picture";
        public static final String Column_AudioName   = "AudioName";
        public static final String Column_PictureName = "PictureName";
        public static final String Column_SnapTime    = "SnapTime";

    }
}
