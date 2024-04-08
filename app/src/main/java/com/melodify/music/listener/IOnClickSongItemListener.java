package com.melodify.music.listener;

import com.melodify.music.model.Song;

public interface IOnClickSongItemListener {
    void onClickItemSong(Song song);
    void onClickFavoriteSong(Song song, boolean favorite);
}
