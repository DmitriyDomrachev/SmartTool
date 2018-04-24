package com.example.dima.smarttool;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.dima.smarttool.DB.HistoryHelper;
import com.example.dima.smarttool.DB.NoteHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NoteAlarmReceiver extends BroadcastReceiver {
    private static final String NOTIFICATION_CHANNEL_ID = "note_notification_channel";
    static Map<String, Note> noteTimeMap = new HashMap<String, Note>();                       //зраниение значиний времени старта и номера состояния
    String name;
    Note note;

    @Override
    public void onReceive(Context context, Intent intent) {

        name = intent.getStringExtra("nameEditText");
        Log.d("alarm", "receiveNote");
        NoteHelper nh = new NoteHelper(context);                                     // инициализация помощника управления состояниямив базе данных
        loadNotes(nh.getAll());


        final NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500,
                    1000});

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        if (noteTimeMap.get(name) != null) {
            this.note = (noteTimeMap.get(name));
            Log.d("alarm", "setNote: " + name);
            HistoryHelper hh = new HistoryHelper(context);
            hh.insert("Заметка: "+ note.getText()+ "\nВремя включения: "+ getTime());


            Intent notifyIntent = new Intent(context, ShowNoteActivity.class);
            notifyIntent.putExtra("name", note.getName());
            notifyIntent.putExtra("note", note.getText());
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                    context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
            );


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.drawable.note)
                    .setContentTitle("Note time")
                    .setContentText("Note: " + note.getName())
                    .setAutoCancel(true)
                    .setContentIntent(notifyPendingIntent);
            notificationManager.notify(note.getId(), builder.build());
        }

    }

    private String getTime() {                                                          // используйте метод для вывода текущего времени
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        date.setTime(calendar.getTimeInMillis());
        return date.getHours() + ":" + date.getMinutes();
    }

    public void loadNotes(ArrayList<Note> notes) {
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            noteTimeMap.put(note.getName(), note);
            Log.d("timeS", "loadNote: " + note.getName());
        }

    }


    private int progInex(float in, int max) {
        in = in / 100;
        return (int) (max * in);
    }

    private void sendNotification(String name) {

    }

}