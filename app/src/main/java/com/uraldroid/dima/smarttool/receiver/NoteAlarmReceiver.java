package com.uraldroid.dima.smarttool.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.uraldroid.dima.smarttool.DB.HistoryHelper;
import com.uraldroid.dima.smarttool.DB.NoteHelper;
import com.uraldroid.dima.smarttool.Note;
import com.uraldroid.dima.smarttool.R;
import com.uraldroid.dima.smarttool.activity.ShowActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.uraldroid.dima.smarttool.GeoService.NOTE_NOTIFICATION_CHANNEL_ID;
import static com.uraldroid.dima.smarttool.GeoService.NOTE_SOUND_NOTIFICATION_CHANNEL_ID;
import static com.uraldroid.dima.smarttool.fragment.SettingFragment.SOUND_NOTIF_NOTE_SETTING;



public class NoteAlarmReceiver extends BroadcastReceiver {
    static Map<String, Note> noteTimeMap = new HashMap<String, Note>();                       //зраниение значиний времени старта и номера состояния
    static SharedPreferences prefs;
    static NotificationManager notificationManager;
    String name;
    Note note;

    @Override
    public void onReceive(Context context, Intent intent) {

        name = intent.getStringExtra("nameEditText");
        Log.d("alarm", "receiveNote");
        NoteHelper nh = new NoteHelper(context);                                     // инициализация помощника управления состояниямив базе данных
        loadNotes(nh.getAll());
        prefs = context.getSharedPreferences("myPrefs",
                Context.MODE_PRIVATE);


        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);


        if (noteTimeMap.get(name) != null) {
            this.note = (noteTimeMap.get(name));
            Log.d("alarm", "setNote: " + name);
            HistoryHelper hh = new HistoryHelper(context);
            hh.insert( context.getResources().getString(R.string.note) + ": " + note.getName()
                    + "\n" + context.getResources().getString(R.string.start_at)+ ": " + getDate());
            sendNotification(context);

        }

    }


    public void loadNotes(ArrayList<Note> notes) {
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            noteTimeMap.put(note.getName(), note);
            Log.d("timeS", "loadNote: " + note.getName());
        }

    }

    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return dateFormat.format(new Date());
    }


    private void sendNotification(Context context) {

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


}