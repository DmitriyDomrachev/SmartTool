package com.example.dima.smarttool;


import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

//    Date date = new Date();
//    static Map<String, Integer> stateTimeMap = new HashMap<String, Integer>();
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    public  void onCreate(){
//        StateHelper sh = new StateHelper(getApplicationContext());                                     // инициализация помощника управления состояниямив базе данных
//        loadStates(sh.getAll());
//
//    }
//
//    public int onStartCommand (Intent intent, int flags, int startId) {
//        while (true){
//            Calendar calendar = Calendar.getInstance();
//            date.setTime(calendar.getTimeInMillis());
//            String time = date.getHours() + ":" + date.getMinutes();
//            Log.d("timeService", "time: "+time);
//
//
//            if (stateTimeMap.get(time) != null)
//                MainActivity.controlState.setState(stateTimeMap.get(time));
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                return START_STICKY;
//            }
//        }
//
//}
//
//    public  void setTimeMap(HashMap stateTimeMap){
//        this.stateTimeMap = stateTimeMap;
//    }
//
//    public void loadStates(ArrayList<State> states) {
//        for (int i = 0; i < states.size(); i++) {
//            State state1 = states.get(i);
//            String time = TimeUnit.MILLISECONDS.toHours(state1.getStartTime()) + ":" + TimeUnit.MILLISECONDS.toMinutes(state1.getStartTime() - TimeUnit.MILLISECONDS.toHours(state1.getStartTime()) * 3600000);
//            stateTimeMap.put(time, i);
//            Log.d("time", "loadState " + time);
//
//        }
//
//    }
//
//}
public class Scanning extends Service {

    // constant
    public static final long NOTIFY_INTERVAL = 60 * 1000; // 60 seconds

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

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
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast
                    Toast.makeText(getApplicationContext(), "Пора кормить кота!",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        // используйте метод в сообщении для вывода текущего времени вместо слов о кормежке кота
        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "[dd/MM/yyyy - HH:mm:ss]", Locale.getDefault());
            return sdf.format(new Date());
        }
    }
}


