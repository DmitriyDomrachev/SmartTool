package com.example.dima.smarttool.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dima.smarttool.ControlState;
import com.example.dima.smarttool.R;

/**
 * Created by dima on 27.02.2018.
 */

public class ScanFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        TextView battery = view.findViewById(R.id.ScanFragmentBatteryTextView);
        TextView mobile = view.findViewById(R.id.ScanFragmentMobileTextView);
        TextView wifi = view.findViewById(R.id.ScanFragmentWiFiTextView);
        TextView bluetooth = view.findViewById(R.id.ScanFragmentBleutoothTextView);
        TextView sound = view.findViewById(R.id.ScanFragmentSoundTextView);
//        ControlState cs = new ControlState();
//        battery.setText(battery.getText()+""+cs.getBatteryStateScan());
//        wifi.setText(battery.getText()+""+cs.isWiFiStateScan());
//        mobile.setText(battery.getText()+""+cs.isMobileStateScan());
//        bluetooth.setText(battery.getText()+""+cs.isBluetoothStateScan());
//        sound.setText(battery.getText()+""+cs.getBatteryStateScan());

        return view ;
    }

    public void onStart() {

        super.onStart();
    }




}