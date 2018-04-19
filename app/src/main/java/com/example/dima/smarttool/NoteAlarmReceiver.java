package com.example.dima.smarttool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.dima.smarttool.DB.NoteHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NoteAlarmReceiver extends BroadcastReceiver {
    static Map<String, Note> noteTimeMap = new HashMap<String, Note>();                       //зраниение значиний времени старта и номера состояния
    String name;


    @Override
    public void onReceive(Context context, Intent intent) {

        name = intent.getStringExtra("nameEditText");
        Log.d("alarm", "receive");
        NoteHelper nh = new NoteHelper(context);                                     // инициализация помощника управления состояниямив базе данных
        loadNotes(nh.getAll());
        Toast.makeText(context, "receive",
                Toast.LENGTH_LONG).show();
        if (noteTimeMap.get(name) != null) {
            setNote(noteTimeMap.get(name));
            Log.d("alarm", "setNote: " + name);
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

    private void setNote(Note note) {

        Log.d("timeS", "setNote: " + note.getName());

    }

    private int progInex(float in, int max) {
        in = in / 100;
        return (int) (max * in);
    }
}