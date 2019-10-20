package com.kelmory.goodtogo.running;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.kelmory.goodtogo.utils.persistence.RunItemDB;
import com.kelmory.goodtogo.utils.persistence.RunTableItem;
import com.kelmory.goodtogo.utils.DatabaseManipulation;

import java.util.LinkedList;
import java.util.Locale;

public class RunningManager{
    private final static String TAG = RunningManager.class.getSimpleName();

    private static RunTableItem run = new RunTableItem();
    private static LinkedList<LatLng> route = new LinkedList<>();
    private static LatLng mLatLng;
    private static boolean started = false;

    static synchronized void startRunning(){
        run.setRunStartTime(System.currentTimeMillis());
        started = true;
        if(mLatLng != null)
            route.add(mLatLng);
    }

    static synchronized void stopRunning(Context context){
        started = false;
        // Calculate parameters first
        long startTime = run.getRunStartTime();
        int runDuration = (int)(System.currentTimeMillis() - startTime) / 1000; // millis to sec
        double distance = RunTableItem.calcDistance(route);
        double speed = RunTableItem.calculateSpeed(distance, runDuration);
        String runRoute = RunTableItem.setRoute(route);

        // If distance is 0, invalid run data;
        if(distance == 0) {
            return ;
        }

        // Set item params
        run.setDistance(distance);
        run.setRoute(runRoute);
        run.setRunTime(runDuration);
        run.setRunSpeed(speed);

        // Save Item
        RunItemDB db = RunItemDB.getDatabase(context);
        DatabaseManipulation.saveSingleData(db, run);

        // Clear static variables
        route.clear();
    }


    static String getDistance() {
        return String.valueOf(RunTableItem.calcDistance(route));
    }

    static String getRunTime() {
        long current = System.currentTimeMillis();
        int duration = (int)(current - run.getRunStartTime()) / 1000;
        return String.format(Locale.ENGLISH,
                "%02d:%02d", duration / 60, duration % 60);
    }

    public static void addRoutePoint(LatLng position){
        mLatLng = position;
        route.add(position);
    }

    public static LinkedList<LatLng> getRoute() {
        return route;
    }

    public static boolean isStarted() {
        return started;
    }

}