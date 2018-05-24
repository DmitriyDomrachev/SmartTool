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
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.dima.smarttool.DB.HistoryHelper;
import com.example.dima.smarttool.R;

/**
 * Created by dima on 27.02.2018.
 */

public class SettingFragment extends Fragment {
    TextView clear;
    Spinner journalSettingSpinner, timeLocationSpinner, distanceLocationSpinner;
    Switch soundStateSwitch, notifStateSwitch, soundNoteSwitch;
    public static final String JOURNAL_SETTING = "journalSetting",
            TIME_LOCATION_SETTING = "timeLocationSetting", DIST_LOCATION_SETTING = "distanceLocationSetting",
            SOUND_NOTIF_STATE_SETTING = "soundNotifStateSetting", NOTIF_STATE_SETTING = "NotifStateSetting",
            SOUND_NOTIF_NOTE_SETTING = "soundNotifNoteSetting";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        clear = view.findViewById(R.id.SettingFragmentClearText);
        journalSettingSpinner = view.findViewById(R.id.SettingFragmentJournalSpinner);
        timeLocationSpinner = view.findViewById(R.id.SettingFragmentTimeLocationSpinner);
        distanceLocationSpinner = view.findViewById(R.id.SettingFragmentDistanceLocationSpinner);
        soundStateSwitch = view.findViewById(R.id.SettingFragmentStateSoundNotificationSwitch);
        notifStateSwitch = view.findViewById(R.id.SettingFragmentStateNotificationSwitch);
        soundNoteSwitch = view.findViewById(R.id.SettingFragmentNoteSoundNotificationSwitch);



        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryHelper hh = new HistoryHelper(getActivity().getApplicationContext());
                hh.clearTable();
            }
        });

        final SharedPreferences prefs = getActivity().getSharedPreferences("myPrefs",
                Context.MODE_PRIVATE);
       final  SharedPreferences.Editor ed = prefs.edit();

        //создание объекта для работы с SharedPreference

        soundStateSwitch.setChecked(prefs.getBoolean(SOUND_NOTIF_STATE_SETTING,true));
        notifStateSwitch.setChecked(prefs.getBoolean(NOTIF_STATE_SETTING,true));
        soundNoteSwitch.setChecked(prefs.getBoolean(SOUND_NOTIF_NOTE_SETTING,true));


        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(getActivity()
                .getApplicationContext(), R.array.journalSettingSpinner, R.layout.spinner_text);
        //создание аддаптеров для spinner

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        journalSettingSpinner.setAdapter(adapter);
        journalSettingSpinner.setSelection(prefs.getInt(JOURNAL_SETTING, 0));

        adapter = ArrayAdapter.createFromResource(getActivity()
                .getApplicationContext(), R.array.timeLocation, R.layout.spinner_text);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeLocationSpinner.setAdapter(adapter);
        timeLocationSpinner.setSelection(prefs.getInt(TIME_LOCATION_SETTING,0));

        adapter = ArrayAdapter.createFromResource(getActivity()
                .getApplicationContext(), R.array.distanceLocation, R.layout.spinner_text);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distanceLocationSpinner.setAdapter(adapter);
        distanceLocationSpinner.setSelection(prefs.getInt(DIST_LOCATION_SETTING,0));
        //настройка spinners

        soundStateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ed.putBoolean(SOUND_NOTIF_STATE_SETTING, isChecked);
                ed.apply();

            }
        });

        notifStateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ed.putBoolean(NOTIF_STATE_SETTING, isChecked);
                ed.apply();

            }
        });

        soundNoteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ed.putBoolean(SOUND_NOTIF_NOTE_SETTING, isChecked);
                ed.apply();

            }
        }); //слушатели на switch с сохранением в SharedPreference




        journalSettingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ed.putInt(JOURNAL_SETTING, position);
                ed.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        timeLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ed.putInt(TIME_LOCATION_SETTING, position);
                ed.apply();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        distanceLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ed.putInt(DIST_LOCATION_SETTING, position);
                ed.apply();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }
}

