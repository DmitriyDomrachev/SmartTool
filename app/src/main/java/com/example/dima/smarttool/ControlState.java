//package com.example.dima.smarttool;
//
//import android.bluetooth.BluetoothAdapter;
//import android.net.wifi.WifiManager;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import java.util.ArrayList;
//
///**
// * Created by dima on 10.03.2018.
// */
//
//public class ControlState extends MainActivity {
//    private State state = new State();
//    WifiManager wifiManager ;
//    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
//
//
//    public void scanBattery(int bat) {
//        state.batteryState = bat;
//    }
//
//    public void scanSound(int volume) {
//        state.soundState = 100 * volume / 7;
//    }
//
//    public void startScan(ArrayList<State> states) {
//        new Scanning().execute();
//    }                                                                   //запуск потока сканирования
//
//    private class Scanning extends AsyncTask<Void, Integer, Void> {
//        protected void onPreExecute(int y) {
//            super.onPreExecute();
//        }
//
//        protected Void doInBackground(Void... args) {
//            while (true) {
//                Log.d("test", "scan");
//                state.wifiState = wifiManager.isWifiEnabled();
//                state.bluetoothState = btAdapter.isEnabled();
//
//                MainActivity.rewriteFragment();
//
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        protected void onPostExecute(Void image) {
//
//        }
//    }  // поток постоянного сканирования устройства
//
//    public boolean isWiFiState() {
//        return state.wifiState;
//    }
//
//    public boolean isBluetoothState() {
//
//        return state.bluetoothState;
//    }
//
//
//    public int getBatteryState() {
//        return state.batteryState;
//    }
//
//    public int getSoundState() {
//        return state.soundState;
//    }
//
//
//
//
//}
