package com.melodify.music.listener;

import com.melodify.music.model.Song;

public interface IOnManagerSongListener {
    void onClickUpdateSong(Song song);
    void onClickDeleteSong(Song song);
}
