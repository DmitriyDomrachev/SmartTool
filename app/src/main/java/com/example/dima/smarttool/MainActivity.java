package com.example.dima.smarttool;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.dima.smarttool.DB.StateHelper;
import com.example.dima.smarttool.fragment.ListFragment;
import com.example.dima.smarttool.fragment.ScanFragment;
import com.example.dima.smarttool.fragment.SettingFragment;
import com.example.dima.smarttool.fragment.UserFragment;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.BLUETOOTH_ADMIN;


public class MainActivity extends AppCompatActivity {

    static FragmentManager fragmentManager;
    static FragmentTransaction fragmentTransaction;
    static Fragment fragment;
    public static ControlState controlState;       // объект класса, необходимый для отслеждивания текущего состояния устройства
    private static int navigateID = R.id.navigation_scan;
    private static int REQUEST_READ_ACCESS_FINE = 10001, countFragments = 0;
    private static final String[] READ_ACCESS_FINE = new String[]{BLUETOOTH_ADMIN, ACCESS_NETWORK_STATE};
    private static int batteryChange;
    static AudioManager audioManager;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            float percentage = level / (float) scale;
            controlState.scanBattery((int) ((percentage) * 100));
        }
    };



    static ArrayList<State> stateLoadArr = new ArrayList<>();
    static int countState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        fragmentManager = getFragmentManager(); //отображение 1 фрагмента
        fragment = new ScanFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commitAllowingStateLoss();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED); //IntentFilter батареи
        Intent batteryStatus = registerReceiver(mBroadcastReceiver, ifilter); //текущее состояние батареи, mBroadcastReceiver в качестве преемника



        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddStateActivity.class));
            }
        });

        loadDB();           // заргузка данных из базы данных


    }

    protected void onResume() {
        super.onResume();
        @SuppressLint("WifiManagerLeak") WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        controlState = new ControlState();
        controlState.connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);    // создание объекта для отслеждивания состояния устройства
        controlState.btAdapter = btAdapter;
        controlState.startScan();


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_scan:
                        navigateID = R.id.navigation_scan;
                        rewriteFragment();
                        return true;

                    case R.id.navigation_list:
                        navigateID = R.id.navigation_list;
                        fragment = new ListFragment();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.commitAllowingStateLoss();


                        return true;

                    case R.id.navigation_user:
                        navigateID = R.id.navigation_user;
                        rewriteFragment();
                        return true;

                    case R.id.navigation_setting:
                        navigateID = R.id.navigation_setting;
                        rewriteFragment();
                        return true;
                }
                return false;
            }
        });


    }

    private boolean isPermissionGranted(String permission) {
        int permissionCheck = ActivityCompat.checkSelfPermission(this, permission);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission, int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    protected void onPostExecute(Void image) {
        Log.d("test1 ", "поток end");

    }

    public static void rewriteFragment() {
        Log.d("test1", "rewrite");
        switch (navigateID) {
            case R.id.navigation_scan:
                controlState.scanSound(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
                Bundle arg = new Bundle();
                arg.putInt("battery", controlState.getBatteryState());
                arg.putInt("sound", controlState.getSoundState());
                arg.putBoolean("wifi", controlState.isWiFiState());
                arg.putBoolean("bluetooth", controlState.isBluetoothState());
                arg.putBoolean("mobile", controlState.isMobileState());
                fragment = new ScanFragment();
                fragment.setArguments(arg);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commitAllowingStateLoss();
                Log.d("test1", "rewrite scan");
                return;

            case R.id.navigation_user:
                fragment = new UserFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commitAllowingStateLoss();
                return;
            case R.id.navigation_setting:
                fragment = new SettingFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commitAllowingStateLoss();
                return;
            default:


        }


    }  //пересоздание фрагментов для отображения измененной информации

    public  ArrayList<State> updateListView() {
        return stateLoadArr;
    }

    public static int getCountState() {
        return countState;
    }

    public static ArrayList<State> getStateArr() {
        return stateLoadArr;
    }

    public void loadDB() {
        StateHelper sh = new StateHelper(getApplicationContext());     // инициализация помощника управления состояниямив базе данных
        stateLoadArr.clear();
        stateLoadArr.addAll(sh.getAll());                                        // сохранениесех состаяний из БД в ArrayList
        countState = stateLoadArr.size();
    }



}