package com.kelmory.goodtogo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {
    private static final int COARSE_LOCATION_REQUEST_CODE = 1;
    private static final int FINE_LOCATION_REQUEST_CODE = 2;
    private static final int INTERNET_REQUEST_CODE = 3;
    private static final int EXTERNAL_REQUEST_CODE = 4;


    private Activity activity;

    public PermissionManager(Activity activity){
        this.activity = activity;
    }

    public boolean checkPermissionForFineLocation(){
        int result = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkPermissionForCoarseLocation(){

        int result = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkPermissionForNetwork(){
        int result = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.INTERNET);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkPermissionForExternalStorage(){
        int result = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissionForFineLocation(){
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            FINE_LOCATION_REQUEST_CODE);
        }
    }

    public void requestPermissionForCoarseLocation(){
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            COARSE_LOCATION_REQUEST_CODE);
        }
    }

    public void requestPermissionForExternalStorage(){
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            EXTERNAL_REQUEST_CODE);
        }
    }

}
