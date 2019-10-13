package com.kelmory.goodtogo.musicPlayer;

import android.app.Activity;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kelmory.goodtogo.R;

import java.util.ArrayList;

public class MusicListPopupActivity extends Activity {

    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_list);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = (int)(dm.heightPixels * 0.6);

        getWindow().setLayout(width, height);

        ArrayList<MusicItem> musics = (ArrayList<MusicItem>)getIntent()
                .getSerializableExtra(getResources().getString(R.string.music_set));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_music_list);
        musicAdapter = new MusicAdapter();
        musicAdapter.setMusicItems(musics);
        recyclerView.setAdapter(musicAdapter);
    }
}

class MusicAdapter extends RecyclerView.Adapter {

    private ArrayList<MusicItem> musicItems;

    public static class MusicViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewFileName;
        private TextView textViewMusicDuration;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFileName = (TextView)itemView.findViewById(R.id.textview_list_file_name);
            textViewMusicDuration = (TextView)itemView.findViewById(R.id.textview_list_duration);
        }
    }

    @NonNull
    @Override
    public MusicAdapter.MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CardView view =(CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_single_item, parent, false);

        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MusicItem item = musicItems.get(position);
        ((MusicViewHolder) holder).textViewFileName.setText(item.getName());
        ((MusicViewHolder) holder).textViewMusicDuration.setText(item.getDurationStr());
    }

    @Override
    public int getItemCount() {
        return musicItems.size();
    }

    public void setMusicItems(ArrayList<MusicItem> musicItems) {
        this.musicItems = musicItems;
    }
}
