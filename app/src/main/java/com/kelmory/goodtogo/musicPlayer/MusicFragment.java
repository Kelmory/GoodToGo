package com.kelmory.goodtogo.musicPlayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kelmory.goodtogo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static android.app.Activity.RESULT_OK;


public class MusicFragment extends Fragment {

    private static final String TAG = "MusicFragment";
    private static final int FIRST_MUSIC = 0;
    private static final int MUSIC_POSITION_REQUEST_CODE = 436;

    private ArrayList<MusicItem> musics;

    private int musicIndex;
    private MusicItem musicPlaying;
    private MediaPlayer mediaPlayer;

    private View mView;
    private TextView textViewProgress;
    private ImageButton imageButtonPlay;
    private SeekBar seekBarProgress;

    private Handler mSeekBarHandler = new Handler();
    private Runnable mSeekBarRunnable;

    public MusicFragment() {
        // Required empty public constructor
    }


    public static MusicFragment newInstance() {
        return new MusicFragment();
    }

    @Override
    public void onAttach(@NonNull final Context context) {
        musics = new ArrayList<>();
        MusicAccess.getMusic(context.getContentResolver(),
                new MusicAccess.OnScanCompleteListener() {
            @Override
            public void onScanComplete(ArrayList<MusicItem> list) {
                musics = list;
                initMusicInformation();
            }
        });

        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.view_music, container, false);
        // Record text view progress for updating progress
        textViewProgress = view.findViewById(R.id.textview_progress);
        //
        mView = view;

        initButtons(view);
        return view;
    }

    // Update names, set initial information.
    private void updateMusicViewInfo() {
        if(mView != null) {
            if (musicPlaying != null) {
                TextView name = mView.findViewById(R.id.textview_file_name);
                name.setText(musicPlaying.getName());

                textViewProgress = mView.findViewById(R.id.textview_progress);
                textViewProgress.setText(String.format("00:00/%s", musicPlaying.getDurationStr()));
            }
            seekBarProgress.setMax(mediaPlayer.getDuration() / 1000);
        }
    }


    private void initMusicInformation() {
        if(musics == null){
            Log.w(TAG, "ArrayList `musics` not initialized.");
        }
        else if(musics.isEmpty()){
            Log.d(TAG, "No music available.");
        }
        else {
            Log.d(TAG, "Musics loaded: " + musics.size());
            // If musics exist, set music player to prepare the first.
            setMusic(FIRST_MUSIC);
        }
    }

    private void initButtons(View view){
        ImageButton imageButtonList = view.findViewById(R.id.button_music_list);
        imageButtonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start a pop-up activity and get result position for a music list.
                Intent intent = new Intent(getActivity(),
                        MusicListPopupActivity.class);
                intent.putParcelableArrayListExtra(getString(R.string.music_set), musics);
                startActivityForResult(intent, MUSIC_POSITION_REQUEST_CODE);
            }
        });

        ImageButton imageButtonPrev = view.findViewById(R.id.button_play_previous);
        imageButtonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set music index in a circular way
                musicIndex = (musicIndex - 1) < 0 ? musics.size() - 1 : musicIndex - 1;
                setMusic(musicIndex);

                // Play music right after switch.
                mediaPlayer.start();

                // Update UI for hints.
                imageButtonPlay.setImageResource(R.drawable.ic_pause_black_36dp);
            }
        });


        ImageButton imageButtonNext = view.findViewById(R.id.button_play_next);
        imageButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set music index in another circular pattern.
                musicIndex = (musicIndex + 1) % musics.size();
                setMusic(musicIndex);
                mediaPlayer.start();
                imageButtonPlay.setImageResource(R.drawable.ic_pause_black_36dp);
            }
        });

        imageButtonPlay = view.findViewById(R.id.button_play_pause);
        imageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update media player status and UI.
                if(!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                    imageButtonPlay.setImageResource(R.drawable.ic_pause_black_36dp);
                }
                else{
                    mediaPlayer.pause();
                    imageButtonPlay.setImageResource(R.drawable.ic_play_arrow_black_36dp);
                }
            }
        });

        if(seekBarProgress == null) {
            seekBarProgress = view.findViewById(R.id.seekbar_progress);
            if (musicPlaying != null) {
                seekBarProgress.setMax(musicPlaying.getDuration());
            } else {
                seekBarProgress.setMax(0);
            }
            seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        mediaPlayer.seekTo(progress * 1000);
                        textViewProgress.setText(String.format("%s/%s",
                                MusicItem.formatPlayTime(progress),
                                musicPlaying.getDurationStr()));
                    }
                }
                // Empty methods but asked for implementation.
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });
        }
    }

    private void setMusic(int index){
        // Clear prior mediaPlayer
        if(mediaPlayer != null) {
            // Release MediaPlayer
            mediaPlayer.release();
        }
        // Reset music index;
        musicIndex = index;
        // Get music with position and information list
        musicPlaying = musics.get(index);
        File musicFileToPlay = new File(musicPlaying.getPath());

        // Set up media player.
        mediaPlayer = MediaPlayer.create(getContext(), Uri.fromFile(musicFileToPlay));
        mediaPlayer.seekTo(0);

        // Set listener for play completion.
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Release resources relevant to playing one.
                mp.release();
                mSeekBarHandler.removeCallbacks(mSeekBarRunnable);
                // Switch to next music.
                setMusic(musicIndex + 1);
                mediaPlayer.start();
            }
        });

        // Setup another runnable component for updating progress of music.
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int duration = mediaPlayer.getCurrentPosition() / 1000;

                    if(textViewProgress != null) {
                        textViewProgress.setText(String.format("%s/%s",
                                MusicItem.formatPlayTime(duration),
                                musicPlaying.getDurationStr()));
                    }
                    if(seekBarProgress != null) {
                        seekBarProgress.setProgress(duration);
                    }

                    mSeekBarHandler.postDelayed(this, 50);
                }
            });
        }
        else{
            Log.e(TAG, "Activity null when updating music info");
        }
        // Update information after another music set.
        updateMusicViewInfo();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Receive music position from pop-up list activity.
        if (requestCode == MUSIC_POSITION_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                try {
                    int position = data.getIntExtra(
                            getResources().getString(R.string.str_music_position), musicIndex);
                    if (position != musicIndex){
                        // Set music player for another music.
                        setMusic(position);
                        imageButtonPlay.setImageResource(R.drawable.ic_pause_black_36dp);
                        mediaPlayer.start();
                        updateMusicViewInfo();
                    }
                }catch (NullPointerException e){
                    Log.e(TAG, Arrays.toString(e.getStackTrace()));
                }
            }
        }
    }
}
