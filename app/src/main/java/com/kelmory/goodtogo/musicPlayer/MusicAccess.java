package com.kelmory.goodtogo.musicPlayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;

public class MusicAccess {

    private static final String[] projection = {
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
    };

    public static void getMusic(final ArrayList<MusicItem> musics, final ContentResolver resolver){
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    // Set Uri to be external content uri
                    Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    // Init a cursor with a query
                    Cursor cursor = resolver.query(collection,
                            projection,
                            null,
                            null,
                            null);
                    // Warn and return if query cursor is null
                    if(cursor == null){
                        Log.w("MusicAccess", "No music found.");
                        return null;
                    }

                    // Else iterate cursor to record paths
                    while(cursor.moveToNext()){
                        String path = cursor.getString(
                                cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        String name = cursor.getString(
                                cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        MusicItem music = new MusicItem(path, name);
                        musics.add(music);
                    }
                    // Close cursor after use
                    cursor.close();

                    return null;
                }
            }.execute();
        } catch (Exception e){
            Log.e("MusicAccess", Arrays.toString(e.getStackTrace()));
        }
    }

}

