package com.tao.tmusicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tao.tmusicplayer.database.entity.Music;
import com.tao.tmusicplayer.service.MusicService;
import com.tao.tmusicplayer.utils.FormatUtil;
import com.tao.tmusicplayer.widget.LyricView;

import java.util.ArrayList;
import java.util.List;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener,
        MediaPlayer.OnCompletionListener {

    private TextView tv_play_name;
    private TextView tv_time_now;
    private TextView tv_time_max;
    private ImageView iv_back;
    private ImageView iv_model;
    private ImageView iv_last;
    private ImageView iv_play;
    private ImageView iv_next;
    private ImageView iv_queue;
    private SeekBar seekBar;
    private LyricView lyricView;

    private Context context = this;
    private ServiceConnection musicConnection;
    private MusicService.MusicController musicController;
    private Music music;

    private int playIndex = -1;
    private boolean looping = false;
    private final int UPDATE_UI = 0x0001;

    //使用handler定时执行任务
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UPDATE_UI) {
                updateUI();
                //每500毫秒更新一次
                handler.sendEmptyMessageDelayed(UPDATE_UI, 500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initView();
        setListener();
        startMusicService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //进入到界面后开始更新进度条
        if (musicController != null) {
            handler.sendEmptyMessage(UPDATE_UI);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止更新进度条的进度
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出应用后与service解除绑定
        unbindService(musicConnection);
    }

    private void initView() {
        tv_play_name = findViewById(R.id.tv_play_name);
        tv_time_now = findViewById(R.id.tv_play_time_now);
        tv_time_max = findViewById(R.id.tv_play_time_max);
        iv_back = findViewById(R.id.iv_play_back);
        iv_model = findViewById(R.id.iv_play_model);
        iv_last = findViewById(R.id.iv_play_last);
        iv_play = findViewById(R.id.iv_play);
        iv_next = findViewById(R.id.iv_play_next);
        iv_queue = findViewById(R.id.iv_play_queue);
        seekBar = findViewById(R.id.seekBar_play);
        lyricView = findViewById(R.id.lyric_play);

        List<String> textList = new ArrayList<>();
        List<Long> timeList = new ArrayList<>();
        textList.add("抱歉，暂不支持歌词播放");
        timeList.add((long) 0);
        lyricView.setLyricText(textList, timeList);
    }

    private void setListener() {
        iv_back.setOnClickListener(this);
        iv_model.setOnClickListener(this);
        iv_last.setOnClickListener(this);
        iv_play.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_queue.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
    }

    private void startMusicService() {
        //启动后台音乐服务
        Intent musicIntent = new Intent(context, MusicService.class);
        musicConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicController = (MusicService.MusicController) service;
                music = musicController.getMusicInfo();
                playIndex = musicController.getMusicIndex();
                handler.sendEmptyMessage(UPDATE_UI);
                Log.d("PlayActivity", "onServiceConnected: ");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicController = null;
            }
        };
        bindService(musicIntent, musicConnection, BIND_AUTO_CREATE);
    }

    private void selectMusic(int position) {
        playIndex = musicController.openMusic(position);
        musicController.setLoop(looping);
        musicController.getMediaPlayer().setOnCompletionListener(PlayActivity.this);
        tv_play_name.setText(String.format("%s-%s", music.getTitle(), music.getArtist()));
        tv_time_max.setText(FormatUtil.formatTime(musicController.getDuration()));
        tv_time_now.setText(FormatUtil.formatTime(musicController.getCurrentPosition()));
        seekBar.setMax(musicController.getDuration());
        seekBar.setProgress(0);
    }

    private void play() {
        if (musicController != null) {
            musicController.play();
            updateUI();
        }
    }

    private void pause() {
        if (musicController != null) {
            musicController.pause();
            updateUI();
        }
    }

    private void last() {
        selectMusic(playIndex - 1);
        play();
        handler.sendEmptyMessage(UPDATE_UI);
    }

    private void next() {
        selectMusic(playIndex + 1);
        play();
        handler.sendEmptyMessage(UPDATE_UI);
    }

    private void updateUI() {
        music = musicController.getMusicInfo();
        if (musicController.getMediaPlayer().isPlaying()) {
            iv_play.setImageResource(R.drawable.ic_pause_circle);
        } else {
            iv_play.setImageResource(R.drawable.ic_play_circle);
        }
        if (musicController != null && musicController.getMediaPlayer() != null && musicController.getMediaPlayer().isPlaying()) {
            tv_play_name.setText(String.format("%s-%s", music.getTitle(), music.getArtist()));
            tv_time_max.setText(FormatUtil.formatTime(musicController.getDuration()));
            tv_time_now.setText(FormatUtil.formatTime(musicController.getCurrentPosition()));
            seekBar.setMax(musicController.getDuration());
            seekBar.setProgress(musicController.getCurrentPosition());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_play_back:
                finish();
                break;
            case R.id.iv_play:
                if (musicController != null) {
                    if (musicController.getMediaPlayer() == null || playIndex == -1) {
                        selectMusic(0);
                    }
                    if (musicController.getMediaPlayer().isPlaying()) {
                        pause();
                    } else {
                        play();
                        handler.sendEmptyMessage(UPDATE_UI);
                    }
                }
                break;
            case R.id.iv_play_last:
                last();
                break;
            case R.id.iv_play_next:
                next();
                break;
            case R.id.iv_play_model:
                Toast.makeText(context, "切换播放模式", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_play_queue:
                Toast.makeText(context, "打开播放列表", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (b && musicController != null) {
            musicController.seekTo(i);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (!mediaPlayer.isLooping()) {
            next();
        }
    }
}