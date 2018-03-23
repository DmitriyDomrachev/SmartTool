package com.example.dima.smarttool;

import java.util.ArrayList;

/**
 * Created by dima on 27.02.2018.
 */

public class State {


    static ArrayList<State> stateList = new ArrayList<>();
    static boolean wifiState, bluetoothState, mobileState;
    static int batteryState, soundState;
    static String name;


    public State(String name,boolean wifiState, boolean bluetoothState, boolean mobileState, int batteryState, int soundState) {
        this.name = name;
        this.wifiState = wifiState;
        this.bluetoothState = bluetoothState;
        this.mobileState = mobileState;
        this.batteryState = batteryState;
        this.soundState = soundState;

    }
    public State(){}


    public void addState (String name,boolean wifiState, boolean bluetoothState, boolean mobileState, int batteryState, int soundState) {
        stateList.add(new State(name,wifiState,bluetoothState,mobileState,batteryState,soundState));
    }

    public State getState (int num) {
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
