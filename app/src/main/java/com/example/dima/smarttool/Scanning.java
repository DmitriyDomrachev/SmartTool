package com.example.dima.smarttool;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.dima.smarttool.DB.StateHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class Scanning extends Service {

    // constant
    public static final long NOTIFY_INTERVAL = 10 * 1000;                               // время обновления
    static Map<String, State> stateTimeMap = new HashMap<String, State>();          //зраниение значиний времени старта и номера состояния

    private Handler mHandler = new Handler();                                           // run on another Thread to avoid crash
    private Timer mTimer = null;                                                        // timer handling
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    WifiManager wifiManager;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0,
                NOTIFY_INTERVAL);
        StateHelper sh = new StateHelper(getApplicationContext());                                     // инициализация помощника управления состояниямив базе данных
        loadStates(sh.getAll());
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast
                    Toast.makeText(getApplicationContext(), getTime(),
                            Toast.LENGTH_SHORT).show();
                    if (stateTimeMap.get(getTime()) != null) {
                        setState(stateTimeMap.get(getTime()));

                    }

                }
            });
        }


    }

    private String getTime() {                                                          // используйте метод для вывода текущего времени
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        date.setTime(calendar.getTimeInMillis());
        return date.getHours() + ":" + date.getMinutes();
    }


    public static void loadStates(ArrayList<State> states) {
        for (int i = 0; i < states.size(); i++) {
            State state = states.get(i);
            String time = TimeUnit.MILLISECONDS.toHours(state.getStartTime()) + ":" + TimeUnit.MILLISECONDS.toMinutes(state.getStartTime() - TimeUnit.MILLISECONDS.toHours(state.getStartTime()) * 3600000);
            stateTimeMap.put(time, state);
            Log.d("timeS", "loadState: " + time);
        }

    }

    private void setState(State state){
        wifiManager.setWifiEnabled(state.isWiFiState());
        if (state.isBluetoothState()) btAdapter.enable();
        else btAdapter.disable();
        Log.d("timeS", "setState: " + state.getName());

    }
}

