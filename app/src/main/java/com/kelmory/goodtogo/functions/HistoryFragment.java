package com.kelmory.goodtogo.functions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kelmory.goodtogo.R;


public class HistoryFragment extends Fragment {

    private static final String TAG = HistoryFragment.class.getSimpleName();

    private HistoryAdapter historyAdapter;
    private Context mContext;

    private TextView textViewWeeklyPace;
    private TextView textViewWeeklyDist;

    static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historyAdapter = new HistoryAdapter(mContext);
        Log.d(TAG, "Fragment created.");
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.view_history, container, false);

        // Initialize with text views inflated with data.
        textViewWeeklyPace = root.findViewById(R.id.textview_report_pace);
        textViewWeeklyDist = root.findViewById(R.id.textview_report_dist);

        textViewWeeklyPace.setText(historyAdapter.getWeeklyPace());
        textViewWeeklyDist.setText(historyAdapter.getWeeklyDist());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerview_history);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(historyAdapter);

        Button buttonDeleteAll = root.findViewById(R.id.button_report_reset);
        buttonDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Delete this record?")
                        .setMessage("Record will lost forever, continue?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Clear data and update view.
                                historyAdapter.clear();
                                historyAdapter.notifyDataSetChanged();
                                textViewWeeklyPace.setText(historyAdapter.getWeeklyPace());
                                textViewWeeklyDist.setText(historyAdapter.getWeeklyDist());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Update view for insurance.
                                textViewWeeklyPace.setText(historyAdapter.getWeeklyPace());
                                textViewWeeklyDist.setText(historyAdapter.getWeeklyDist());
                            }
                        });
                builder.create().show();
            }
        });

        return root;
    }

}