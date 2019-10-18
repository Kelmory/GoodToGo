package com.kelmory.goodtogo.running;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.kelmory.goodtogo.running.persistence.RunItemDB;
import com.kelmory.goodtogo.running.persistence.RunTableItem;
import com.kelmory.goodtogo.utils.AndroidLocationService;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Locale;

public class RunningManager implements AndroidLocationService.OnLocationGetCallback {
    private final static String TAG = RunningManager.class.getSimpleName();

    private static RunTableItem run;
    private static LinkedList<LatLng> route;
    private static LatLng mLatLng;
    private static boolean started;

    private Context mContext;
    private AndroidLocationService locationService;

    static RunningManager getInstance(Context context){
        RunningManager runningManager = new RunningManager(context);
        started = true;
        run = new RunTableItem();
        route = new LinkedList<>();
        runningManager.startRunning();
        return runningManager;
    }

    public static LinkedList<LatLng> getRoute() {
        return route;
    }

    private RunningManager(Context context){
        locationService = new AndroidLocationService(context);
        locationService.setLocationListener(this);
        mContext = context;

    }

    public static boolean isStarted() {
        return started;
    }

    void startRunning(){
        locationService.startService();

        run.setRunStartTime(System.currentTimeMillis());

        if(mLatLng != null && started)
            route.add(mLatLng);
    }

    void stopRunning(){
        Log.d(TAG, "stop running");
        locationService.stopService();
        started = false;
        // Calculate parameters first
        long startTime = run.getRunStartTime();
        int runDuration = (int)(System.currentTimeMillis() - startTime);
        double distance = RunTableItem.calcDistance(route);
        double speed = RunTableItem.calculateSpeed(distance, runDuration);
        String runRoute = RunTableItem.setRoute(route);

        // if distance is 0, invalid data;
        if(distance == 0) {
            Toast.makeText(mContext, "Running too short, not recorded", Toast.LENGTH_LONG);
            return ;
        }

        // Set item params
        run.setDistance(distance);
        run.setRoute(runRoute);
        run.setRunTime(runDuration);
        run.setRunSpeed(speed);

        // Save Item
        RunItemDB db = RunItemDB.getDatabase(mContext);
        DatabaseManipulation.saveSingleData(db, run);

        // Clear static variables
        route.clear();

    }

    @Override
    public void onLocationGet(Location location) {
        mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if(started)
            route.add(mLatLng);
    }

    String getDistance() {
        return String.valueOf(RunTableItem.calcDistance(route));
    }

    String getRunTime() {
        long current = System.currentTimeMillis();
        int duration = (int)(current - run.getRunStartTime()) / 1000;
        return String.format(Locale.ENGLISH, "%d:%d", duration / 60, duration % 60);
    }
}

class RunComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        RunTableItem item1 = (RunTableItem)o1;
        RunTableItem item2 = (RunTableItem)o2;

        return Long.compare(item1.getRunStartTime(), item2.getRunStartTime());
    }
}