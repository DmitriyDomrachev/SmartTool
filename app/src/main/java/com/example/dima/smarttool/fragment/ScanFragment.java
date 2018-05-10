package com.example.dima.smarttool.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dima.smarttool.DB.HistoryHelper;
import com.example.dima.smarttool.MainActivity;
import com.example.dima.smarttool.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by dima on 27.02.2018.
 */

public class ScanFragment extends Fragment {
    boolean WiFiState = true, BluetoothState = true;
    int BatteryState = 0, SoundState = 0;
    String[] names;
    ArrayList<String> nameList;
    TextView stateName;
    boolean isWiFiState, isBluetoothState;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences prefs = getActivity().getSharedPreferences("myPrefs",
                Context.MODE_PRIVATE);

        HistoryHelper hh = new HistoryHelper(getActivity().getApplicationContext());
        nameList = hh.getAll();
        names = new String[nameList.size()];
        if (prefs.getInt("journalSetting", 0) == 0)
            Collections.reverse(nameList);
        names = nameList.toArray(names);

        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        TextView batteryText = view.findViewById(R.id.ScanFragmentBatteryTextView);
        ImageButton wifi = view.findViewById(R.id.ScanFragmentWiFiImageButton);
        ImageButton bluetooth = view.findViewById(R.id.ScanFragmentBluetoothImageButton);
        ImageButton battery = view.findViewById(R.id.ScanFragmentBatteryImageButton);
        TextView stateName = view.findViewById(R.id.ScanFragmentStateTextView);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            WiFiState = getArguments().getBoolean("wifi");
            BluetoothState = getArguments().getBoolean("bluetooth");
            BatteryState = getArguments().getInt("battery");
            SoundState = getArguments().getInt("sound");
        }
        batteryText.setText("" + BatteryState);
        if (WiFiState)
            wifi.setColorFilter(Color.argb(255, 124, 75, 255));
        else wifi.setColorFilter(Color.argb(255, 189, 189, 189));
        if (BluetoothState)
            bluetooth.setColorFilter(Color.argb(255, 124, 75, 255));
        else bluetooth.setColorFilter(Color.argb(255, 189, 189, 189));
        if (BatteryState > 30)
            battery.setColorFilter(Color.argb(255, 124, 75, 255));
        else battery.setColorFilter(Color.argb(255, 189, 189, 189));

        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WiFiState = !WiFiState;
                MainActivity.setWiFi(WiFiState);
            }
        });

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothState =! BluetoothState;
                MainActivity.setBluetooth(BluetoothState);
            }
        });


        stateName.setText("Последнее состояние: " + String.valueOf(prefs.getString("stateName", "")));

        // находим список
        ListView lvMain = (ListView) view.findViewById(R.id.ScanFragmentListView);

        // создаем адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, names) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView tv = view.findViewById(android.R.id.text1);

                tv.setTextColor(getResources().getColor(R.color.secondary_text));

                return view;
            }
        };

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