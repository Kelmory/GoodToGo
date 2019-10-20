package com.kelmory.goodtogo.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.kelmory.goodtogo.utils.persistence.RunItemDAO;
import com.kelmory.goodtogo.utils.persistence.RunItemDB;
import com.kelmory.goodtogo.utils.persistence.RunTableItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class DatabaseManipulation{

    private static final String TAG = DatabaseManipulation.class.getSimpleName();

    public interface OnDataRetrievedListener{
        void onDataRetrieved(ArrayList<RunTableItem> runs);
    }

    private static OnDataRetrievedListener listener;

    public static void setListener(OnDataRetrievedListener listener) {
        DatabaseManipulation.listener = listener;
    }

    public static void retrieveData(final RunItemDB db){
        try{
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    final RunItemDAO dao = db.runItemDAO();
                    List<RunTableItem> runTableItems = dao.listAll();

                    Log.d(TAG, "Retrieved data: " + runTableItems.size());

                    if(runTableItems.isEmpty()) {
                        listener.onDataRetrieved(new ArrayList<RunTableItem>());
                        return null;
                    }
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

    public static void deleteSingleData(final RunItemDB db, final RunTableItem item){
        try{
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    RunItemDAO dao = db.runItemDAO();
                    dao.delete(item);
                    return null;
                }
            }.execute();
        } catch (Exception e){
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
    }

    public static void deleteAll(final RunItemDB db) {
        try{
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    RunItemDAO dao = db.runItemDAO();
                    dao.deleteAll();
                    return null;
                }
            }.execute();
        } catch (Exception e){
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
    }

    public static void saveSingleData(final RunItemDB db, final RunTableItem item){
        try{
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    RunItemDAO dao = db.runItemDAO();
                    dao.insert(item);
                    Log.d(TAG, "Item saved: " + item.getDistance());
                    return null;
                }
            }.execute();
        }catch (Exception e){
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
    }

    public static void getWeeklyItems(final RunItemDB db) {
        try{
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    Calendar c = Calendar.getInstance(TimeZone.getDefault());
                    c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
                    long time = c.getTimeInMillis();
                    RunItemDAO dao = db.runItemDAO();
                    List<RunTableItem> list = dao.listRecent(time);

                    if(list.isEmpty()) {
                        weeklyRetrievedListener.onWeeklyRetrieved(new ArrayList<RunTableItem>());
                        return null;
                    }
                    else{
                        ArrayList<RunTableItem> runs = new ArrayList<>(list);
                        weeklyRetrievedListener.onWeeklyRetrieved(runs);
                        return null;
                    }
                }
            }.execute();
        } catch (Exception e){
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
    }

    public interface OnWeeklyRetrievedListener {
        void onWeeklyRetrieved(ArrayList<RunTableItem> runs);
    }

    private static OnWeeklyRetrievedListener weeklyRetrievedListener;

    public static void setWeeklyRetrievedListener(OnWeeklyRetrievedListener weeklyRetrievedListener) {
        DatabaseManipulation.weeklyRetrievedListener = weeklyRetrievedListener;
    }
}