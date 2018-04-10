package com.example.dima.smarttool;

import android.util.Log;

/**
 * Created by dima on 27.02.2018.
 */

public class State {


    private boolean wifiState, bluetoothState;
    private int batteryState, mediaSoundState, systemSoundState, id;
    private long startTime;
    private String name;




    public State(int id, String name, boolean wifiState, boolean bluetoothState, int batteryState, int mediaSoundState, int systemSoundState, long startTime) {
        this.id = id;
        this.name = name;
        this.wifiState = wifiState;
        this.bluetoothState = bluetoothState;
        this.batteryState = batteryState;
        this.mediaSoundState = mediaSoundState;
        this.systemSoundState = systemSoundState;
        this.startTime = startTime;

        Log.d("test","new state name ="+name);

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

    @Override
    public String toString() {
        return "State{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", wifi='" + wifiState + '\'' +
                ", bluetooth='" + bluetoothState + '\'' +
                ", mediaSound='" + mediaSoundState + '\'' +
                ", systemSound='" + systemSoundState + '\'' +
                '}'+"\n";
    }

}