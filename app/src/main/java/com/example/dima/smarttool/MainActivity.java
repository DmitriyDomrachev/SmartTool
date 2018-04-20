package com.example.dima.smarttool;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.dima.smarttool.DB.NoteHelper;
import com.example.dima.smarttool.DB.StateHelper;
import com.example.dima.smarttool.fragment.ListFragment;
import com.example.dima.smarttool.fragment.NoteFragment;
import com.example.dima.smarttool.fragment.ScanFragment;
import com.example.dima.smarttool.fragment.SettingFragment;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;


public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    static FragmentTransaction fragmentTransaction;
    Fragment fragment;

    private static int navigateID = R.id.navigation_scan;
    private static int REQUEST_READ_ACCESS_FINE = 3;
    private static final String[] READ_ACCESS_FINE = new String[]{READ_CONTACTS, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
    final int REQUEST_SAVE = 1;
    public static int batteryChange;
    static AudioManager audioManager;
    static BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    static WifiManager wifiManager;
    FloatingActionButton fab;
    private PendingIntent pendingIntent;
    AlarmManager alarmManager;
    static ArrayList<State> stateLoadArr = new ArrayList<>();
    static ArrayList<Note> noteLoadArr = new ArrayList<>();
    static int countState, countNote;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            float percentage = level / (float) scale;
            batteryChange = (int) ((percentage) * 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestPermission(READ_ACCESS_FINE, REQUEST_READ_ACCESS_FINE);


        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE); //запрос разрешения на изменения состояния не беспокоить

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }


        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        setContentView(R.layout.activity_main);
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);                         //IntentFilter батареи
        Intent batteryStatus = registerReceiver(mBroadcastReceiver, ifilter);                            //текущее состояние батареи, mBroadcastReceiver в качестве преемника

        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        fab = findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (navigateID) {
                    case R.id.navigation_list:
                        startActivity(new Intent(MainActivity.this, AddStateActivity.class));
                        return;
                    case R.id.navigation_note:
                        startActivity(new Intent(MainActivity.this, AddNoteActivity.class));
                }
            }
        });

        startService(new Intent(MainActivity
                .this, GPSService.class));



    }

    protected void onResume() {
        super.onResume();
        loadDB();           // заргузка данных из базы данных
        new RewriteFragment().execute();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_scan:
                        navigateID = R.id.navigation_scan;
                        rewriteFragment();
                        fab.hide();

                        return true;

                    case R.id.navigation_list:
                        fab.show();
                        fragmentManager = getFragmentManager();
                        navigateID = R.id.navigation_list;
                        fragment = new ListFragment();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        return true;

                    case R.id.navigation_note:
                        fab.show();
                        fragmentManager = getFragmentManager();
                        navigateID = R.id.navigation_note;
                        fragment = new NoteFragment();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        return true;

                    case R.id.navigation_setting:
                        fab.hide();
                        fragmentManager = getFragmentManager();
                        fragment = new SettingFragment();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        return true;
                }
                return false;
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("alarm", "destroy");
    }


    public void rewriteFragment() {
        if (navigateID == R.id.navigation_scan) {
            fragmentManager = getFragmentManager();
            Bundle arg = new Bundle();
            arg.putInt("battery", batteryChange);
            arg.putInt("sound", 5);
            arg.putBoolean("wifi", wifiManager.isWifiEnabled());
            arg.putBoolean("bluetooth", btAdapter.isEnabled());
            Log.d("rewrite", wifiManager.isWifiEnabled()+" "+btAdapter.isEnabled());
            fragment = new ScanFragment();
            fragment.setArguments(arg);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commitAllowingStateLoss();
            Log.d("test1", "rewrite scan");
        }
    }                                        //пересоздание фрагментов для отображения измененной информации

    public static int getCountState() {
        return countState;
    }

    public static ArrayList<State> getStateArr() {
        return stateLoadArr;
    }

    public static ArrayList<Note> getNoteArr() {
        return noteLoadArr;
    }

    public void loadDB() {
        StateHelper sh = new StateHelper(getApplicationContext());                                     // инициализация помощника управления состояниямив базе данных
        stateLoadArr.clear();
        stateLoadArr.addAll(sh.getAll());                                                              // сохранениесех состаяний из БД в ArrayList
        countState = stateLoadArr.size();
        NoteHelper nh = new NoteHelper(getApplicationContext());
        noteLoadArr.clear();
        noteLoadArr.addAll(nh.getAll());
        countNote = noteLoadArr.size();
                                                                                                    //сканирование состояния
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

    }

    private class RewriteFragment extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... args) {

            Log.d("test", "поток");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            navigateID = R.id.navigation_scan;
            rewriteFragment();

            return null;
        }
    }

    protected void onPostExecute(Void image) {

    }

    private void requestPermission(String[] permission, int requestCode) {
        ActivityCompat.requestPermissions(this, permission, requestCode);
    }


}







