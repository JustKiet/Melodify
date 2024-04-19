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
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;
import android.graphics.Bitmap;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.melodify.music.R;
import com.melodify.music.constant.Constant;
import com.melodify.music.constant.GlobalFunction;
import com.melodify.music.databinding.FragmentPlaySongBinding;
import com.melodify.music.model.Song;
import com.melodify.music.service.MusicService;
import com.melodify.music.utils.AppUtil;
import com.melodify.music.utils.GlideUtils;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@SuppressLint("NonConstantResourceId")
public class PlaySongFragment extends Fragment implements View.OnClickListener {

    private FragmentPlaySongBinding mFragmentPlaySongBinding;
    private Timer mTimer;
    private int mAction;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAction = intent.getIntExtra(Constant.MUSIC_ACTION, 0);
            handleMusicAction();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentPlaySongBinding = FragmentPlaySongBinding.inflate(inflater, container, false);

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,
                    new IntentFilter(Constant.CHANGE_LISTENER));
        }
        initControl();
        showInforSong();
        updateStatusShuffleButton();
        updateStatusRepeatButton();
        mAction = MusicService.mAction;
        handleMusicAction();

        return mFragmentPlaySongBinding.getRoot();
    }

    private void initControl() {
        mTimer = new Timer();

        mFragmentPlaySongBinding.imgShuffle.setOnClickListener(this);
        mFragmentPlaySongBinding.imgRepeat.setOnClickListener(this);
        mFragmentPlaySongBinding.imgPrevious.setOnClickListener(this);
        mFragmentPlaySongBinding.imgPlay.setOnClickListener(this);
        mFragmentPlaySongBinding.imgNext.setOnClickListener(this);
        mFragmentPlaySongBinding.imgDownload.setOnClickListener(this);
        mFragmentPlaySongBinding.imgShare.setOnClickListener(this);


        mFragmentPlaySongBinding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicService.mPlayer.seekTo(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });
    }

    private void showInforSong() {
        if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying.isEmpty()) {
            return;
        }
        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
        mFragmentPlaySongBinding.tvSongName.setText(currentSong.getTitle());
        mFragmentPlaySongBinding.tvArtist.setText(currentSong.getArtist());
        GlideUtils.loadUrl(currentSong.getImage(), mFragmentPlaySongBinding.imgSong);
    }

    private void handleMusicAction() {
        if (Constant.CANCEL_NOTIFICATION == mAction) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return;
        }
        switch (mAction) {
            case Constant.PREVIOUS:
            case Constant.NEXT:
                stopAnimationPlayMusic();
                showInforSong();
                break;

            case Constant.PLAY:
                showInforSong();
                if (MusicService.isPlaying) {
                    startAnimationPlayMusic();
                }
                showSeekBar();
                showStatusButtonPlay();
                break;

            case Constant.PAUSE:
                stopAnimationPlayMusic();
                showSeekBar();
                showStatusButtonPlay();
                break;

            case Constant.RESUME:
                startAnimationPlayMusic();
                showSeekBar();
                showStatusButtonPlay();
                break;
        }
    }

    private void startAnimationPlayMusic() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mFragmentPlaySongBinding.imgSong.animate().rotationBy(360).withEndAction(this).setDuration(15000)
                        .setInterpolator(new LinearInterpolator()).start();
            }
        };
        mFragmentPlaySongBinding.imgSong.animate().rotationBy(360).withEndAction(runnable).setDuration(15000)
                .setInterpolator(new LinearInterpolator()).start();
    }

    private void stopAnimationPlayMusic() {
        mFragmentPlaySongBinding.imgSong.animate().cancel();
    }

    public void showSeekBar() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(() -> {
                    if (MusicService.mPlayer == null) {
                        return;
                    }
                    mFragmentPlaySongBinding.tvTimeCurrent.setText(AppUtil.getTime(MusicService.mPlayer.getCurrentPosition()));
                    mFragmentPlaySongBinding.tvTimeMax.setText(AppUtil.getTime(MusicService.mLengthSong));
                    mFragmentPlaySongBinding.seekbar.setMax(MusicService.mLengthSong);
                    mFragmentPlaySongBinding.seekbar.setProgress(MusicService.mPlayer.getCurrentPosition());
                });
            }
        }, 0, 1000);
    }

    private void showStatusButtonPlay() {
        if (MusicService.isPlaying) {
            mFragmentPlaySongBinding.imgPlay.setImageResource(R.drawable.ic_pause_black);
        } else {
            mFragmentPlaySongBinding.imgPlay.setImageResource(R.drawable.ic_play_black);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_previous:
                clickOnPrevButton();
                break;

            case R.id.img_play:
                clickOnPlayButton();
                break;

            case R.id.img_next:
                clickOnNextButton();
                break;

            case R.id.img_shuffle:
                clickOnShuffleButton();
                break;

            case R.id.img_repeat:
                clickOnRepeatButton();
                break;

            case R.id.img_share:
                shareSong();
                break;

            case R.id.img_download:
                showDownloadDialog();
                break;

            default:
                break;
        }
    }

    private void clickOnShuffleButton() {
        if (!MusicService.isShuffle) {
            MusicService.isShuffle = true;
            MusicService.isRepeat = false;
        } else {
            MusicService.isShuffle = false;
        }
        updateStatusShuffleButton();
        updateStatusRepeatButton();
    }

    private void clickOnRepeatButton() {
        if (!MusicService.isRepeat) {
            MusicService.isRepeat = true;
            MusicService.isShuffle = false;
        } else {
            MusicService.isRepeat = false;
        }
        updateStatusShuffleButton();
        updateStatusRepeatButton();
    }

    private void updateStatusShuffleButton() {
        if (MusicService.isShuffle) {
            mFragmentPlaySongBinding.imgShuffle.setImageResource(R.drawable.ic_shuffle_enable);
        } else {
            mFragmentPlaySongBinding.imgShuffle.setImageResource(R.drawable.ic_shuffle_disable);
        }
    }

    private void updateStatusRepeatButton() {
        if (MusicService.isRepeat) {
            mFragmentPlaySongBinding.imgRepeat.setImageResource(R.drawable.ic_repeat_one_enable);
        } else {
            mFragmentPlaySongBinding.imgRepeat.setImageResource(R.drawable.ic_repeat_disable);
        }
    }

    private void clickOnPrevButton() {
        GlobalFunction.startMusicService(getActivity(), Constant.PREVIOUS, MusicService.mSongPosition);
    }

    private void clickOnNextButton() {
        GlobalFunction.startMusicService(getActivity(), Constant.NEXT, MusicService.mSongPosition);
    }

    private void clickOnPlayButton() {
        if (MusicService.isPlaying) {
            GlobalFunction.startMusicService(getActivity(), Constant.PAUSE, MusicService.mSongPosition);
        } else {
            GlobalFunction.startMusicService(getActivity(), Constant.RESUME, MusicService.mSongPosition);
        }
    }

    private void shareSong() {
        if (getActivity() == null) return;

        Bitmap screenshot = AppUtil.takeScreenshot(getActivity());

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");

        // Sử dụng getActivity() để lấy context của activity chứa fragment
        String sharingText = getActivity().getString(R.string.sharing);
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharingText);

        shareIntent.putExtra(Intent.EXTRA_STREAM, AppUtil.getImageUri(getActivity(), screenshot));

        startActivity(Intent.createChooser(shareIntent, "Share song via"));
    }

    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.download_modal)
                .setPositiveButton(R.string.download_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        downloadSong();
                    }
                })
                .setNegativeButton(R.string.download_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
    }

    private String getUrlFromJSON(String title) {
        String json;
        try {
            InputStream is = getResources().openRawResource(R.raw.melodifydata);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);

            JSONObject jsonObject = new JSONObject(json);
            JSONArray songsArray = jsonObject.getJSONArray("songs");
            for (int i = 0; i < songsArray.length(); i++) {
                JSONObject songObject = songsArray.optJSONObject(i);
                if (songObject != null) {
                    String jsonTitle = songObject.optString("title");
                    if (jsonTitle != null && jsonTitle.equals(title)) {
                        return songObject.optString("url");
                    }
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }



    private void downloadSong() {
        String title = MusicService.mListSongPlaying.get(MusicService.mSongPosition).getTitle();
        String url = getUrlFromJSON(title);

        if (url != null) {

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(title);
            request.setDescription("Downloading " + title);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title + ".mp3");

            DownloadManager downloadManager = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);

            if (downloadManager != null) {
                downloadManager.enqueue(request);
            } else {
            }
        } else {
        }
    }
}