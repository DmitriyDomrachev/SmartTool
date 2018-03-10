package com.example.dima.smarttool;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by dima on 10.03.2018.
 */

public class ControlState extends MainActivity{
    private State state;
    boolean WiFiStateScan, BluetoothStateScan, MobileStateScan,WiFiStateSelect, BluetoothStateSelect, MobileStateSelect;
     int BatteryStateScan;
    int SoundStateScan, BatteryStateSelect, SoundStateSelect;
    @SuppressLint("WifiManagerLeak")

    WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();



    public  void scanWiFi() {
        WiFiStateScan = wifiManager.isWifiEnabled();

    }

    public void scanBluetooth (){
        BluetoothStateScan = btAdapter.isEnabled();
    }
    public void scanMobile (){
        MobileStateScan = true;
    }
    public void scanBattery(){
        BatteryStateScan = batteryLevel;
    }
    public  void scanSound (){
        SoundStateScan = 100;
    }

    public void setState (int numState){
        state.getState(numState);
        WiFiStateSelect = state.WiFiState;
        BluetoothStateSelect = state.BluetoothState;
        MobileStateScan = state.MobileState;
        BatteryStateScan = state.BatteryState;
        SoundStateScan = state.BatteryState;
    }


}
