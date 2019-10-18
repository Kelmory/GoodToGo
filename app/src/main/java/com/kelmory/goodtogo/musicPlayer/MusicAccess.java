package com.kelmory.goodtogo.musicPlayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

class MusicAccess {

    public interface OnScanCompleteListener{
        void onScanComplete(ArrayList<MusicItem> list);
    }

    static void getMusic(final ContentResolver resolver,
                         final OnScanCompleteListener listener){
        try {
            new AsyncTask<Void, Void, Void>() {

                OnScanCompleteListener mListener;

                @Override
                protected Void doInBackground(Void... voids) {
                    // Set Uri to be external content uri
                    Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    // Init a cursor with a query
                    Cursor cursor = resolver.query(collection,
                            null,
                            null,
                            null,
                            null);
                    // Warn and return if query cursor is null
                    if(cursor == null){
                        Log.w("MusicAccess", "No music found.");
                        return null;
                    }

                    // Else iterate cursor to record paths
                    ArrayList<MusicItem> musics = new ArrayList<>();

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

                    mListener = listener;
                    mListener.onScanComplete(musics);
                    return null;
                }
            }.execute();
        } catch (Exception e){
            Log.e("MusicAccess", Arrays.toString(e.getStackTrace()));
        }
    }
}

