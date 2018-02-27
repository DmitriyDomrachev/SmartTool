package com.example.dima.smarttool;

/**
 * Created by dima on 27.02.2018.
 */

public class ScanDevice {
    boolean WiFiState, BluetoothState, MobileState;
    byte BatteryState, SoundState;


    protected void scanWiFi() {
        WiFiState = true;
    }
    protected void scanBluetooth (){}
    protected void scanMobile (){}
    protected void scanBattery (){}
    protected void scanSound (){}



}

