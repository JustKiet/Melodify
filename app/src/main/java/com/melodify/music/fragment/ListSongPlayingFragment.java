package com.melodify.music.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.melodify.music.adapter.SongPlayingAdapter;
import com.melodify.music.constant.Constant;
import com.melodify.music.constant.GlobalFunction;
import com.melodify.music.databinding.FragmentListSongPlayingBinding;
import com.melodify.music.service.MusicService;

public class ListSongPlayingFragment extends Fragment {

    private FragmentListSongPlayingBinding mFragmentListSongPlayingBinding;
    private SongPlayingAdapter mSongPlayingAdapter;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateStatusListSongPlaying();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentListSongPlayingBinding = FragmentListSongPlayingBinding.inflate(inflater, container, false);

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,
                    new IntentFilter(Constant.CHANGE_LISTENER));
        }
        displayListSongPlaying();

        return mFragmentListSongPlayingBinding.getRoot();
    }

    private void displayListSongPlaying() {
        if (getActivity() == null || MusicService.mListSongPlaying == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentListSongPlayingBinding.rcvData.setLayoutManager(linearLayoutManager);

        mSongPlayingAdapter = new SongPlayingAdapter(MusicService.mListSongPlaying, this::clickItemSongPlaying);
        mFragmentListSongPlayingBinding.rcvData.setAdapter(mSongPlayingAdapter);

        updateStatusListSongPlaying();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateStatusListSongPlaying() {
        if (getActivity() == null || MusicService.mListSongPlaying == null || MusicService.mListSongPlaying.isEmpty()) {
            return;
        }
        for (int i = 0; i < MusicService.mListSongPlaying.size(); i++) {
            MusicService.mListSongPlaying.get(i).setPlaying(i == MusicService.mSongPosition);
        }
        mSongPlayingAdapter.notifyDataSetChanged();
    }

    private void clickItemSongPlaying(int position) {
        MusicService.isPlaying = false;
        GlobalFunction.startMusicService(getActivity(), Constant.PLAY, position);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
        }
    }
}
