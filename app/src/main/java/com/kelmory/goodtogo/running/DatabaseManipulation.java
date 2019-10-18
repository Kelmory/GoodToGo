package com.kelmory.goodtogo.running;

import android.os.AsyncTask;
import android.util.Log;

import com.kelmory.goodtogo.running.persistence.RunItemDAO;
import com.kelmory.goodtogo.running.persistence.RunItemDB;
import com.kelmory.goodtogo.running.persistence.RunTableItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseManipulation{

    private static final String TAG = DatabaseManipulation.class.getSimpleName();

    interface OnDataRetrievedListener{
        void onDataRetrieved(ArrayList<RunTableItem> runs);
    }

    private static OnDataRetrievedListener listener;

    public static void setListener(OnDataRetrievedListener listener) {
        DatabaseManipulation.listener = listener;
    }

    static void retrieveData(final RunItemDB db){
        try{
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    final RunItemDAO dao = db.runItemDAO();
                    List<RunTableItem> runTableItems = dao.listAll();
                    if(runTableItems == null || runTableItems.isEmpty())
                        return null;
                    else{
                        ArrayList<RunTableItem> runs = new ArrayList<>(runTableItems);
                        listener.onDataRetrieved(runs);
                        return null;
                    }
                }
            }.execute();
        }catch (Exception e){
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
    }

    static void saveSingleData(final RunItemDB db, final RunTableItem item){
        try{
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    RunItemDAO dao = db.runItemDAO();
                    dao.insert(item);
                    return null;
                }
            }.execute();
        }catch (Exception e){
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
    }
}