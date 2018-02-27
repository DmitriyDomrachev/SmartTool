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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    public void updateScan(){
        TextView battery = getView().findViewById(R.id.ScanFragmentBatteryTextView);
        TextView mobile = getView().findViewById(R.id.ScanFragmentMobileTextView);
        TextView wifi = getView().findViewById(R.id.ScanFragmentWiFiTextView);
        TextView bluetooth = getView().findViewById(R.id.ScanFragmentBleutoothTextView);
        TextView sound = getView().findViewById(R.id.ScanFragmentSoundTextView);
        battery.setText("lalal");
    }


}