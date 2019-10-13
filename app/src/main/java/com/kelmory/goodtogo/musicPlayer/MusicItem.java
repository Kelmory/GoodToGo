package com.kelmory.goodtogo.musicPlayer;

import android.content.ContentResolver;

import java.io.FileDescriptor;
import java.util.Locale;

class MusicItem {
    private String mName;
    private String mPath;
    private int mDuration;

    public MusicItem(String path, String name) {
        mPath = path;
        mName = name;
    }

    public void setDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public int getDuration() {
        return mDuration;
    }

    public String getDurationStr(){
        int min = mDuration / 60;
        int sec = mDuration % 60;
        return String.format(Locale.ENGLISH, "%d/%d", min, sec);
    }

    public String getPath(){
        return mPath;
    }

    public String getName() {
        return mName;
    }
}
