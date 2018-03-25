package com.example.dima.smarttool;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by dima on 27.02.2018.
 */

public class State {


     static ArrayList<State> stateList = new ArrayList<>();
    boolean wifiState, bluetoothState, mobileState;
    int batteryState, soundState;
    String name, LOGARG = "mytest";


    public State(String name,boolean wifiState, boolean bluetoothState, boolean mobileState, int batteryState, int soundState) {
        this.name = name;
        this.wifiState = wifiState;
        this.bluetoothState = bluetoothState;
        this.mobileState = mobileState;
        this.batteryState = batteryState;
        this.soundState = soundState;
        Log.d(LOGARG,"new state name ="+name);

    }
    public State(){}


    public void addState (String name, boolean wifiState, boolean bluetoothState, boolean mobileState, int batteryState, int soundState) {
        stateList.add(new State(name,wifiState,bluetoothState,mobileState,batteryState,soundState));
    }

    public State getState (int num) {
        Log.d(LOGARG,"get state name = "+ stateList.get(num).getName()+"state num = "+num);

        return stateList.get(num);
    }

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

    public int size(){
        return stateList.size();
    }

    public int indexOf(State state){
        return stateList.indexOf(state);
    }

}
