package com.example.dima.smarttool;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.dima.smarttool.DB.HistoryHelper;
import com.example.dima.smarttool.DB.NoteHelper;
import com.example.dima.smarttool.DB.StateHelper;
import com.example.dima.smarttool.activity.MainActivity;
import com.example.dima.smarttool.activity.ShowActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.example.dima.smarttool.fragment.SettingFragment.NOTIF_STATE_SETTING;
import static com.example.dima.smarttool.fragment.SettingFragment.SOUND_NOTIF_NOTE_SETTING;
import static com.example.dima.smarttool.fragment.SettingFragment.SOUND_NOTIF_STATE_SETTING;

/**
 * Created by dima on 30.05.2018.
 */

public class GeoService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    public static final int PLAY_SERVICE_RESOLUTION_REQUEST = 65564;
    public static final String CHECK_PLAY_SERVICES = "CheckPlayServices";
    //PlayService Location
    private static final int MY_PERNISSION_REQUEST_CODE = 1236;
    private static final String TAG = "GeoService";
    private static final String WIFI_PREFS = "currentWiFi";
    private static final String BLUETOOTH_PREFS = "currentBluetooth";
    private static final String MEDIA_PREFS = "currentMedia";
    private static final String SYSTEM_PREFS = "currentSYSTEM";
    public static final String STATE_NOTIFICATION_CHANNEL_ID = "state_notification_channel";
    public static final String NOTE_NOTIFICATION_CHANNEL_ID = "note_notification_channel";
    private static final String FOREGROUND_NOTIFICATION_CHANNEL_ID = "foreground_notification_channel";
    public static final String NOTE_SOUND_NOTIFICATION_CHANNEL_ID = "note_sound_notification_channel";
    public static final String STATE_SOUND_NOTIFICATION_CHANNEL_ID = "state_sound_notification_channel";
    private static final String CURRENT_STATE = "currentStateName";
    private static final String LAST_NOTE = "lastNote";

    static ArrayList<LatLng> stateGpsList = new ArrayList<LatLng>();


    static SharedPreferences pref;
    static AudioManager audioManager;
    private static int UPDATE_INTERVAL = 1000; //10 min
    private static int FASTEST_INTERVAL = 900;
    private static int DISPLACEMENT = 20;
    private static int countNotes;
    ArrayList<GeoQuery> geoQuery = new ArrayList<GeoQuery>();
    DatabaseReference ref;
    GeoFire geoFire;
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    WifiManager wifiManager;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        pref = getSharedPreferences("myPrefs",
                Context.MODE_PRIVATE);
        ref = FirebaseDatabase.getInstance().getReference("MyLocation");
        geoFire = new GeoFire(ref);
        setUpLocation();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        StateHelper sh = new StateHelper(getApplicationContext());
        NoteHelper nh = new NoteHelper(getApplicationContext());

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        loadStatesNotes(sh.getAll(), nh.getAll());


        // Start foreground
        sendNotification();

        return Service.START_STICKY;

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private void setUpLocation() {
        Log.d(TAG, "setUp");

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)) {

            Toast.makeText(this, "Где мои разрешения??", Toast.LENGTH_LONG).show();
            //TODO: start activity to request permissions

        } else {
            if (pref.getBoolean(CHECK_PLAY_SERVICES, true)) {
                Log.d(TAG, "checkPlayService");
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }


    private void displayLocation() {
        Log.d(TAG, "DisplayLocation");


        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED))
            return;

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            final double lat = mLastLocation.getLatitude();
            final double lng = mLastLocation.getLongitude();

            //Update to Firebase

            geoFire.setLocation("You", new GeoLocation(lat, lng), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    Log.d(TAG, "location added to firebase");

                }
            });


            Log.d(TAG, String.format("Your location was changed : %f / %f", lat, lng));
        } else
            Log.d(TAG, "Can not get your location");
    }

    @SuppressLint("RestrictedApi")
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
        Log.d(TAG, "createLocationRequest");

    }


    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        Log.d(TAG, "buildGoogleApiClient");

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        displayLocation();

    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)) {
            return;
        } else if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }


    private void setGeoFence(final State state, int i) {
        Log.d(TAG, "i:: " + i);
        final SharedPreferences prefs = getSharedPreferences("myPrefs",
                Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

        geoQuery.add(geoFire.queryAtLocation(new GeoLocation(state.getLat(), state.getLng()), 0.2f));

        if (state.getLat() != 0 || state.getLng() != 0) {
            geoQuery.get(i).addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {


                    if(!Objects.equals(prefs.getString(CURRENT_STATE, "1"), state.getName())){
                        setLastState(getCurrentState());
                        setState(state);
                        editor.putString(CURRENT_STATE, state.getName());
                        editor.apply();

                    }


                }

                @Override
                public void onKeyExited(String key) {
                    Log.d(TAG, "onKeyExited");
                    setState(getLastState());
                    editor.putString(CURRENT_STATE, "users");
                    editor.apply();

                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {
                    Log.d(TAG, String.format("%s moved within dangerous area [%f/%f]", key, location.latitude, location.longitude));

                }


                @Override
                public void onGeoQueryReady() {

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {
                    Log.e("ERROR", "" + error);
                }
            });
        }
    }


    private void setGeoFence(final Note note, int i) {
        Log.d(TAG, "setGeoFence: " + note.getName());
        final SharedPreferences prefs = getSharedPreferences("myPrefs",
                Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

        geoQuery.add(geoFire.queryAtLocation(new GeoLocation(note.getLat(), note.getLng()), 0.2f));


        if (note.getLat() != 0 || note.getLng() != 0) {
            geoQuery.get(i).addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    Log.d(TAG, "onKeyEntered");

                    if (!prefs.getBoolean(note.getName() + "activated", false)) {
                        sendNotification(getApplicationContext(), note);
                        editor.putBoolean(note.getName() + "activated", true);
                        editor.apply();
                    }
                }

                @Override
                public void onKeyExited(String key) {
                    Log.d(TAG, "onKeyExited");
                    editor.putBoolean(note.getName() + "activated", false);
                    editor.apply();

                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {
                    Log.d(TAG, String.format("%s moved within dangerous area [%f/%f]", key, location.latitude, location.longitude));

                }


                @Override
                public void onGeoQueryReady() {

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {
                    Log.e("ERROR", "" + error);
                }
            });
        }
    }


    private void loadStatesNotes(ArrayList<State> states, ArrayList<Note> notes) {

        countNotes = notes.size();
        Log.d(TAG, "Count: " + countNotes);
        for (int i = 0; i < countNotes; i++) {
            Note note = notes.get(i);
            LatLng latLng = new LatLng(note.getLat(), note.getLng());
            stateGpsList.add(latLng);
            setGeoFence(note, i);

            geoFire.setLocation("Note: " + note.getName(), new GeoLocation(note.getLat(), note.getLat()), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    Log.d(TAG, "note added to firebase");

                }
            });

            Log.d(TAG, "loadNote: " + note.getName());
        }

        for (int i = 0; i < states.size(); i++) {
            State state = states.get(i);
            LatLng latLng = new LatLng(state.getLat(), state.getLng());
            stateGpsList.add(latLng);
            setGeoFence(state, countNotes);
            countNotes++;

            geoFire.setLocation("State: " + state.getName(), new GeoLocation(state.getLat(), state.getLat()), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    Log.d(TAG, "state added to firebase");

                }
            });

            Log.d(TAG, "loadState: " + state.getName());
        }

        countNotes = notes.size();

    }


    private void setState(State state) {


        HistoryHelper hh = new HistoryHelper(getApplicationContext());
        hh.insert("Состояние: " + state.getName() + "\nВремя включения: " + getDate());
        wifiManager.setWifiEnabled(state.isWiFiState());

        if (state.isBluetoothState()) btAdapter.enable();
        else btAdapter.disable();

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, state.getMediaSoundState(), 0);
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, state.getSystemSoundState(), 0);

        SharedPreferences prefs = getSharedPreferences("myPrefs",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("stateName", state.getName());
        ed.apply();
        Log.d(TAG, "setState: " + state.getName());

        if (prefs.getBoolean(NOTIF_STATE_SETTING, true))
            sendNotification(getApplicationContext(), state);

    }


    private void sendNotification(Context context, State state) {

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("myPrefs",
                Context.MODE_PRIVATE);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(STATE_NOTIFICATION_CHANNEL_ID,
                    "State notifications", NotificationManager.IMPORTANCE_DEFAULT);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel 1");
            notificationChannel.enableLights(true);
            notificationChannel.setSound(null, null);
            notificationChannel.enableVibration(false);


            if (notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);

            notificationChannel = new NotificationChannel(STATE_SOUND_NOTIFICATION_CHANNEL_ID,
                    "State notifications", NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel 2");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(false);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);


            }
        }


        Intent notifyIntent = new Intent(context, ShowActivity.class);
        notifyIntent.putExtra("type", "State");
        notifyIntent.putExtra("name", state.getName());
        notifyIntent.putExtra("lat", state.getLat());
        notifyIntent.putExtra("lng", state.getLng());
        notifyIntent.putExtra("time", state.getStartTime());
        notifyIntent.putExtra("text", "Wifi: " + state.isWiFiState()
                + "\nBluetooth: " + state.isBluetoothState() + "\nMedia: " + state.getMediaSoundState()
                + "%\nSystem: " + state.getSystemSoundState() + "%");
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        if (prefs.getBoolean(SOUND_NOTIF_STATE_SETTING, false)) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, STATE_SOUND_NOTIFICATION_CHANNEL_ID)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.drawable.list)
                    .setContentTitle("Установлен профиль")
                    .setContentText(state.getName())
                    .setAutoCancel(true)
                    .setContentIntent(notifyPendingIntent);
            assert notificationManager != null;
            notificationManager.notify(state.getId(), builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, STATE_NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.list)
                    .setContentTitle("Установлен профиль")
                    .setContentText(state.getName())
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setContentIntent(notifyPendingIntent);
            assert notificationManager != null;
            notificationManager.notify(state.getId(), builder.build());
        }

    }


    private void sendNotification(Context context, Note note) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("myPrefs",
                Context.MODE_PRIVATE);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(NOTE_NOTIFICATION_CHANNEL_ID,
                    "Note notifications", NotificationManager.IMPORTANCE_DEFAULT);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel 01");
            notificationChannel.setSound(null, null);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);

            notificationChannel = new NotificationChannel(NOTE_SOUND_NOTIFICATION_CHANNEL_ID,
                    "Note notifications", NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel 02");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);

        }


        Intent notifyIntent = new Intent(context, ShowActivity.class);
        notifyIntent.putExtra("type", "Note");
        notifyIntent.putExtra("name", note.getName());
        notifyIntent.putExtra("lat", note.getLat());
        notifyIntent.putExtra("lng", note.getLng());
        notifyIntent.putExtra("time", note.getStartTime());
        notifyIntent.putExtra("text", note.getText());
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );


        if (prefs.getBoolean(SOUND_NOTIF_NOTE_SETTING, true)) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTE_SOUND_NOTIFICATION_CHANNEL_ID)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.drawable.list)
                    .setContentTitle(note.getName())
                    .setContentText(note.getText())
                    .setAutoCancel(true)
                    .setContentIntent(notifyPendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            assert notificationManager != null;
            notificationManager.notify(note.getId(), builder.build());


        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTE_NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.list)
                    .setContentTitle(note.getName())
                    .setContentText(note.getText())
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setAutoCancel(true)
                    .setContentIntent(notifyPendingIntent);

            assert notificationManager != null;
            notificationManager.notify(note.getId(), builder.build());


        }


    }


    public void sendNotification() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(FOREGROUND_NOTIFICATION_CHANNEL_ID,
                    "Foreground notifications", NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription("Foreground channel");
            notificationChannel.enableLights(true);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);


        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), FOREGROUND_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.notificatoin_icon)
                .setContentTitle("SmartTool")
                .setContentText("Отслеживание местоположения")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        startForeground(231, builder.build());


    }


    private int numToPercent(float in, int max) {
        in = in / 100;
        return (int) (max * in);
    }


    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return dateFormat.format(new Date());
    }


    private State getLastState() {
        SharedPreferences prefs = getSharedPreferences("myPrefs",
                Context.MODE_PRIVATE);
        boolean wifi = prefs.getBoolean(WIFI_PREFS, false);
        boolean bluetooth = prefs.getBoolean(BLUETOOTH_PREFS, false);
        int media = prefs.getInt(MEDIA_PREFS, 100);
        int system = prefs.getInt(SYSTEM_PREFS, 100);
        return new State(wifi, bluetooth, media, system, "Пользовательский");
    }


    private void setLastState(State lastState) {

        SharedPreferences prefs = getSharedPreferences("myPrefs",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putBoolean(WIFI_PREFS, lastState.isWiFiState());
        ed.putBoolean(BLUETOOTH_PREFS, lastState.isBluetoothState());
        ed.putInt(MEDIA_PREFS, lastState.getMediaSoundState());
        ed.putInt(SYSTEM_PREFS, lastState.getSystemSoundState());
        ed.apply();
    }


    private State getCurrentState() {
        return new State(wifiManager.isWifiEnabled(), btAdapter.isEnabled(),
                audioManager.getStreamVolume(AudioManager.STREAM_MUSIC),
                audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
    }


}
