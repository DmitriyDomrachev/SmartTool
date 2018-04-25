package com.example.dima.smarttool.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.dima.smarttool.Note;

import java.util.ArrayList;

import static com.example.dima.smarttool.DB.NoteDBHelper.NUM_COLUMN_ID;
import static com.example.dima.smarttool.DB.NoteDBHelper.NUM_COLUMN_LAT;
import static com.example.dima.smarttool.DB.NoteDBHelper.NUM_COLUMN_LNG;
import static com.example.dima.smarttool.DB.NoteDBHelper.NUM_COLUMN_NAME;
import static com.example.dima.smarttool.DB.NoteDBHelper.NUM_COLUMN_TEXT;
import static com.example.dima.smarttool.DB.NoteDBHelper.NUM_COLUMN_TIME_START;
import static com.example.dima.smarttool.DB.NoteDBHelper.TABLE_NAME;

/**
 * Created by dima on 19.04.2018.
 */

public class NoteHelper {
    SQLiteDatabase db; // объект для манипулирования данными в базе.

    public NoteHelper(Context context) {
        NoteDBHelper dbHelper = new NoteDBHelper(context); //инициализируем нашего помошника базы данных
        db = dbHelper.getWritableDatabase(); // получаем доступ к базе с возможностью записи/чтения
    }

    //метод для того, чтобы положить данные в базу
    public long insert(String name, String text, long startTime, double lat, double lng) {
        ContentValues cv = new ContentValues();// хранилище с принципом ключ-значени

        cv.put(NoteDBHelper.COLUMN_NAME, name);
        cv.put(NoteDBHelper.COLUMN_TEXT, text);
        cv.put(NoteDBHelper.COLUMN_TIME_START, startTime);
        cv.put(NoteDBHelper.COLUMN_LAT, lat);
        cv.put(NoteDBHelper.COLUMN_LNG, lng);
        
        Log.d("DB", "insert: " + name + " " + text + " " + startTime + " " + lat + " " + lng);

        return db.insert(TABLE_NAME, null, cv); // метод insert возвращает id, помещенного объекта в таблицу.

        // указали имя таблицы и хранилище данных
    }

    //метод для получения всех записей из таблицы
    public ArrayList<Note> getAll() {

        Cursor mCursor = db.query(TABLE_NAME, null, null, null, null, null,
                null);
        ArrayList<Note> arr = new ArrayList<>();


        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {

            //деай пока записи есть в таблице
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                String name = mCursor.getString(NUM_COLUMN_NAME);
                String text = mCursor.getString(NUM_COLUMN_TEXT);
                long startTime = mCursor.getLong(NUM_COLUMN_TIME_START);
                double lat = mCursor.getDouble(NUM_COLUMN_LAT);
                double lng = mCursor.getDouble(NUM_COLUMN_LNG);
                Log.d("DB", "get: " + name);


                // получем значения соотвествующих полей и формируем объект, добавив его в коллекцию.
                arr.add(new Note((int) id, name, text, lat, lng, startTime));


            } while (mCursor.moveToNext());
        }

        db.close(); // закрыли транзакцию
        return arr; // вернули коллекцию
    }

    public void deleteState(String id) {
        Log.d("DB", "delete id = " + id);
        db.delete(TABLE_NAME, NoteDBHelper.COLUMN_ID + "=?", new String[]{id});
    }


}

