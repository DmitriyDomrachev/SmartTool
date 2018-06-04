package com.uraldroid.dima.smarttool.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.uraldroid.dima.smarttool.DB.HistoryHelper;
import com.uraldroid.dima.smarttool.R;
import com.uraldroid.dima.smarttool.activity.MainActivity;

import java.util.ArrayList;
import java.util.Collections;

import static com.uraldroid.dima.smarttool.GeoService.CHECK_PLAY_SERVICES;
import static com.uraldroid.dima.smarttool.GeoService.PLAY_SERVICE_RESOLUTION_REQUEST;

/**
 * Created by dima on 27.02.2018.
 */

public class ScanFragment extends Fragment {
    final String TAG = "scanFragment";
    boolean WiFiState = true, BluetoothState = true;
    int BatteryState = 0, SoundState = 0;
    String[] names;
    ArrayList<String> nameList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences prefs = getActivity().getSharedPreferences("myPrefs",
                Context.MODE_PRIVATE);


        //check play services
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(CHECK_PLAY_SERVICES, checkPlayService());
        editor.apply();

        HistoryHelper hh = new HistoryHelper(getActivity().getApplicationContext());
        nameList = hh.getAll();
        names = new String[nameList.size()];
        if (prefs.getInt("journalSetting", 0) == 0)
            Collections.reverse(nameList);
        names = nameList.toArray(names);

        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        TextView batteryText = view.findViewById(R.id.ScanFragmentBatteryTextView);
        final ImageButton wifi = view.findViewById(R.id.ScanFragmentWiFiImageButton);
        final ImageButton bluetooth = view.findViewById(R.id.ScanFragmentBluetoothImageButton);
        final ImageButton battery = view.findViewById(R.id.ScanFragmentBatteryImageButton);
        TextView stateName = view.findViewById(R.id.ScanFragmentStateTextView);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            WiFiState = getArguments().getBoolean("wifi");
            BluetoothState = getArguments().getBoolean("bluetooth");
            Log.d(TAG, "state: " + WiFiState + BluetoothState);
            BatteryState = getArguments().getInt("battery");
            SoundState = getArguments().getInt("sound");
        } //загрузка значений
        batteryText.setText("" + BatteryState);

        if (BatteryState > 20) {
            battery.setColorFilter(getResources().getColor(R.color.colorPrimaryLight));
            batteryText.setTextColor(getResources().getColor(R.color.secondaryTextColor));

        } else {
            battery.setColorFilter(getResources().getColor(R.color.colorSecondaryDark));
            batteryText.setTextColor(getResources().getColor(R.color.primaryTextColor));

        }

        if (WiFiState)
            wifi.setColorFilter(getResources().getColor(R.color.colorPrimaryLight));
        else wifi.setColorFilter(getResources().getColor(R.color.colorSecondaryDark));

        if (BluetoothState)
            bluetooth.setColorFilter(getResources().getColor(R.color.colorPrimaryLight));
        else bluetooth.setColorFilter(getResources().getColor(R.color.colorSecondaryDark));
        //установка цвета при запуске


        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WiFiState = !WiFiState;
                if (WiFiState)
                    wifi.setColorFilter(getResources().getColor(R.color.colorPrimaryLight));
                else wifi.setColorFilter(getResources().getColor(R.color.colorSecondaryDark));
                MainActivity.setWiFi(WiFiState);
                //управление состоянием
            }
        });

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothState = !BluetoothState;
                if (BluetoothState)
                    bluetooth.setColorFilter(getResources().getColor(R.color.colorPrimaryLight));
                else bluetooth.setColorFilter(getResources().getColor(R.color.colorSecondaryDark));
                MainActivity.setBluetooth(BluetoothState);
                //управление состоянием

            }
        });


        stateName.setText(String.valueOf(stateName.getText() + " " + (prefs.getString("stateName", ""))));

        // находим список
        ListView lvMain = view.findViewById(R.id.ScanFragmentListView);

        // создаем адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, names) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView tv = view.findViewById(android.R.id.text1);

                tv.setTextColor(getResources().getColor(R.color.secondaryTextColor));

                return view;
            }
        };

        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);

        return view;


    }

    private boolean checkPlayService() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICE_RESOLUTION_REQUEST).show();

            } else {
                Toast.makeText(getActivity().getApplicationContext(), "This device is not supported",
                        Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }


}