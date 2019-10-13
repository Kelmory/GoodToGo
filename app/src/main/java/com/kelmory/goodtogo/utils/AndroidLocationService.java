package com.kelmory.goodtogo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;


public class AndroidLocationService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = AndroidLocationService.class.getSimpleName();

    private static final int PLAY_SERVICE_REQUEST_CODE = 9000;
    private static final int ALL_PERMISSION_RESULT = 1001;

    private static final int INTERVAL = 1000;
    private static final int FASTEST_INTERVAL = 500;

    private GoogleApiClient googleApiClient;
    private Location mLocation;

    private Context mContext;

    public interface OnLocationGetCallback {
        void onLocationGet(Location location);
    }

    private OnLocationGetCallback listener;

    public AndroidLocationService(Context context){
        listener = null;

        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        mContext = context;

        ArrayList<String> permissionToRequest = permissionToRequests(permissions);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(permissionToRequest.size() > 0){
                ((Activity)context).requestPermissions(permissionToRequest
                        .toArray(new String[permissionToRequest.size()]), ALL_PERMISSION_RESULT);
            }
        }

        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void startService(){
        if (googleApiClient != null) {
            googleApiClient.connect();
        }

        if(!checkPlayServices()){
            Toast.makeText(mContext, "Play Service Unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopService(){
        if(googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(mContext);

        if(resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode))
                apiAvailability.getErrorDialog((Activity) mContext,
                        resultCode, PLAY_SERVICE_REQUEST_CODE);
            return false;
        }

        return true;
    }

    private ArrayList<String> permissionToRequests(ArrayList<String> permissions) {
        ArrayList<String> result = new ArrayList<>();
        for(String perm : permissions)
            if (!hasPermission(perm))
                result.add(perm);
        return result;
    }

    private boolean hasPermission(String perm) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
            return mContext.checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED;
        else
            return true;
    }

    public Location getLocation(){
        return mLocation;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            return ;

        mLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationServices.FusedLocationApi
                .requestLocationUpdates(googleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // do nothing with this situation
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // do nothing with this situation
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        listener.onLocationGet(mLocation);
    }

    public void setLocationListener(OnLocationGetCallback locationListener){
        listener = locationListener;
    }

}
