package com.melodify.music.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.melodify.music.MyApplication;
import com.melodify.music.R;
import com.melodify.music.activity.AddSongActivity;
import com.melodify.music.adapter.AdminSongAdapter;
import com.melodify.music.constant.Constant;
import com.melodify.music.constant.GlobalFunction;
import com.melodify.music.databinding.FragmentAdminHomeBinding;
import com.melodify.music.listener.IOnManagerSongListener;
import com.melodify.music.model.Song;
import com.melodify.music.utils.StringUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private FragmentAdminHomeBinding mFragmentAdminHomeBinding;
    private List<Song> mListSong;
    private AdminSongAdapter mAdminSongAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentAdminHomeBinding = FragmentAdminHomeBinding.inflate(inflater, container, false);

        initView();
        initListener();
        getListSong("");
        return mFragmentAdminHomeBinding.getRoot();
    }

    private void initView() {
        if (getActivity() == null) return;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentAdminHomeBinding.rcvSong.setLayoutManager(linearLayoutManager);
        mListSong = new ArrayList<>();
        mAdminSongAdapter = new AdminSongAdapter(mListSong, new IOnManagerSongListener() {
            @Override
            public void onClickUpdateSong(Song song) {
                onClickEditSong(song);
            }

            @Override
            public void onClickDeleteSong(Song song) {
                deleteSongItem(song);
            }
        });
        mFragmentAdminHomeBinding.rcvSong.setAdapter(mAdminSongAdapter);
    }

    private void initListener() {
        mFragmentAdminHomeBinding.btnAddSong.setOnClickListener(v -> onClickAddSong());

        mFragmentAdminHomeBinding.imgSearch.setOnClickListener(view1 -> searchSong());

        mFragmentAdminHomeBinding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchSong();
                return true;
            }
            return false;
        });

        mFragmentAdminHomeBinding.edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    searchSong();
                }
            }
        });
    }

    private void onClickAddSong() {
        GlobalFunction.startActivity(getActivity(), AddSongActivity.class);
    }

    private void onClickEditSong(Song song) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_SONG_OBJECT, song);
        GlobalFunction.startActivity(getActivity(), AddSongActivity.class, bundle);
    }

    private void deleteSongItem(Song song) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> {
                    if (getActivity() == null) {
                        return;
                    }
                    MyApplication.get(getActivity()).getSongsDatabaseReference()
                            .child(String.valueOf(song.getId())).removeValue((error, ref) ->
                            Toast.makeText(getActivity(),
                                    getString(R.string.msg_delete_movie_successfully),
                                    Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void searchSong() {
        String strKey = mFragmentAdminHomeBinding.edtSearchName.getText().toString().trim();
        if (mListSong != null) {
            mListSong.clear();
        } else {
            mListSong = new ArrayList<>();
        }
        getListSong(strKey);
        GlobalFunction.hideSoftKeyboard(getActivity());
    }

    public void getListSong(String keyword) {
        if (getActivity() == null) return;
        MyApplication.get(getActivity()).getSongsDatabaseReference()
                .addChildEventListener(new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        Song song = dataSnapshot.getValue(Song.class);
                        if (song == null || mListSong == null) return;
                        if (StringUtil.isEmpty(keyword)) {
                            mListSong.add(0, song);
                        } else {
                            if (GlobalFunction.getTextSearch(song.getTitle()).toLowerCase().trim()
                                    .contains(GlobalFunction.getTextSearch(keyword).toLowerCase().trim())) {
                                mListSong.add(0, song);
                            }
                        }
                        if (mAdminSongAdapter != null) mAdminSongAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        Song song = dataSnapshot.getValue(Song.class);
                        if (song == null || mListSong == null || mListSong.isEmpty()) return;
                        for (int i = 0; i < mListSong.size(); i++) {
                            if (song.getId() == mListSong.get(i).getId()) {
                                mListSong.set(i, song);
                                break;
                            }
                        }
                        if (mAdminSongAdapter != null) mAdminSongAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        Song song = dataSnapshot.getValue(Song.class);
                        if (song == null || mListSong == null || mListSong.isEmpty()) return;
                        for (Song songObject : mListSong) {
                            if (song.getId() == songObject.getId()) {
                                mListSong.remove(songObject);
                                break;
                            }
                        }
                        if (mAdminSongAdapter != null) mAdminSongAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }
}
