package com.example.dima.smarttool;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ShowStateActivity extends FragmentActivity implements OnMapReadyCallback{
    Intent intent;
    TextView name, note;
    private GoogleMap mMap;
    private UiSettings uiSettings;
    MapFragment mMapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_state);
        intent = getIntent();
        name = findViewById(R.id.showStateNameText);
        note = findViewById(R.id.showStateText);
        name.setText(intent.getStringExtra("name"));
        note.setText(intent.getStringExtra("note"));

        mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapContainer, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 3);
            return;
        }
        uiSettings = mMap.getUiSettings();
        mMap.setIndoorEnabled(false); //отключение  зданий
        mMap.setMyLocationEnabled(true); //добавление слоя моего местоположения
        uiSettings.setMapToolbarEnabled(false); //отключение панели инструменов googleMaps
        uiSettings.setTiltGesturesEnabled(false);
    }
}
