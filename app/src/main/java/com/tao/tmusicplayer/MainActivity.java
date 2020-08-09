package com.tao.tmusicplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tao.tmusicplayer.adapter.MusicAdapter;
import com.tao.tmusicplayer.database.dao.MusicDao;
import com.tao.tmusicplayer.database.entity.Music;
import com.tao.tmusicplayer.service.MusicService;
import com.tao.tmusicplayer.widget.CircleProgressView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        MediaPlayer.OnCompletionListener {

    private ListView list_music;
    private ImageView iv_refresh;
    private ImageView iv_music_img;
    private ImageView iv_music_play;
    private ImageView iv_music_next;
    private TextView tv_music_title;
    private RelativeLayout rl_music_player;
    private CircleProgressView circleProgressView;

    private Context context = this;
    private MusicDao musicDao;
    private List<Music> musicList;
    private MusicAdapter musicAdapter;
    private Intent musicIntent;
    private ServiceConnection musicConnection;
    private MusicService.MusicController musicController;

    private boolean looping = false;
    private int playIndex = -1;
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
        setContentView(R.layout.activity_main);

        musicDao = new MusicDao(context);
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
        list_music = findViewById(R.id.list_main_music);
        iv_refresh = findViewById(R.id.iv_main_refresh);
        iv_music_img = findViewById(R.id.iv_main_music_img);
        iv_music_play = findViewById(R.id.iv_main_music_play);
        iv_music_next = findViewById(R.id.iv_main_music_next);
        tv_music_title = findViewById(R.id.tv_main_music_title);
        rl_music_player = findViewById(R.id.rl_main_music_player);
        circleProgressView = findViewById(R.id.cpv_music);

        musicList = musicDao.getAllLocalMusic();
        musicAdapter = new MusicAdapter(context, musicList);
        list_music.setAdapter(musicAdapter);
    }

    private void setListener() {
        iv_refresh.setOnClickListener(this);
        iv_music_play.setOnClickListener(this);
        iv_music_next.setOnClickListener(this);
        rl_music_player.setOnClickListener(this);
        list_music.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getId() == R.id.list_main_music) {
                    if (position == playIndex) {
                        if (!musicController.getMediaPlayer().isPlaying()) {
                            play();
                            handler.sendEmptyMessage(UPDATE_UI);
                        }
                    } else {
                        selectMusic(position);
                        play();
                        handler.sendEmptyMessage(UPDATE_UI);
                    }
                }
            }
        });
        musicAdapter.setOnItemDeleteClickListener(new MusicAdapter.onItemDeleteListener() {
            @Override
            public void onDeleteClick(int i) {
                musicDao.delLocalMusicById(musicList.get(i).getId());
                musicList.remove(i);
                musicController.setMusicList(musicList);
                musicAdapter.notifyDataSetChanged();
            }
        });
    }

    private void startMusicService() {
        //启动后台音乐服务
        musicIntent = new Intent(context, MusicService.class);
        musicConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicController = (MusicService.MusicController) service;
                if (musicList != null && !musicList.isEmpty()) {
                    musicController.setMusicList(musicList);
                }
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
        musicController.getMediaPlayer().setOnCompletionListener(MainActivity.this);
        tv_music_title.setText(String.format("%s-%s", musicList.get(playIndex).getTitle(), musicList.get(playIndex).getArtist()));
        circleProgressView.setMax(musicList.get(playIndex).getDuration());
        circleProgressView.setProgress(0);
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

    private void next() {
        selectMusic(playIndex + 1);
        play();
        handler.sendEmptyMessage(UPDATE_UI);
    }

    private void updateUI() {
        if (musicController.getMediaPlayer().isPlaying()) {
            iv_music_play.setImageResource(R.drawable.ic_pause);
        } else {
            iv_music_play.setImageResource(R.drawable.ic_play);
        }
        if (musicController != null && musicController.getMediaPlayer() != null && musicController.getMediaPlayer().isPlaying()) {
            tv_music_title.setText(String.format("%s-%s", musicController.getMusicInfo().getTitle(), musicController.getMusicInfo().getArtist()));
            // 更新进度条及时间
            int playPosition = musicController.getCurrentPosition();
            circleProgressView.setMax(musicController.getDuration());
            circleProgressView.setProgress(playPosition);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_main_refresh:
                int hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    //没有权限，向用户请求权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                } else {
                    Toast.makeText(context, "正在搜索本地资源...", Toast.LENGTH_SHORT).show();
                    long count = musicController.refreshMusicData();
                    if (musicList != null && musicList.size() > 0) {
                        musicList.clear();
                    }
                    if (musicDao.getAllLocalMusic().size() > 0) {
                        musicList.addAll(musicDao.getAllLocalMusic());
                        musicAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(context, "搜索完成,添加" + count + "首歌曲", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_main_music_play:
                if (musicList == null || musicList.isEmpty()) break;
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
            case R.id.iv_main_music_next:
                next();
                break;
            case R.id.rl_main_music_player:
                Intent play = new Intent(context, PlayActivity.class);
                startActivity(play);
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (!mediaPlayer.isLooping()) {
            next();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户同意，执行操作
                iv_refresh.performClick();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            Log.d("MainActivity", "onActivityResult: ");
            bindService(musicIntent, musicConnection, BIND_AUTO_CREATE);
        }
    }
}