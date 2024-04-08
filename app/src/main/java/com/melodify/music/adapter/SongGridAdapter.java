package com.melodify.music.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.melodify.music.R;
import com.melodify.music.constant.GlobalFunction;
import com.melodify.music.databinding.ItemSongGridBinding;
import com.melodify.music.listener.IOnClickSongItemListener;
import com.melodify.music.model.Song;
import com.melodify.music.utils.GlideUtils;

import java.util.List;

public class SongGridAdapter extends RecyclerView.Adapter<SongGridAdapter.SongGridViewHolder> {

    private final List<Song> mListSongs;
    public final IOnClickSongItemListener iOnClickSongItemListener;

    public SongGridAdapter(List<Song> mListSongs, IOnClickSongItemListener iOnClickSongItemListener) {
        this.mListSongs = mListSongs;
        this.iOnClickSongItemListener = iOnClickSongItemListener;
    }

    @NonNull
    @Override
    public SongGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSongGridBinding itemSongGridBinding = ItemSongGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SongGridViewHolder(itemSongGridBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SongGridViewHolder holder, int position) {
        Song song = mListSongs.get(position);
        if (song == null) {
            return;
        }
        GlideUtils.loadUrl(song.getImage(), holder.mItemSongGridBinding.imgSong);
        holder.mItemSongGridBinding.tvSongName.setText(song.getTitle());
        holder.mItemSongGridBinding.tvArtist.setText(song.getArtist());

        String strListen = song.getCount() > 1 ? "listens" : "listen";
        String strCountListen = song.getCount() + " " + strListen;
        holder.mItemSongGridBinding.tvCountListen.setText(strCountListen);

        boolean isFavorite = GlobalFunction.isFavoriteSong(song);
        if (isFavorite) {
            holder.mItemSongGridBinding.imgFavorite.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.mItemSongGridBinding.imgFavorite.setImageResource(R.drawable.ic_unfavorite);
        }

        holder.mItemSongGridBinding.layoutItem.setOnClickListener(v -> iOnClickSongItemListener.onClickItemSong(song));
        holder.mItemSongGridBinding.imgFavorite.setOnClickListener(v -> iOnClickSongItemListener.onClickFavoriteSong(song, !isFavorite));
    }

    @Override
    public int getItemCount() {
        return null == mListSongs ? 0 : mListSongs.size();
    }

    public static class SongGridViewHolder extends RecyclerView.ViewHolder {

        private final ItemSongGridBinding mItemSongGridBinding;

        public SongGridViewHolder(ItemSongGridBinding itemSongGridBinding) {
            super(itemSongGridBinding.getRoot());
            this.mItemSongGridBinding = itemSongGridBinding;
        }
    }
}
