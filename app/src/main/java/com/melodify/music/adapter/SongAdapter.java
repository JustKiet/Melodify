package com.melodify.music.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.melodify.music.R;
import com.melodify.music.constant.GlobalFunction;
import com.melodify.music.databinding.ItemSongBinding;
import com.melodify.music.listener.IOnClickSongItemListener;
import com.melodify.music.model.Song;
import com.melodify.music.utils.GlideUtils;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private final List<Song> mListSongs;
    public final IOnClickSongItemListener iOnClickSongItemListener;

    public SongAdapter(List<Song> mListSongs, IOnClickSongItemListener iOnClickSongItemListener) {
        this.mListSongs = mListSongs;
        this.iOnClickSongItemListener = iOnClickSongItemListener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSongBinding itemSongBinding = ItemSongBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SongViewHolder(itemSongBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = mListSongs.get(position);
        if (song == null) {
            return;
        }
        GlideUtils.loadUrl(song.getImage(), holder.mItemSongBinding.imgSong);
        holder.mItemSongBinding.tvSongName.setText(song.getTitle());
        holder.mItemSongBinding.tvArtist.setText(song.getArtist());

        String strListen = song.getCount() > 1 ? "listens" : "listen";
        String strCountListen = song.getCount() + " " + strListen;
        holder.mItemSongBinding.tvCountListen.setText(strCountListen);

        boolean isFavorite = GlobalFunction.isFavoriteSong(song);
        if (isFavorite) {
            holder.mItemSongBinding.imgFavorite.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.mItemSongBinding.imgFavorite.setImageResource(R.drawable.ic_unfavorite);
        }

        holder.mItemSongBinding.layoutItem.setOnClickListener(v -> iOnClickSongItemListener.onClickItemSong(song));
        holder.mItemSongBinding.imgFavorite.setOnClickListener(v -> iOnClickSongItemListener.onClickFavoriteSong(song, !isFavorite));
    }

    @Override
    public int getItemCount() {
        return null == mListSongs ? 0 : mListSongs.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {

        private final ItemSongBinding mItemSongBinding;

        public SongViewHolder(ItemSongBinding itemSongBinding) {
            super(itemSongBinding.getRoot());
            this.mItemSongBinding = itemSongBinding;
        }
    }
}
