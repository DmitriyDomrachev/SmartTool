package com.example.dima.smarttool.receiver;

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

import com.example.dima.smarttool.DB.HistoryHelper;
import com.example.dima.smarttool.DB.NoteHelper;
import com.example.dima.smarttool.Note;
import com.example.dima.smarttool.R;
import com.example.dima.smarttool.activity.ShowActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.dima.smarttool.fragment.SettingFragment.SOUND_NOTIF_NOTE_SETTING;

public class NoteAlarmReceiver extends BroadcastReceiver {
    private static final String NOTIFICATION_CHANNEL_ID = "note_notification_channel";
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
            hh.insert("Напоминание: " + note.getName() + "\nВремя включения: " + getDate());
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
            if (prefs.getBoolean(SOUND_NOTIF_NOTE_SETTING, true)) {

                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        "Note notifications", NotificationManager.IMPORTANCE_HIGH);
                // Configure the notification channel.
                notificationChannel.setDescription("Channel description");
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);

                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(notificationChannel);
                }
            } else {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        "Note notifications", NotificationManager.IMPORTANCE_LOW);
                // Configure the notification channel.
                notificationChannel.setDescription("Channel description");
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);

                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(notificationChannel);
                }
            }
        }


        Intent notifyIntent = new Intent(context, ShowActivity.class);
        notifyIntent.putExtra("type", "Note");
        notifyIntent.putExtra("name", note.getName());
        notifyIntent.putExtra("text", note.getText());
        notifyIntent.putExtra("lat", note.getLat());
        notifyIntent.putExtra("lng", note.getLng());
        notifyIntent.putExtra("time", note.getStartTime());
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.note)
                .setContentTitle("Напоминание")
                .setContentText(note.getName())
                .setAutoCancel(true)
                .setContentIntent(notifyPendingIntent);
        assert notificationManager != null;
        notificationManager.notify(note.getId(), builder.build());
    }


}