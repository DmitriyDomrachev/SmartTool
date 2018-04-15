package com.example.dima.smarttool;

import android.util.Log;

/**
 * Created by dima on 27.02.2018.
 */

public class State {


    private boolean wifiState, bluetoothState;
    private int batteryState, mediaSoundState, systemSoundState, id;
    private long startTime;
    private String name, latlng;




    public State(int id, String name, boolean wifiState, boolean bluetoothState, int batteryState, int mediaSoundState, int systemSoundState, long startTime, String latlng) {
        this.id = id;
        this.name = name;
        this.wifiState = wifiState;
        this.bluetoothState = bluetoothState;
        this.batteryState = batteryState;
        this.mediaSoundState = mediaSoundState;
        this.systemSoundState = systemSoundState;
        this.startTime = startTime;
        this.latlng = latlng;

        Log.d("test","new state nameEditText ="+name);

    }
    public State(){}



    public boolean isWiFiState() {
        return wifiState;
    }

    public boolean isBluetoothState() {
        return bluetoothState;
    }

    public int getBatteryState() {
        return batteryState;
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

    public void setWifiState(boolean wifiState) {
        this.wifiState = wifiState;
    }

    public String getLatlng() {
        return latlng;
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
                '}'+"\n";
    }

}