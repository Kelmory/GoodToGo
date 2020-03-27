package com.kelmory.goodtogo.musicPlayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kelmory.goodtogo.R;

import java.util.ArrayList;

public class MusicListPopupActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_list);

        // Set this activity to a window-like form.
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = (int)(dm.heightPixels * 0.6);

        getWindow().setLayout(width, height);

        ArrayList<MusicItem> musics = getIntent().getParcelableArrayListExtra(getResources().getString(R.string.music_set));

        // Set up recycler view for listing musics.
        RecyclerView recyclerView = findViewById(R.id.recyclerview_music_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        MusicAdapter musicAdapter = new MusicAdapter();
        musicAdapter.setMusicItems(musics);
        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Send data with position.
                Intent data = new Intent();
                data.putExtra(getResources().getString(R.string.str_music_position), position);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        recyclerView.setAdapter(musicAdapter);

        musicAdapter.notifyDataSetChanged();
    }
}

class MusicAdapter extends RecyclerView.Adapter {

    private ArrayList<MusicItem> musicItems;
    private OnItemClickListener mListener;

    public static class MusicViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewFileName;
        private TextView textViewMusicDuration;

        MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFileName = (TextView)itemView.findViewById(R.id.textview_list_file_name);
            textViewMusicDuration = (TextView)itemView.findViewById(R.id.textview_list_duration);
        }

        void bind(final int position,
                  final OnItemClickListener mListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(position);
                }
            });
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
        ((MusicViewHolder) holder).bind(position, mListener);
    }

    @Override
    public int getItemCount() {
        if(musicItems == null)
            return 0;
        else
            return musicItems.size();
    }

    void setMusicItems(ArrayList<MusicItem> musicItems) {
        this.musicItems = musicItems;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

}
