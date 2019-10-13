package com.kelmory.goodtogo.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class DistanceComputation {

    private static final double EARTH_RADIUS = 6378137;

    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }

    public static double GetDistance(double lat1, double lng1, double lat2, double lng2)
    {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);

        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));

        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;

        return s;
    }

    public static double GetDistance(Location location1, Location location2){
        return GetDistance(location1.getLatitude(), location1.getLongitude(),
                location2.getLatitude(), location2.getLongitude());
    }

    public static double GetDistance(Location location1, LatLng location2){
        return GetDistance(location1.getLatitude(), location1.getLongitude(),
                location2.latitude, location2.longitude);
    }

    public static double GetDistance(LatLng latLng, LatLng latLng1) {
        return GetDistance(latLng.latitude, latLng.longitude, latLng1.latitude, latLng1.longitude);
    }
}
