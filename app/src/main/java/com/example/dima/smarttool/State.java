package com.example.dima.smarttool;

import android.util.Log;

/**
 * Created by dima on 27.02.2018.
 */

public class State {


    boolean wifiState, bluetoothState, mobileState;
    int batteryState, soundState, id;
    long startTime;
    String name, LOGARG = "mytest";




    public State(int id, String name, boolean wifiState, boolean bluetoothState, int batteryState, int soundState, long startTime) {
        this.id = id;
        this.name = name;
        this.wifiState = wifiState;
        this.bluetoothState = bluetoothState;
        this.batteryState = batteryState;
        this.soundState = soundState;
        this.startTime = startTime;

        Log.d(LOGARG,"new state name ="+name);

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

    public int getSoundState() {
        return soundState;
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
                '}'+"\n";
    }

}