package com.example.dima.smarttool;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.Toast;

import com.example.dima.smarttool.fragment.ListFragment;
import com.example.dima.smarttool.fragment.ScanFragment;
import com.example.dima.smarttool.fragment.SettingFragment;
import com.example.dima.smarttool.fragment.UserFragment;

import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.CHANGE_WIFI_STATE;


public class MainActivity extends AppCompatActivity {

    static FragmentManager fragmentManager;
    static FragmentTransaction fragmentTransaction;
    static Fragment fragment;
    public static ControlState controlState;
    private static int navigateID = R.id.navigation_scan;
    int batteryLevel;
    private static int REQUEST_READ_ACCESS_FINE = 10001, countFragments = 0;
    private static final String[] READ_ACCESS_FINE = new String[]{ACCESS_WIFI_STATE, CHANGE_WIFI_STATE, BLUETOOTH_ADMIN};





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fragmentManager = getFragmentManager();         //отображение 1 фрагмента
        fragment = new ScanFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commitAllowingStateLoss();


    }


    protected void onResume() {
        super.onResume();
        @SuppressLint("WifiManagerLeak") WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        controlState = new ControlState();              // создание объекта для отслеждивания состояния устройства
        controlState.wifiManager = wifiManager;
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
                        rewriteFragment();
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

    public float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new     IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }           //уровень зарада акб


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
                Bundle arg = new Bundle();
                arg.putInt("battery", controlState.getBatteryStateScan());
                arg.putInt("sound", controlState.getSoundStateScan());
                arg.putBoolean("wifi", controlState.isWiFiStateScan());
                arg.putBoolean("bluetooth", controlState.isBluetoothStateScan());
                arg.putBoolean("mobile", controlState.isMobileStateScan());
                fragment = new ScanFragment();
                fragment.setArguments(arg);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commitAllowingStateLoss();
                Log.d("test1", "rewrite scan");

                return;
            case R.id.navigation_list:
                fragment = new ListFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commitAllowingStateLoss();
                Log.d("test1", "rewrite list");
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


}




