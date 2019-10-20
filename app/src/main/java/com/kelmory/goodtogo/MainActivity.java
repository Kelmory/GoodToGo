package com.kelmory.goodtogo;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kelmory.goodtogo.functions.FunctionActivity;
import com.kelmory.goodtogo.musicPlayer.MusicFragment;
import com.kelmory.goodtogo.running.RunningIntentService;
import com.kelmory.goodtogo.running.RunningManager;
import com.kelmory.goodtogo.utils.AndroidLocationService;
import com.kelmory.goodtogo.utils.PermissionManager;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private static final int FUNCTION_START_REQUEST_CODE = 143;
    private GoogleMap mMap;
    private Location mLocation;
    private AndroidLocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLocationService();
        initFunctionButton();
        initRunButton();
        initMusicPlayer();
        initMap();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initFunctionButton(){
        FloatingActionButton floatingFunctionButton = findViewById(R.id.floatbutton_function);
        floatingFunctionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FunctionActivity.class);
                startActivityForResult(intent, FUNCTION_START_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initRunButton(){
        FloatingActionButton floatingRunButton = findViewById(R.id.floatbutton_run);
        floatingRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mLocation != null){
                    LatLng position = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16.0f));
                }
                else{
                    Toast.makeText(MainActivity.this,
                            "Location unavailable temporarily",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        floatingRunButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!RunningManager.isStarted()) {
                    RunningIntentService.startActionStart(MainActivity.this);
                }
                else{
                    RunningIntentService.startActionStop(MainActivity.this);
                }
                return false;
            }
        });
    }

    private void initLocationService(){
        mLocation = null;
        locationService = new AndroidLocationService(this);

        final Context context = this;

        locationService.setLocationListener(new AndroidLocationService.OnLocationGetCallback() {
            @Override
            public void onLocationGet(Location location) {
                mLocation = location;
                LatLng position = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_location))
                        .position(position));

                if(RunningManager.isStarted()){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18.0f));


                    LinkedList<LatLng> route = RunningManager.getRoute();
                    if(route != null && !route.isEmpty()){
                        mMap.addPolyline(new PolylineOptions()
                                .addAll(route));
                    }
                }
            }
        });
    }

    private void initMusicPlayer(){
        PermissionManager permissionManager = new PermissionManager(this);
        if(!permissionManager.checkPermissionForExternalStorage()){
            permissionManager.requestPermissionForExternalStorage();
        }
        else {
            MusicFragment.newInstance();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationService.startService();
    }

    @Override
    protected void onPause() {
        locationService.stopService();
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-33.908, 151.211);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13.5f));
    }

}
