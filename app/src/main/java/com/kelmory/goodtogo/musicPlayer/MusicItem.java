package com.kelmory.goodtogo.musicPlayer;

import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

// Implementing parcelable to make this class feasible to be transmitted by intent;
class MusicItem implements Parcelable {
    private String mName;
    private String mPath;
    private int mDuration;

    MusicItem(String path, String name) {
        mPath = path;
        mName = name;

        // Get duration of a music
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mPath);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        mDuration = Integer.parseInt(duration) / 1000;
    }

    private MusicItem(Parcel in) {
        mName = in.readString();
        mPath = in.readString();
        mDuration = in.readInt();
    }

    public static final Creator<MusicItem> CREATOR = new Creator<MusicItem>() {
        @Override
        public MusicItem createFromParcel(Parcel in) {
            return new MusicItem(in);
        }

        @Override
        public MusicItem[] newArray(int size) {
            return new MusicItem[size];
        }
    };

    public void setDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    int getDuration() {
        return mDuration;
    }

    String getDurationStr(){
        return formatPlayTime(mDuration);
    }

    String getPath(){
        return mPath;
    }

    public String getName() {
        return mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mPath);
        dest.writeInt(mDuration);
    }

    static String formatPlayTime(int duration){
        int min = duration / 60;
        int sec = duration % 60;
        return String.format(Locale.ENGLISH, "%02d:%02d", min, sec);
    }
}
