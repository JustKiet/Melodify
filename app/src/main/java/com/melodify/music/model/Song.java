package com.melodify.music.model;

import java.io.Serializable;
import java.util.HashMap;

public class Song implements Serializable {
    private long id;
    private String title;
    private String image;
    private String url;
    private String artist;
    private boolean latest;
    private boolean featured;
    private int count;
    private boolean isPlaying;

    private HashMap<String, UserInfor> favorite;

    public Song() {
    }

    public Song(long id, String title, String artist, String image, String url, boolean latest, boolean featured) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.image = image;
        this.url = url;
        this.latest = latest;
        this.featured = featured;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public boolean isLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public HashMap<String, UserInfor> getFavorite() {
        return favorite;
    }

    public void setFavorite(HashMap<String, UserInfor> favorite) {
        this.favorite = favorite;
    }
}
