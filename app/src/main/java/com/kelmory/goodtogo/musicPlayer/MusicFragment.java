package com.kelmory.goodtogo.musicPlayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.kelmory.goodtogo.R;

import java.io.File;
import java.util.ArrayList;


public class MusicFragment extends Fragment {

    private ArrayList<MusicItem> musics;



    private MusicItem musicPlaying;
    private MediaPlayer mediaPlayer;

    private ImageButton imageButtonPlay;

    public MusicFragment() {
        // Required empty public constructor
    }


    public static MusicFragment newInstance() {
        MusicFragment fragment = new MusicFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music, container, false);
    }


    public void initMusicInformation() {
        if(musics == null){
            return ;
        }
        else {
            musicPlaying = musics.get(0);
            File musicFileToPlay = new File(musicPlaying.getPath());
            mediaPlayer = MediaPlayer.create(getContext(), Uri.fromFile(musicFileToPlay));
            mediaPlayer.seekTo(0);
            musicPlaying.setDuration(mediaPlayer.getDuration());
        }
    }

    public void initButtons(View view){
        ImageButton imageButtonList = (ImageButton) view.findViewById(R.id.button_music_list);
        imageButtonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        MusicListPopupActivity.class);
                intent.putExtra(getString(R.string.music_set), musics);
                startActivity(intent);
            }
        });

        ImageButton imageButtonPrev = (ImageButton) view.findViewById(R.id.button_play_previous);
        imageButtonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "prev", Toast.LENGTH_LONG).show();
            }
        });


        ImageButton imageButtonNext = (ImageButton) view.findViewById(R.id.button_play_next);
        imageButtonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "next", Toast.LENGTH_LONG).show();
            }
        });

        imageButtonPlay = (ImageButton)view.findViewById(R.id.button_play_pause);
        imageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                    imageButtonPlay.setImageResource(R.drawable.ic_pause_black_36dp);
                }
                else{
                    mediaPlayer.stop();
                    imageButtonPlay.setImageResource(R.drawable.ic_play_arrow_black_36dp);
                }
            }
        });

        SeekBar seekBarProgress = (SeekBar) view.findViewById(R.id.seekbar_progress);
        seekBarProgress.setMax(musicPlaying.getDuration());
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
