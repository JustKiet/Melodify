package com.melodify.music.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.melodify.music.MyApplication;
import com.melodify.music.R;
import com.melodify.music.constant.Constant;
import com.melodify.music.constant.GlobalFunction;
import com.melodify.music.databinding.ActivityAddSongBinding;
import com.melodify.music.model.Song;
import com.melodify.music.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class AddSongActivity extends BaseActivity {

    private ActivityAddSongBinding mActivityAddSongBinding;
    private boolean isUpdate;
    private Song mSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityAddSongBinding = ActivityAddSongBinding.inflate(getLayoutInflater());
        setContentView(mActivityAddSongBinding.getRoot());

        getDataIntent();
        initToolbar();
        initView();

        mActivityAddSongBinding.btnAddOrEdit.setOnClickListener(v -> addOrEditFood());
    }

    private void getDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            isUpdate = true;
            mSong = (Song) bundleReceived.get(Constant.KEY_INTENT_SONG_OBJECT);
        }
    }

    private void initToolbar() {
        mActivityAddSongBinding.toolbar.imgLeft.setImageResource(R.drawable.ic_back_white);
        mActivityAddSongBinding.toolbar.tvTitle.setText(R.string.label_add_song);
        mActivityAddSongBinding.toolbar.layoutPlayAll.setVisibility(View.GONE);
        mActivityAddSongBinding.toolbar.imgLeft.setOnClickListener(v -> onBackPressed());
    }

    private void initView() {
        if (isUpdate) {
            mActivityAddSongBinding.toolbar.tvTitle.setText(getString(R.string.label_update_song));
            mActivityAddSongBinding.btnAddOrEdit.setText(getString(R.string.action_edit));

            mActivityAddSongBinding.edtName.setText(mSong.getTitle());
            mActivityAddSongBinding.edtArtist.setText(mSong.getArtist());
            mActivityAddSongBinding.edtImage.setText(mSong.getImage());
            mActivityAddSongBinding.edtLink.setText(mSong.getUrl());
            mActivityAddSongBinding.chbFeatured.setChecked(mSong.isFeatured());
            mActivityAddSongBinding.chbLatest.setChecked(mSong.isLatest());
        } else {
            mActivityAddSongBinding.toolbar.tvTitle.setText(getString(R.string.label_add_song));
            mActivityAddSongBinding.btnAddOrEdit.setText(getString(R.string.action_add));
        }
    }

    private void addOrEditFood() {
        String strName = mActivityAddSongBinding.edtName.getText().toString().trim();
        String strArtist = mActivityAddSongBinding.edtArtist.getText().toString().trim();
        String strImage = mActivityAddSongBinding.edtImage.getText().toString().trim();
        String strLink = mActivityAddSongBinding.edtLink.getText().toString().trim();
        boolean isFeatured = mActivityAddSongBinding.chbFeatured.isChecked();
        boolean isLatest = mActivityAddSongBinding.chbLatest.isChecked();

        if (StringUtil.isEmpty(strName)) {
            Toast.makeText(this, getString(R.string.msg_name_song_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strArtist)) {
            Toast.makeText(this, getString(R.string.msg_artist_song_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strImage)) {
            Toast.makeText(this, getString(R.string.msg_image_song_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strLink)) {
            Toast.makeText(this, getString(R.string.msg_link_song_require), Toast.LENGTH_SHORT).show();
            return;
        }

        // Update song
        if (isUpdate) {
            showProgressDialog(true);
            Map<String, Object> map = new HashMap<>();
            map.put("title", strName);
            map.put("artist", strArtist);
            map.put("image", strImage);
            map.put("url", strLink);
            map.put("featured", isFeatured);
            map.put("latest", isLatest);


            MyApplication.get(this).getSongsDatabaseReference()
                    .child(String.valueOf(mSong.getId())).updateChildren(map, (error, ref) -> {
                showProgressDialog(false);
                Toast.makeText(AddSongActivity.this,
                        getString(R.string.msg_edit_song_success), Toast.LENGTH_SHORT).show();
                GlobalFunction.hideSoftKeyboard(this);
            });
            return;
        }

        // Add song
        showProgressDialog(true);
        long songId = System.currentTimeMillis();
        Song song = new Song(songId, strName, strArtist, strImage, strLink, isFeatured, isLatest);
        MyApplication.get(this).getSongsDatabaseReference()
                .child(String.valueOf(songId)).setValue(song, (error, ref) -> {
            showProgressDialog(false);
            mActivityAddSongBinding.edtName.setText("");
            mActivityAddSongBinding.edtArtist.setText("");
            mActivityAddSongBinding.edtImage.setText("");
            mActivityAddSongBinding.edtLink.setText("");
            mActivityAddSongBinding.chbFeatured.setChecked(false);
            mActivityAddSongBinding.chbLatest.setChecked(false);
            GlobalFunction.hideSoftKeyboard(this);
            Toast.makeText(this, getString(R.string.msg_add_song_success), Toast.LENGTH_SHORT).show();
        });
    }
}