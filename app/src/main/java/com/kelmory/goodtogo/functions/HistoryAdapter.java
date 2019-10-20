package com.kelmory.goodtogo.functions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kelmory.goodtogo.R;
import com.kelmory.goodtogo.utils.DatabaseManipulation;
import com.kelmory.goodtogo.utils.persistence.RunItemDB;
import com.kelmory.goodtogo.utils.persistence.RunTableItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter {
    private final static String TAG = HistoryAdapter.class.getSimpleName();

    private final Context mContext;
    private ArrayList<RunTableItem> runTableItems;
    private ArrayList<RunTableItem> runTableWeekly;

    HistoryAdapter(Context context){
        mContext = context;
        runTableItems = null;
        RunItemDB db = RunItemDB.getDatabase(context);
        DatabaseManipulation.retrieveData(db);
        DatabaseManipulation.setListener(new DatabaseManipulation.OnDataRetrievedListener() {
            @Override
            public void onDataRetrieved(ArrayList<RunTableItem> runs) {
                runTableItems = runs;

                Log.d(TAG, "Num of run items: " + runTableItems.size());

                HistoryAdapter.this.notifyDataSetChanged();
            }
        });

        DatabaseManipulation.getWeeklyItems(db);
        DatabaseManipulation.setWeeklyRetrievedListener(
                new DatabaseManipulation.OnWeeklyRetrievedListener() {
                    @Override
                    public void onWeeklyRetrieved(ArrayList<RunTableItem> runs) {
                        runTableWeekly = runs;
                    }
                });
    }

    public void clear() {
        runTableItems.clear();
        RunItemDB db = RunItemDB.getDatabase(mContext);
        DatabaseManipulation.deleteAll(db);
    }

    public String getWeeklyDist() {
        if(runTableWeekly == null || runTableWeekly.isEmpty())
            return "----";
        else{
            double dist = 0.0;
            for(RunTableItem item : runTableWeekly){
                dist += item.getDistance();
            }
            String unit = dist > 1000 ? "km" : "m";
            dist = dist > 1000 ? dist / 1000 : dist;
            return String.format(Locale.ENGLISH,
                    "%.2f %s", dist, unit);
        }

    }

    public String getWeeklyPace() {
        if(runTableWeekly == null || runTableWeekly.isEmpty())
            return "----";
        else{
            double dist = 0.0;
            double time = 0.0;
            for(RunTableItem item : runTableWeekly){
                dist += item.getDistance();
                time += item.getRunTime();
            }

            double pace = (time / 60)/ (dist / 1000);

            return String.format(Locale.ENGLISH,
                    "%.2f min/km", pace);
        }
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder{
        TextView textViewRunStartTime;
        TextView textViewRunDist;
        TextView textViewRunTime;
        TextView textViewRunSpeed;
        TextView textViewRunPace;
        ImageButton imageButtonDelete;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRunStartTime = itemView.findViewById(R.id.textview_history_single_start);
            textViewRunDist = itemView.findViewById(R.id.textview_history_single_dist);
            textViewRunTime = itemView.findViewById(R.id.textview_history_single_time);
            textViewRunPace = itemView.findViewById(R.id.textview_history_single_pace);
            textViewRunSpeed = itemView.findViewById(R.id.textview_history_single_speed);
            imageButtonDelete = itemView.findViewById(R.id.imagebutton_delete);
        }
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_history_single_run, parent, false);

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        RunTableItem runItem = runTableItems.get(position);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = format.format(new Date(runItem.getRunStartTime()));

        double dist = runItem.getDistance();
        String unit = dist > 1000 ? "km" : "m";
        String distStr = dist > 1000 ?
                String.valueOf((int)(dist / 1000)) :
                String.valueOf((int)dist);
        distStr = distStr + unit;

        int time = runItem.getRunTime();
        String timeStr = String.format(Locale.ENGLISH,
                "%02d\' %02d\'\'", time / 60, time % 60);

        double speed = runItem.getRunSpeed();
        String speedStr = String.format(Locale.ENGLISH,
                "%.2f m/s", speed);

        double pace = 1 / (runItem.getRunSpeed() / 1000 * 60);
        String paceStr = String.format(Locale.ENGLISH,
                "%.2f min/km", pace);

        ((HistoryViewHolder) holder).textViewRunStartTime.setText(dateStr);
        ((HistoryViewHolder) holder).textViewRunDist.setText(distStr);
        ((HistoryViewHolder) holder).textViewRunTime.setText(timeStr);
        ((HistoryViewHolder) holder).textViewRunSpeed.setText(speedStr);
        ((HistoryViewHolder) holder).textViewRunPace.setText(paceStr);

        ((HistoryViewHolder) holder).imageButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Delete this record?")
                        .setMessage("Record will lost forever, continue?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                RunItemDB db = RunItemDB.getDatabase(mContext);
                                DatabaseManipulation.deleteSingleData(
                                        db, runTableItems.get(position));

                                runTableItems.remove(position);
                                HistoryAdapter.this.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ;
                            }
                        });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (runTableItems == null) ? 0 : runTableItems.size();
    }
}