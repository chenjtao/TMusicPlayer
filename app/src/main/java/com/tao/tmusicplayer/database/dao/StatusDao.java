package com.tao.tmusicplayer.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.tao.tmusicplayer.database.DataBaseHelper;

public class StatusDao {

    private static final String TAG = "StatusDao";
    /**
     * 状态表
     */
    private static final String TABLE_STATUS_NAME = "status";

    /**
     * 状态表创建语句
     */
    public static final String TABLE_STATUS_CREATE = "create table if not exists " + TABLE_STATUS_NAME + "("
            + "id text primary key," + "filePath text," + "displayName text," + "title text,"
            + "artist text," + "album text," + "duration int," + "durationStr text,"
            + "size int," + "sizeStr text," + "mimeType text" + ")";

    /**
     * 状态表删除语句
     */
    public static final String TABLE_STATUS_DELETE = "drop table if exists " + StatusDao.TABLE_STATUS_NAME;

    private SQLiteDatabase database;

    public StatusDao(Context context) {
        database = new DataBaseHelper(context).getWritableDatabase();
    }
}
