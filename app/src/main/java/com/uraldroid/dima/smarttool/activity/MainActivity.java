package com.uraldroid.dima.smarttool.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.uraldroid.dima.smarttool.DB.NoteHelper;
import com.uraldroid.dima.smarttool.DB.StateHelper;
import com.uraldroid.dima.smarttool.GeoService;
import com.uraldroid.dima.smarttool.Note;
import com.uraldroid.dima.smarttool.R;
import com.uraldroid.dima.smarttool.State;
import com.uraldroid.dima.smarttool.fragment.NoteFragment;
import com.uraldroid.dima.smarttool.fragment.ScanFragment;
import com.uraldroid.dima.smarttool.fragment.SettingFragment;
import com.uraldroid.dima.smarttool.fragment.StateFragment;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECEIVE_BOOT_COMPLETED;


public class MainActivity extends AppCompatActivity {

    private static final String[] READ_ACCESS_FINE = new String[]{ACCESS_FINE_LOCATION, RECEIVE_BOOT_COMPLETED};
    public static int batteryChange;
    static FragmentTransaction fragmentTransaction;
    static AudioManager audioManager;
    static BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    static WifiManager wifiManager;
    static ArrayList<State> stateLoadArr = new ArrayList<>();
    static ArrayList<Note> noteLoadArr = new ArrayList<>();
    static String TAG = "mainActivity";
    static int countState, countNote;
    static RewriteFragment rw;
    private static int navigateID = R.id.navigation_scan;
    private static int REQUEST_READ_ACCESS_FINE = 3;
    final int REQUEST_SAVE = 1;
    FragmentManager fragmentManager;
    Fragment fragmentSc, fragmentSt, fragmentN, fragmentS;
    FloatingActionButton fab;
    AlarmManager alarmManager;
    Toolbar toolbar;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            float percentage = level / (float) scale;
            batteryChange = (int) ((percentage) * 100);
        }
    };



    public static ArrayList<State> getStateArr() {
        return stateLoadArr;
    }

    public static ArrayList<Note> getNoteArr() {
        return noteLoadArr;
    }

    public static void setWiFi(boolean wifi) {
        wifiManager.setWifiEnabled(wifi);
        Log.d(TAG, "setWiFi");

    }

    public static void setBluetooth(boolean bluetooth) {
        if (bluetooth) btAdapter.enable();
        else btAdapter.disable();
        Log.d(TAG, "setBluetooth");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showStart();

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermission(READ_ACCESS_FINE, REQUEST_READ_ACCESS_FINE);
        requestPermission(READ_ACCESS_FINE, REQUEST_READ_ACCESS_FINE);


        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        //запрос разрешения на изменения состояния не беспокоить

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(android.provider.Settings
                    .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }


        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        setContentView(R.layout.activity_main);

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        Intent batteryStatus = registerReceiver(mBroadcastReceiver, ifilter);

        //текущее состояние батареи, mBroadcastReceiver в качестве преемника
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SmartTool");

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

        if (!isMyServiceRunning(GeoService.class))
            startService(new Intent(getApplicationContext(), GeoService.class));

        if (!isMyServiceRunning(GeoService.class))
            startService(new Intent(getApplicationContext(), GeoService.class));

        navigateID = R.id.navigation_scan;


    }


    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        loadDB();           // заргузка данных из базы данных

        fragmentSt = new StateFragment();
        fragmentN = new NoteFragment();
        fragmentS = new SettingFragment();

        fragmentManager = getFragmentManager();

        rewriteFragment();

        rw = new RewriteFragment();
        rw.execute();


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_scan:
                        navigateID = R.id.navigation_scan;
                        getSupportActionBar().setTitle("SmartTool");
                        rewriteFragment();
                        fab.hide();

                        return true;

                    case R.id.navigation_list:
                        fab.show();
                        navigateID = R.id.navigation_list;
                        getSupportActionBar().setTitle(getString(R.string.state_list));
                        loadFragment(fragmentSt);
                        return true;

                    case R.id.navigation_note:
                        fab.show();
                        navigateID = R.id.navigation_note;
                        getSupportActionBar().setTitle(getString(R.string.note_list));
                        loadFragment(fragmentN);
                        return true;

                    case R.id.navigation_setting:
                        fab.hide();
                        navigateID = R.id.navigation_setting;
                        getSupportActionBar().setTitle(getString(R.string.settings));
                        loadFragment(fragmentS);

                        return true;
                }
                return false;
            }
        });





        switch (navigation.getSelectedItemId()) {
            case R.id.navigation_scan:
                rewriteFragment();
                return;
            case R.id.navigation_list:
                loadFragment(fragmentSt);
                return;
            case R.id.navigation_note:
                loadFragment(new NoteFragment());
                return;
            case R.id.navigation_setting:
                loadFragment(fragmentS);
                return;
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_info) {
            startActivity(new Intent(MainActivity.this, InfoActivity.class));
        }
        return true;
    }


    public void rewriteFragment() {
        if (navigateID == R.id.navigation_scan ) {
            fragmentManager = getFragmentManager();
            Bundle arg = new Bundle();
            arg.putInt("battery", batteryChange);
            arg.putInt("sound", 5);
            arg.putBoolean("wifi", wifiManager.isWifiEnabled());
            arg.putBoolean("bluetooth", btAdapter.isEnabled());
            fragmentSc = new ScanFragment();
            if (!fragmentSc.isAdded()) {
                fragmentSc.setArguments(arg);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragmentSc);
                fragmentTransaction.commitAllowingStateLoss();
                Log.d(TAG, "rewrite scan");
            }
        }
    } //пересоздание фрагментов для отображения измененной информации


    public void loadDB() {
        StateHelper sh = new StateHelper(getApplicationContext());
        // инициализация помощника управления состояниямив базе данных
        stateLoadArr.clear();
        stateLoadArr.addAll(sh.getAll());
        // сохранениесех состаяний из БД в ArrayList
        countState = stateLoadArr.size();
        NoteHelper nh = new NoteHelper(getApplicationContext());
        noteLoadArr.clear();
        noteLoadArr.addAll(nh.getAll());
        countNote = noteLoadArr.size();
        //сканирование состояния
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

    }


    private void requestPermission(String[] permission, int requestCode) {
        ActivityCompat.requestPermissions(this, permission, requestCode);
    }


    private void showStart(){
        SharedPreferences prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
         if(!prefs.getBoolean("showedStartActivity", false)) {
             startActivity(new Intent(MainActivity.this, InfoActivity.class));
             editor.putBoolean("showedStartActivity", true);
             editor.apply();
         }
    }


    private void loadFragment(Fragment fragment) {
        fragmentTransaction = fragmentManager.beginTransaction();// вызов транзакции
        fragmentTransaction.replace(R.id.container, fragment);// замена фрагмента
        fragmentTransaction.commitAllowingStateLoss();// завершение транзакции

    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


        private class RewriteFragment extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        protected Void doInBackground(Void... args) {
            boolean wifi = wifiManager.isWifiEnabled();
            boolean bt = btAdapter.isEnabled();
            int battery = batteryChange;

            if (navigateID == R.id.navigation_scan)
                rewriteFragment();

            while (navigateID == R.id.navigation_scan) {
                Log.d("RewriteFragment","while");
                if (wifi != wifiManager.isWifiEnabled() || bt != btAdapter.isEnabled() ||
                        battery != batteryChange) {
                    rewriteFragment();
                    wifi = wifiManager.isWifiEnabled();
                    bt = btAdapter.isEnabled();
                    battery = batteryChange;
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

    }


}







