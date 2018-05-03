package com.example.dima.smarttool;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.example.dima.smarttool.DB.HistoryHelper;
import com.example.dima.smarttool.DB.StateHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StateAlarmReceiver extends BroadcastReceiver {
    static Map<String, State> stateTimeMap = new HashMap<String, State>();                       //зраниение значиний времени старта и номера состояния
    static AudioManager audioManager;
    String name;
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    WifiManager wifiManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        name = intent.getStringExtra("nameEditText");
        Log.d("alarm", "receive");
        StateHelper sh = new StateHelper(context);                                     // инициализация помощника управления состояниямив базе данных
        loadStates(sh.getAll());
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Toast.makeText(context, "receive",
                Toast.LENGTH_LONG).show();
        if (stateTimeMap.get(name) != null) {
            State state = stateTimeMap.get(name);
            setState(state);
            SharedPreferences prefs = context.getSharedPreferences("myPrefs",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = prefs.edit();
            ed.putString("stateName", state.getName());
            ed.commit();

            HistoryHelper hh = new HistoryHelper(context);
            hh.insert("Состояние: "+ state.getName()+ "\nВремя включения: "+ getDate());

            Log.d("alarm", "setState: " + name);
        }

    }


    private String getTime() {                                                          // используйте метод для вывода текущего времени
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        date.setTime(calendar.getTimeInMillis());
        return date.getHours() + ":" + date.getMinutes();
    }


    public void loadStates(ArrayList<State> states) {
        for (int i = 0; i < states.size(); i++) {
            State state = states.get(i);
            stateTimeMap.put(state.getName(), state);
            Log.d("timeS", "loadState: " + state.getName() + " " + state.getMediaSoundState());
        }

    }

    private void setState(State state) {
        wifiManager.setWifiEnabled(state.isWiFiState());
        if (state.isBluetoothState()) btAdapter.enable();
        else btAdapter.disable();

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progInex(state.getMediaSoundState(),
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progInex(state.getSystemSoundState(),
                audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)), 0);
        Log.d("timeS", "setState: " + state.getName());

    }

    private int progInex(float in, int max) {
        in = in / 100;
        return (int) (max * in);
    }

    private String boolToString (boolean in){
        if (in) return "on";
        else  return "off";
    }

    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return dateFormat.format(new Date());
    }
}
