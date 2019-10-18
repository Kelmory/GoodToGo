package com.kelmory.goodtogo.running.persistence;

import android.icu.util.Calendar;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;
import com.kelmory.goodtogo.utils.DistanceComputation;

import java.util.LinkedList;
import java.util.Locale;

@Entity(tableName = "run_history")
public class RunTableItem {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "runID")
    private int runID;

    @ColumnInfo(name = "runRoute")
    private String route;

    @ColumnInfo(name = "runDistance")
    private double distance;

    @ColumnInfo(name = "runStartTime")
    private long runStartTime;

    @ColumnInfo(name = "runDuration")
    private int runTime;

    @ColumnInfo(name = "runSpeed")
    private double runSpeed;

    public int getRunID() { return runID; }

    public void setRunID(int runID) { this.runID = runID; }

    public void setRoute(String route) { this.route = route; }

    public String getRoute() { return route; }

    public double getDistance() { return distance; }

    public void setDistance(double distance) { this.distance = distance; }

    public long getRunStartTime() { return runStartTime; }

    public void setRunStartTime(long runStartTime) { this.runStartTime = runStartTime; }

    public int getRunTime() { return runTime; }

    public void setRunTime(int runTime) { this.runTime = runTime; }

    public double getRunSpeed() { return runSpeed; }

    public void setRunSpeed(double runSpeed) { this.runSpeed = runSpeed; }

    /*
     *  Static methods to deal with calculation and format transfer.
     */


    public RunTableItem(){
        route = "";
        runStartTime = System.currentTimeMillis();
    }

    public static double calculateSpeed(double distance, long time){
        return distance / time;
    }

    public static String setRoute(LinkedList<LatLng> routeLatLng) {
        String route = "";
        if(!routeLatLng.isEmpty()){
            for(LatLng node: routeLatLng){
                String add = String.format(Locale.ENGLISH,"%.6f-%.6f;", node.latitude, node.longitude);
                route = route.concat(add);
            }
        }
        return route;
    }

    public static LinkedList<LatLng> getRouteLatLng(String route){
        LinkedList<LatLng> routeLatLng = new LinkedList<>();
        String[] strPoints = route.split(";");
        for(String strPoint : strPoints) {
            String[] strLatLng = strPoint.split("-");
            LatLng latLng = new LatLng(
                    Double.valueOf(strLatLng[0]), Double.valueOf(strLatLng[1]));
            routeLatLng.add(latLng);
        }
        return routeLatLng;
    }

    public static double calcDistance(LinkedList<LatLng> list){
        double dist = 0.0;

        if(list.isEmpty() || list.size() == 1)
            return dist;

        LatLng start = list.get(0);
        for(LatLng next: list.subList(1, list.size() - 1)){
            dist += DistanceComputation.GetDistance(start, next);
            start = next;
        }
        return dist;
    }

}
