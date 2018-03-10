package com.example.dima.smarttool;

import java.util.ArrayList;

/**
 * Created by dima on 27.02.2018.
 */

public class State {


    static ArrayList<State> stateList = new ArrayList<>();
    boolean WiFiState, BluetoothState, MobileState;
    int BatteryState, SoundState;


    public State(boolean wifiState, boolean bluetoothState, boolean mobileState, int batteryState, int soundState) {
        WiFiState = wifiState;
        BluetoothState = bluetoothState;
        MobileState = mobileState;
        BatteryState = batteryState;
        SoundState = soundState;

    }

    public void addState (boolean wifiState, boolean bluetoothState, boolean mobileState, int batteryState, int soundState) {
        stateList.add(new State(wifiState,bluetoothState,mobileState,batteryState,soundState));
    }

    public State getState (int num) {
        return stateList.get(num);
    }
}
