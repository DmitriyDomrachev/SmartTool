package com.example.dima.smarttool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import com.example.dima.smarttool.fragment.ListFragment;
import com.example.dima.smarttool.fragment.ScanFragment;
import com.example.dima.smarttool.fragment.SettingFragment;
import com.example.dima.smarttool.fragment.UserFragment;


public class MainActivity extends AppCompatActivity {

    static FragmentManager fragmentManager;
    static FragmentTransaction fragmentTransaction;
    static Fragment fragment;
    int batteryLevel;


    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("EXTRA_LEVEL", 0);
            int max = intent.getIntExtra("EXTRA_SCALE",0);
            batteryLevel = (level/max*100);
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

    protected void onDestroy() {
        unregisterReceiver( batteryReceiver );
        super.onDestroy();
    }


}



