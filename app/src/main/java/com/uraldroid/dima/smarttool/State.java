package com.uraldroid.dima.smarttool;

import android.util.Log;

/**
 * Created by dima on 27.02.2018.
 */

public class State {


    private boolean wifiState, bluetoothState;
    private int  mediaSoundState, systemSoundState, id;
    private long startTime;
    private String name;
    private double lat, lng;




    public State(int id, String name, boolean wifiState, boolean bluetoothState, int mediaSoundState, int systemSoundState, long startTime, double lat, double lng) {
        this.id = id;
        this.name = name;
        this.wifiState = wifiState;
        this.bluetoothState = bluetoothState;
        this.mediaSoundState = mediaSoundState;
        this.systemSoundState = systemSoundState;
        this.startTime = startTime;
        this.lat = lat;
        this.lng = lng;

        Log.d("test","new state nameEditText ="+name);

    }

    public State(boolean wifiState, boolean bluetoothState, int mediaSoundState, int systemSoundState) {
        this.wifiState = wifiState;
        this.bluetoothState = bluetoothState;
        this.mediaSoundState = mediaSoundState;
        this.systemSoundState = systemSoundState;
    }

    public State(boolean wifiState, boolean bluetoothState, int mediaSoundState, int systemSoundState, String name) {
        this.wifiState = wifiState;
        this.bluetoothState = bluetoothState;
        this.mediaSoundState = mediaSoundState;
        this.systemSoundState = systemSoundState;
        this.name = name;
    }

    public State(){}



    public boolean isWiFiState() {
        return wifiState;
    }

    public boolean isBluetoothState() {
        return bluetoothState;
    }

    public int getMediaSoundState() {
        return mediaSoundState;
    }

    public int getSystemSoundState() {
        return systemSoundState;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public long getStartTime() {
        return startTime;
    }


    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    @Override
    public String toString() {
        return "State{" +
                "id=" + id +
                ", nameEditText='" + name + '\'' +
                ", wifiSwitch='" + wifiState + '\'' +
                ", bluetoothSwitch='" + bluetoothState + '\'' +
                ", mediaSound='" + mediaSoundState + '\'' +
                ", systemSound='" + systemSoundState + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                '}'+"\n";
    }

}