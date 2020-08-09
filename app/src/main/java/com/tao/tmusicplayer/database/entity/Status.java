package com.tao.tmusicplayer.database.entity;

public class Status {
    //唯一标识
    private String id;
    //播放模式
    private int model;
    //最后播放音乐id
    private String lastMusicId;
    //最后播放音乐截至时间
    private int lastPosition;

    public Status() {
    }

    public Status(String id, int model, String lastMusicId, int lastPosition) {
        this.id = id;
        this.model = model;
        this.lastMusicId = lastMusicId;
        this.lastPosition = lastPosition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public String getLastMusicId() {
        return lastMusicId;
    }

    public void setLastMusicId(String lastMusicId) {
        this.lastMusicId = lastMusicId;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }
}
