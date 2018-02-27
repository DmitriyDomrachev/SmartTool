package com.example.dima.smarttool;

import java.util.ArrayList;

/**
 * Created by dima on 27.02.2018.
 */

public class Device {
    static ArrayList<Device> stateList = new ArrayList<>();
    boolean WiFiState, BluetoothState, MobileState;
    byte BatteryState, SoundState;

    public Device (boolean wifiState, boolean bluetoothState, boolean mobileState, byte batteryState, byte soundState) {
        WiFiState = wifiState;
        BluetoothState = bluetoothState;
        MobileState = mobileState;
        BatteryState = batteryState;
        SoundState = soundState;

    }

    public void addState (boolean wifiState, boolean bluetoothState, boolean mobileState, byte batteryState, byte soundState) {
        stateList.add(new Device(wifiState,bluetoothState,mobileState,batteryState,soundState));
    }
    public Device getStatd (int num) {
        return stateList.get(num);
    }
}
