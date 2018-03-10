package com.example.dima.smarttool;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
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
    int batteryLevel;
    private static int REQUEST_READ_ACCESS_FINE = 10001, countFragments = 0;
    private static final String[] READ_ACCESS_FINE = new String[]{ACCESS_WIFI_STATE, CHANGE_WIFI_STATE, BLUETOOTH_ADMIN};


    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("EXTRA_LEVEL", 1);
            int max = intent.getIntExtra("EXTRA_SCALE", 1);
            batteryLevel = (level / max * 100);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getFragmentManager();
        fragment = new ScanFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commitAllowingStateLoss();

        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (isPermissionGranted(BLUETOOTH_ADMIN)&& isPermissionGranted(ACCESS_WIFI_STATE)) {
            Toast.makeText(this, "Разрешение есть, можно работать", Toast.LENGTH_SHORT).show();
        } else { //иначе запрашиваем разрешение
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(READ_ACCESS_FINE, REQUEST_READ_ACCESS_FINE);
            }
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_scan:
                        fragment = new ScanFragment();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        return true;

                    case R.id.navigation_list:
                        fragment = new ListFragment();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        return true;

                    case R.id.navigation_user:
                        fragment = new UserFragment();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        return true;

                    case R.id.navigation_setting:
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






    protected void onResume () {
        super.onResume();
        @SuppressLint("WifiManagerLeak") WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        ControlState controlState = new ControlState();
        controlState.wifiManager = wifiManager;
        controlState.btAdapter = btAdapter;
        controlState.startScan();
    }



    protected void onDestroy() {
        unregisterReceiver(batteryReceiver);
        super.onDestroy();
    }

    private boolean isPermissionGranted(String permission) {
        int permissionCheck = ActivityCompat.checkSelfPermission(this, permission);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission, int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }


}



