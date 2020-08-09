package com.tao.tmusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import com.tao.tmusicplayer.database.dao.MusicDao;
import com.tao.tmusicplayer.database.entity.Music;

import java.io.IOException;
import java.util.List;

public class MusicService extends Service {

    private static final String TAG = "MusicService";

    private MusicDao musicDao;
    private List<Music> musicList;
    private MediaPlayer musicPlayer;
    private MusicService musicService;
    private MusicController musicController;
    private int playIndex = -1;
    private String oldPath;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: successful");
        musicDao = new MusicDao(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: successful");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: successful");
        if (musicController == null) {
            musicController = new MusicController();
        }
        return musicController;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind: successful");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: successful");
        if (musicPlayer != null) {
            if (musicPlayer.isPlaying() || musicPlayer.isLooping()) {
                musicPlayer.stop();
            }
            musicPlayer.release();
            musicPlayer = null;
        }
    }

    public long refreshMusicList() {
        long count = 0;
        if (musicList != null && musicList.size() > 0) {
            musicList.clear();
        }
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Music music = new Music();

                music.setId(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                music.setFilePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                music.setDisplayName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                music.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                music.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                music.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
                music.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                music.setDurationStr(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                music.setSize(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                music.setSizeStr(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                music.setMimeType(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)));

                if (music.getSize() > 1000 * 800) {
                    long l = musicDao.addLocalMusic(music);
                    if (l > 0) count++;
                }
            }
            // 释放资源
            cursor.close();
            List<Music> list = musicDao.getAllLocalMusic();
            if (list != null && !list.isEmpty()) {
                musicList.addAll(list);
            }
        }
        return count;
    }

    public class MusicController extends Binder {
        private static final String TAG = "MusicController";

        public MusicService getMusicService() {
            return MusicService.this;
        }

        public MediaPlayer getMediaPlayer() {
            if (musicPlayer == null) {
                musicPlayer = new MediaPlayer();
            }
            return musicPlayer;
        }

        public Music getMusicInfo() {
            if (playIndex < 0) {
                return musicList.get(0);
            }
            return musicList.get(playIndex);
        }

        public int getMusicIndex() {
            return playIndex;
        }

        public long refreshMusicData() {
            Log.i(TAG, "refreshMusicData: 刷新音乐数据！");
            return refreshMusicList();
        }

        public void setMusicList(List<Music> musicList) {
            MusicService.this.musicList = musicList;
        }

        public int openMusic(int index) {
            if (index < 0) {
                playIndex = musicList.size() - 1;
            } else if (index > musicList.size() - 1) {
                playIndex = 0;
            } else {
                playIndex = index;
            }
            String path = musicList.get(playIndex).getFilePath();
            if (musicPlayer == null) {
                musicPlayer = new MediaPlayer();
            }
            if (!path.isEmpty() && !path.equals(oldPath)) {
                try {
                    musicPlayer.reset();
                    musicPlayer.setDataSource(path);
                    musicPlayer.prepare();
                    oldPath = path;
                    Log.i(TAG, "openMusic: 打开音乐文件，准备播放音乐");
                } catch (IOException e) {
                    Log.e(TAG, "openMusic: 音乐资源加载失败", e);
                }
            }
            return playIndex;
        }

        public void play() {
            if (musicPlayer != null && !musicPlayer.isPlaying()) {
                musicPlayer.start();
                Log.i(TAG, "play: 播放音乐！");
            }
        }

        public void pause() {
            if (musicPlayer != null && musicPlayer.isPlaying()) {
                musicPlayer.pause();
                Log.i(TAG, "pause: 暂停音乐！");
            }
        }

        public void setLoop(boolean loop) {
            if (musicPlayer != null) {
                musicPlayer.setLooping(loop);
            }
        }

        public void seekTo(int progress) {
            if (musicPlayer != null) {
                musicPlayer.seekTo(progress);
            }
        }

        public int getDuration() {
            if (musicPlayer != null) {
                return musicPlayer.getDuration();
            }
            return 0;
        }

        public int getCurrentPosition() {
            if (musicPlayer != null) {
                return musicPlayer.getCurrentPosition();
            }
            return 0;
        }

    }
}
