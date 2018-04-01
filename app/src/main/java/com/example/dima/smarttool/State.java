package com.example.dima.smarttool;

import android.util.Log;

/**
 * Created by dima on 27.02.2018.
 */

public class State {


    boolean wifiState, bluetoothState, mobileState;
    int batteryState, soundState, id;
    String name, LOGARG = "mytest";


    public State(int id, String name,boolean wifiState, boolean bluetoothState, boolean mobileState, int batteryState, int soundState) {
        this.id = id;
        this.name = name;
        this.wifiState = wifiState;
        this.bluetoothState = bluetoothState;
        this.mobileState = mobileState;
        this.batteryState = batteryState;
        this.soundState = soundState;
        Log.d(LOGARG,"new state name ="+name);

    }
    public State(){}



    public boolean isWiFiState() {
        return wifiState;
    }

    public boolean isBluetoothState() {
        return bluetoothState;
    }

    public boolean isMobileState() {
        return mobileState;
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

    @Override
    public String toString() {
        return "State{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", wifi='" + wifiState + '\'' +
                ", mobile='" + mobileState + '\'' +
                ", bluetooth='" + bluetoothState + '\'' +
                '}'+"\n";
    }

}