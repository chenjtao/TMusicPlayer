package com.tao.tmusicplayer.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tao.tmusicplayer.database.DataBaseHelper;
import com.tao.tmusicplayer.database.entity.Music;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicDao {

    private static final String TAG = "MusicDao";
    /**
     * 本地音乐表
     */
    private static final String TABLE_MUSIC_LOCAL_NAME = "music_local";

    /**
     * 本地音乐表创建语句
     */
    public static final String TABLE_MUSIC_LOCAL_CREATE = "create table if not exists " + TABLE_MUSIC_LOCAL_NAME + "("
            + "id text primary key," + "filePath text," + "displayName text," + "title text,"
            + "artist text," + "album text," + "duration int," + "durationStr text,"
            + "size int," + "sizeStr text," + "mimeType text" + ")";

    /**
     * 本地音乐表删除语句
     */
    public static final String TABLE_MUSIC_LOCAL_DELETE = "drop table if exists " + MusicDao.TABLE_MUSIC_LOCAL_NAME;

    private SQLiteDatabase database;

    public MusicDao(Context context) {
        database = new DataBaseHelper(context).getWritableDatabase();
    }

    /**
     * 添加歌曲到本地播放列表
     */
    public long addLocalMusic(Music music) {
        long l = 0;
        ContentValues values = getMusicValues(music);
        try {
            if (getMusicInfoById(music.getId()) == null) {
                l = database.insert(TABLE_MUSIC_LOCAL_NAME, null, values);
            }
        } catch (SQLException e) {
            Log.e(TAG, "addLocalMusic: ", e);
        }
        return l;
    }

    /**
     * 依据mid删除相关数据
     */
    public void delLocalMusicById(String id) {
        try {
            database.delete(TABLE_MUSIC_LOCAL_NAME, "id=?", new String[]{id});
        } catch (SQLException e) {
            Log.e(TAG, "delLocalMusicById: ", e);
        }
    }

    /**
     * 删除所有数据
     */
    public void deleteAll() {
        try {
            database.execSQL(TABLE_MUSIC_LOCAL_DELETE);
            database.execSQL(TABLE_MUSIC_LOCAL_CREATE);
        } catch (SQLException e) {
            Log.e(TAG, "deleteAll: ", e);
        }
    }

    /**
     * 通过mid来获取歌曲的相关信息
     */
    public Music getMusicInfoById(String id) {
        Cursor cursor = database.rawQuery("select * from " + TABLE_MUSIC_LOCAL_NAME + " where id=?", new String[]{id});
        if (!cursor.moveToNext()) {
            return null;
        }
        Music music = getMusicInfo(cursor);
        cursor.close();
        return music;
    }

    /**
     * 查询所有的本地歌曲
     */
    public List<Music> getAllLocalMusic() {
        List<Music> list = new ArrayList<>();
        Cursor cursor = database.query(TABLE_MUSIC_LOCAL_NAME, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Music music = getMusicInfo(cursor);
            File file = new File(music.getFilePath());
            if (!file.exists()) {
                delLocalMusicById(music.getId());
            } else {
                list.add(music);
            }
        }
        cursor.close();
        return list;
    }

    /**
     * 获取本地歌曲总数
     */
    public int getCount() {
        Cursor cursor = database.rawQuery("select count(id)from " + TABLE_MUSIC_LOCAL_NAME, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    /**
     * 通过Cursor来提取相关的MusicInfo数据
     */
    private Music getMusicInfo(Cursor cursor) {
        Music music = new Music();

        music.setId(cursor.getString(cursor.getColumnIndex("id")));
        music.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
        music.setDisplayName(cursor.getString(cursor.getColumnIndex("displayName")));
        music.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        music.setArtist(cursor.getString(cursor.getColumnIndex("artist")));
        music.setAlbum(cursor.getString(cursor.getColumnIndex("album")));
        music.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
        music.setDurationStr(cursor.getString(cursor.getColumnIndex("durationStr")));
        music.setSize(cursor.getInt(cursor.getColumnIndex("size")));
        music.setSizeStr(cursor.getString(cursor.getColumnIndex("sizeStr")));
        music.setMimeType(cursor.getString(cursor.getColumnIndex("mimeType")));

        return music;
    }

    private ContentValues getMusicValues(Music music) {

        ContentValues values = new ContentValues();

        values.put("id", music.getId());
        values.put("filePath", music.getFilePath());
        values.put("displayName", music.getDisplayName());
        values.put("title", music.getTitle());
        values.put("artist", music.getArtist());
        values.put("album", music.getAlbum());
        values.put("duration", music.getDuration());
        values.put("durationStr", music.getDurationStr());
        values.put("size", music.getSize());
        values.put("sizeStr", music.getSizeStr());
        values.put("mimeType", music.getMimeType());

        return values;
    }
}
