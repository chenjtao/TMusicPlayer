package com.tao.tmusicplayer.database.entity;

public class Music {

    /**
     * 歌曲ID
     */
    private String id;
    /**
     * 歌曲文件的路径
     */
    private String filePath;
    /**
     * 歌曲显示名称
     */
    private String displayName;
    /**
     * 歌曲的名称
     */
    private String title;
    /**
     * 歌曲的歌手名
     */
    private String artist;
    /**
     * 歌曲的专辑名
     */
    private String album;
    /**
     * 歌曲的总播放时长
     */
    private int duration;
    /**
     * 歌曲的总播放时长
     */
    private String durationStr;
    /**
     * 歌曲文件的大小
     */
    private int size;
    /**
     * 歌曲文件的大小
     */
    private String sizeStr;
    /**
     * 歌曲文件的类型
     */
    private String mimeType;

    public Music() {
    }

    public Music(String id, String filePath, String displayName, String title, String artist, String album, int duration, String durationStr, int size, String sizeStr, String mimeType) {
        this.id = id;
        this.filePath = filePath;
        this.displayName = displayName;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.durationStr = durationStr;
        this.size = size;
        this.sizeStr = sizeStr;
        this.mimeType = mimeType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDurationStr() {
        return durationStr;
    }

    public void setDurationStr(String durationStr) {
        this.durationStr = durationStr;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSizeStr() {
        return sizeStr;
    }

    public void setSizeStr(String sizeStr) {
        this.sizeStr = sizeStr;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
