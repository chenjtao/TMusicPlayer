package com.tao.tmusicplayer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tao.tmusicplayer.database.dao.MusicDao;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "TMusicPlayer.db";

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(MusicDao.TABLE_MUSIC_LOCAL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(MusicDao.TABLE_MUSIC_LOCAL_DELETE);
        sqLiteDatabase.execSQL(MusicDao.TABLE_MUSIC_LOCAL_CREATE);
    }
}
