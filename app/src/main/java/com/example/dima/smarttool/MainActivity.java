package com.example.dima.smarttool;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
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

import com.example.dima.smarttool.DB.StateHelper;
import com.example.dima.smarttool.fragment.ListFragment;
import com.example.dima.smarttool.fragment.ScanFragment;
import com.example.dima.smarttool.fragment.SettingFragment;
import com.example.dima.smarttool.fragment.UserFragment;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.CHANGE_WIFI_STATE;
import static android.Manifest.permission.READ_CONTACTS;


public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    static FragmentTransaction fragmentTransaction;
    Fragment fragment;

    private static int navigateID = R.id.navigation_scan;

    private static int REQUEST_READ_ACCESS_FINE = 10001, countFragments = 0;
    private static final String[] READ_ACCESS_FINE = new String[]{BLUETOOTH_ADMIN, CHANGE_WIFI_STATE, ACCESS_WIFI_STATE, READ_CONTACTS};

    public static int batteryChange;
    static AudioManager audioManager;
    static BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    static WifiManager wifiManager;
    static FloatingActionButton fab, fabMap;


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            float percentage = level / (float) scale;
            batteryChange = (int) ((percentage) * 100);
        }
    };

    static ArrayList<State> stateLoadArr = new ArrayList<>();
    static int countState;
    public final static String FILE_NAME = "filename";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActivityCompat.requestPermissions(this,
                new String[]{READ_CONTACTS,ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION},
                3);

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }


        setContentView(R.layout.activity_main);
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);                 //IntentFilter батареи
        Intent batteryStatus = registerReceiver(mBroadcastReceiver, ifilter);                            //текущее состояние батареи, mBroadcastReceiver в качестве преемника

        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        fab = findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddStateActivity.class));
            }
        });

        fabMap = findViewById(R.id.fab2);
//        fabMap.hide();
        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });


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

                    case R.id.navigation_user:
                        fab.hide();
                        fragmentManager = getFragmentManager();
                        navigateID = R.id.navigation_user;
                        fragment = new UserFragment();
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
//        fragmentManager = getFragmentManager(); //отображение 1 фрагмента
//        navigateID = R.id.navigation_scan;
//        rewriteFragment();

    }


    public void rewriteFragment() {
        if (navigateID == R.id.navigation_scan) {
            fragmentManager = getFragmentManager();
            Bundle arg = new Bundle();
            arg.putInt("battery", batteryChange);
            arg.putInt("sound", 5);
            arg.putBoolean("wifi", wifiManager.isWifiEnabled());
            arg.putBoolean("bluetooth", btAdapter.isEnabled());
            fragment = new ScanFragment();
            fragment.setArguments(arg);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commitAllowingStateLoss();
            Log.d("test1", "rewrite scan");
        }
    }                                        //пересоздание фрагментов для отображения измененной информации

//    public ArrayList<State> updateListView() {
//        return stateLoadArr;
//    }

    public static int getCountState() {
        return countState;
    }

    public static ArrayList<State> getStateArr() {
        return stateLoadArr;
    }

    public void loadDB() {
        StateHelper sh = new StateHelper(getApplicationContext());                                     // инициализация помощника управления состояниямив базе данных
        stateLoadArr.clear();
        stateLoadArr.addAll(sh.getAll());                                                              // сохранениесех состаяний из БД в ArrayList
        countState = stateLoadArr.size();
        Intent intent = new Intent(this, Scanning.class);
        intent.putExtra("arrayList", stateLoadArr);
        startService(new Intent(this, Scanning.class));                                 //сканирование состояния
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


//    private boolean isPermissionGranted(String permission) {
//        int permissionCheck = ActivityCompat.checkSelfPermission(this, permission);
//        return permissionCheck == PackageManager.PERMISSION_GRANTED;
//    }
//
//    private void requestPermission(String permission, int requestCode) {
//        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
//    }
}







