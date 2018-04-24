package com.example.dima.smarttool.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import static com.example.dima.smarttool.DB.HistotyDBHelper.COLUMN_TEXT;
import static com.example.dima.smarttool.DB.HistotyDBHelper.NUM_COLUMN_TEXT;
import static com.example.dima.smarttool.DB.HistotyDBHelper.TABLE_NAME;

/**
 * Created by dima on 24.04.2018.
 */

public class HistoryHelper {
    SQLiteDatabase db; // объект для манипулирования данными в базе.

    public HistoryHelper(Context context) {
        HistotyDBHelper dbHelper = new HistotyDBHelper(context); //инициализируем нашего помошника базы данных
        db = dbHelper.getWritableDatabase(); // получаем доступ к базе с возможностью записи/чтения
    }

    //метод для того, чтобы положить данные в базу
    public long insert(String text) {
        ContentValues cv = new ContentValues();// хранилище с принципом ключ-значени
        cv.put(COLUMN_TEXT, text);


        Log.d("DB", "insert: "+ text);

        return db.insert(TABLE_NAME, null, cv); // метод insert возвращает id, помещенного объекта в таблицу.

        // указали имя таблицы и хранилище данных
    }

    //метод для получения всех записей из таблицы
    public ArrayList<String> getAll() {

        Cursor mCursor = db.query(TABLE_NAME, null, null, null, null, null,
                null);
        ArrayList<String> arr = new ArrayList<>();


        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {

            //деай пока записи есть в таблице
            do {
                String text = mCursor.getString(NUM_COLUMN_TEXT);
                Log.d("DB", "get: " + text);

                // получем значения соотвествующих полей и формируем объект, добавив его в коллекцию.
                arr.add(text);


            } while (mCursor.moveToNext());
        }

        db.close(); // закрыли транзакцию
        return arr; // вернули коллекцию
    }




}
