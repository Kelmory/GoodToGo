package com.kelmory.goodtogo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kelmory.goodtogo.musicPlayer.MusicFragment;
import com.kelmory.goodtogo.utils.AndroidLocationService;
import com.kelmory.goodtogo.utils.PermissionManager;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location mLocation;
    private AndroidLocationService locationService;

    private MusicFragment musicPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLocationService();
        initRunButton();
        initMusicPlayer();
        initMap();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                Toast.makeText(MainActivity.this,
                        "Start running recording",
                        Toast.LENGTH_LONG)
                        .show();

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
                        .icon(bitmapDescriptorFromVector(context))
                        .position(position));
            }
        });
    }

    private void initMusicPlayer(){
        PermissionManager permissionManager = new PermissionManager(this);
        if(!permissionManager.checkPermissionForExternalStorage()){
            permissionManager.requestPermissionForExternalStorage();
        }
        else {
            musicPlayer = MusicFragment.newInstance();
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


    private BitmapDescriptor bitmapDescriptorFromVector(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.marker_location);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
