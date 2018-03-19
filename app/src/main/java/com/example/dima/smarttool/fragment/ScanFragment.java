package com.example.dima.smarttool.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dima.smarttool.R;

/**
 * Created by dima on 27.02.2018.
 */

public class ScanFragment extends Fragment {
    boolean WiFiState =true, BluetoothState=true, MobileState=true;
    int BatteryState=0, SoundState=0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        TextView battery = view.findViewById(R.id.ScanFragmentBatteryTextView);
        TextView mobile = view.findViewById(R.id.ScanFragmentMobileTextView);
        TextView wifi = view.findViewById(R.id.ScanFragmentWiFiTextView);
        TextView bluetooth = view.findViewById(R.id.ScanFragmentBleutoothTextView);
        TextView sound = view.findViewById(R.id.ScanFragmentSoundTextView);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            WiFiState = getArguments().getBoolean("wifi");
            BluetoothState = getArguments().getBoolean("bluetooth");
            MobileState = getArguments().getBoolean("mobile");
            BatteryState = getArguments().getInt("battery");
            SoundState = getArguments().getInt("sound");
        }
        battery.setText(battery.getText() + "" + BatteryState);
        wifi.setText(wifi.getText() + "" +  WiFiState);
        mobile.setText(mobile.getText() + "" + MobileState);
        bluetooth.setText(bluetooth.getText() + "" + BluetoothState);
        sound.setText(sound.getText() + "" + SoundState);
        return view;
    }










}