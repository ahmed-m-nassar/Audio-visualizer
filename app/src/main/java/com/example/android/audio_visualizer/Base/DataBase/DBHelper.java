package com.example.android.audio_visualizer.Base.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.audio_visualizer.Base.DataBase.AudioVisualizerContract.Audio;
import com.example.android.audio_visualizer.Base.DataBase.AudioVisualizerContract.Picture;
import com.example.android.audio_visualizer.Base.DataBase.AudioVisualizerContract.Audio_Picture;

public class DBHelper extends SQLiteOpenHelper {
    private static final String Database_Name = "AudioVisualizer.db";
    private static final int Database_Version = 3;

    private SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, Database_Name, null, Database_Version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_AUDIO = "CREATE TABLE " + Audio.Table_Name
                + "( " + Audio.Column_Path + " TEXT PRIMARY KEY, "
                + Audio.Column_Name + " TEXT NOT NULL , "
                + Audio.Column_Date + " TEXT NOT NULL, "
                + Audio.Column_Size + " INTEGER NOT NULL, "
                + Audio.Column_Duration + " INTEGER NOT NULL "
                + ")";
        db.execSQL(SQL_CREATE_AUDIO);

        final String SQL_CREATE_PICTURE = "CREATE TABLE " + Picture.Table_Name
                + "( " + Picture.Column_Path + " TEXT PRIMARY KEY, "
                + Picture.Column_Name + " TEXT NOT NULL , "
                + Picture.Column_Date + " TEXT NOT NULL "
                + ")";
        db.execSQL(SQL_CREATE_PICTURE);

        final String SQL_CREATE_AUDIO_PICTURE = "CREATE TABLE " + Audio_Picture.Table_Name
                + "( " + Audio_Picture.Column_AudioPath + " TEXT NOT NULL  , "
                + Audio_Picture.Column_PicturePath + " TEXT NOT NULL  ,"
                + Audio_Picture.Column_SnapTime + " TEXT NOT NULL  ,"
                +" PRIMARY KEY(" +  Audio_Picture.Column_AudioPath + "," + Audio_Picture.Column_PicturePath + ")"
                + ")";
        db.execSQL(SQL_CREATE_AUDIO_PICTURE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ Audio.Table_Name);
        db.execSQL("DROP TABLE IF EXISTS "+ Picture.Table_Name);
        db.execSQL("DROP TABLE IF EXISTS "+ Audio_Picture.Table_Name);
        onCreate(db);
    }
    public Cursor select(String query, String Argument[])
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cu= db.rawQuery(query, Argument);
        return cu;

    }
    public Boolean insert(String tablename, ContentValues content)
    {
        SQLiteDatabase db = getWritableDatabase();
        Long success= db.insert(tablename,null,content);
        return success!=-1;
    }
    public Boolean update(String tablename,ContentValues contentValues,String Argument[],String whreclause)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        int NumberOfRowAffected= db.update(tablename, contentValues, whreclause, Argument);
        return NumberOfRowAffected!=0;
    }
    public Boolean deleteWithoutCascade(String tablename,String Argument[],String whereclause)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        return db.delete(tablename,whereclause,Argument)>0;
    }
}
