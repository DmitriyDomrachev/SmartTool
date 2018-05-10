package com.example.dima.smarttool.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.dima.smarttool.DB.HistoryHelper;
import com.example.dima.smarttool.R;

/**
 * Created by dima on 27.02.2018.
 */

public class SettingFragment extends Fragment {
    Button clear;
    Spinner journalSettingSpinner, timeLocationSpinner, disanceLocationSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        clear = view.findViewById(R.id.SettingFragmentClearButton);
        journalSettingSpinner = view.findViewById(R.id.SettingFragmentJournalSpinner);
        timeLocationSpinner = view.findViewById(R.id.SettingFragmentTimeLocationSpinner);
        disanceLocationSpinner = view.findViewById(R.id.SettingFragmentDistanceLocationSpinner);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryHelper hh = new HistoryHelper(getActivity().getApplicationContext());
                hh.clearTable();
            }
        });

        final SharedPreferences prefs = getActivity().getSharedPreferences("myPrefs",       //создание объекта для работы с SharedPreference
                Context.MODE_PRIVATE);

        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(getActivity()             //создание аддаптеров для spinner
                .getApplicationContext(), R.array.journalSettingSpinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        journalSettingSpinner.setAdapter(adapter);
        journalSettingSpinner.setSelection(prefs.getInt("journalSetting", 0));

        adapter = ArrayAdapter.createFromResource(getActivity()
                .getApplicationContext(), R.array.timeLocation, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeLocationSpinner.setAdapter(adapter);
        timeLocationSpinner.setSelection(prefs.getInt("timeLocationSetting",0));

        adapter = ArrayAdapter.createFromResource(getActivity()
                .getApplicationContext(), R.array.distanceLocation, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        disanceLocationSpinner.setAdapter(adapter);
        disanceLocationSpinner.setSelection(prefs.getInt("distanceLocationSetting",0));


        journalSettingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor ed = prefs.edit();
                ed.putInt("journalSetting", position);
                ed.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });     //устоновка слушателей на spinner
        timeLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor ed = prefs.edit();
                ed.putInt("timeLocationSetting", position);
                ed.apply();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        disanceLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor ed = prefs.edit();
                ed.putInt("distanceLocationSetting", position);
                ed.apply();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }
}

