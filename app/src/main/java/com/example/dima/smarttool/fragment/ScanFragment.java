package com.example.dima.smarttool.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dima.smarttool.DB.HistoryHelper;
import com.example.dima.smarttool.R;

import java.util.ArrayList;

/**
 * Created by dima on 27.02.2018.
 */

public class ScanFragment extends Fragment {
    boolean WiFiState = true, BluetoothState = true;
    int BatteryState = 0, SoundState = 0;
    String[] names;
    ArrayList<String> nameList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        HistoryHelper hh = new HistoryHelper(getActivity().getApplicationContext());
        nameList = hh.getAll();
        names = new String[nameList.size()];
        names = nameList.toArray(names);

        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        TextView battery = view.findViewById(R.id.ScanFragmentBatteryTextView);
        TextView wifi = view.findViewById(R.id.ScanFragmentWiFiTextView);
        TextView bluetooth = view.findViewById(R.id.ScanFragmentBleutoothTextView);
        TextView sound = view.findViewById(R.id.ScanFragmentSoundTextView);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            WiFiState = getArguments().getBoolean("wifi");
            BluetoothState = getArguments().getBoolean("bluetooth");
            BatteryState = getArguments().getInt("battery");
            SoundState = getArguments().getInt("sound");
        }
        battery.setText(battery.getText() + " " + BatteryState + "%");
        wifi.setText(wifi.getText() + boolToString(WiFiState));
        bluetooth.setText(bluetooth.getText() + boolToString(BluetoothState));
        sound.setText(sound.getText() + " " + SoundState + "%");

        // находим список
        ListView lvMain = (ListView) view.findViewById(R.id.ScanFragmentListView);

        // создаем адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, names);

        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);

        return view;




    }

    private String boolToString(boolean in) {
        if (in)
            return " on";
        else
            return " off";
    }


}