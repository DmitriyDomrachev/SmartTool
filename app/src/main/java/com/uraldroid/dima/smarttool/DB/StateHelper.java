package com.uraldroid.dima.smarttool.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.uraldroid.dima.smarttool.State;

import java.util.ArrayList;

import static com.uraldroid.dima.smarttool.DB.StateDBHelper.NUM_COLUMN_LAT;
import static com.uraldroid.dima.smarttool.DB.StateDBHelper.NUM_COLUMN_LNG;
import static com.uraldroid.dima.smarttool.DB.StateDBHelper.NUM_COLUMN_MEDIA_SOUND;
import static com.uraldroid.dima.smarttool.DB.StateDBHelper.NUM_COLUMN_SYSTEM_SOUND;
import static com.uraldroid.dima.smarttool.DB.StateDBHelper.NUM_COLUMN_TIME_START;
import static com.uraldroid.dima.smarttool.DB.StateDBHelper.TABLE_NAME;

/**
 * Created by dima on 25.03.2018.
 */

public class StateHelper {

    SQLiteDatabase db; // объект для манипулирования данными в базе.

    public StateHelper(Context context) {
        StateDBHelper dbHelper = new StateDBHelper(context); //инициализируем нашего помошника базы данных
        db = dbHelper.getWritableDatabase(); // получаем доступ к базе с возможностью записи/чтения
    }

    //метод для того, чтобы положить данные в базу
    public long insert(String name, boolean wifi, boolean bluetooth, long startTime, int mediaSound, int systemSound, double lat, double lng) {
        ContentValues cv = new ContentValues();// хранилище с принципом ключ-значени

        cv.put(StateDBHelper.COLUMN_NAME, name);
        cv.put(StateDBHelper.COLUMN_TIME_START, startTime);
        cv.put(StateDBHelper.COLUMN_WIFI, boolToInt(wifi));
        cv.put(StateDBHelper.COLUMN_BLUETOOTH, boolToInt(bluetooth));
        cv.put(StateDBHelper.COLUMN_MEDIA_SOUND, mediaSound);
        cv.put(StateDBHelper.COLUMN_SYSTEM_SOUND, systemSound);
        cv.put(StateDBHelper.COLUMN_LAT, lat);
        cv.put(StateDBHelper.COLUMN_LNG, lng);
        Log.d("DB","insert: "+name+" "+startTime+" "+wifi+" "+bluetooth+" "+mediaSound+" "+systemSound+" "+lat+" "+lng);

        return db.insert(TABLE_NAME, null, cv); // метод insert возвращает id, помещенного объекта в таблицу.

        // указали имя таблицы и хранилище данных
    }

    //метод для получения всех записей из таблицы
    public ArrayList<State> getAll() {

        Cursor mCursor = db.query(TABLE_NAME, null, null, null, null, null,
                null);
        ArrayList<State> arr = new ArrayList<>();


        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {

            //деай пока записи есть в таблице
            do {
                long id = mCursor.getLong(StateDBHelper.NUM_COLUMN_ID);
                String name = mCursor.getString(StateDBHelper.NUM_COLUMN_NAME);
                boolean wifi = intToBool(mCursor.getInt(StateDBHelper.NUM_COLUMN_WIFI));
                boolean bluetooth = intToBool(mCursor.getInt(StateDBHelper.NUM_COLUMN_BLUETOOTH));
                long startTime = mCursor.getLong(NUM_COLUMN_TIME_START);
                int mediaSound = mCursor.getInt(NUM_COLUMN_MEDIA_SOUND);
                int systemSound = mCursor.getInt(NUM_COLUMN_SYSTEM_SOUND);
                double lat = mCursor.getDouble(NUM_COLUMN_LAT);
                double lng = mCursor.getDouble(NUM_COLUMN_LNG);
                Log.d("DB","get: "+name+" "+wifi+" "+bluetooth);


                // получем значения соотвествующих полей и формируем объект, добавив его в коллекцию.
                arr.add(new State((int)id, name, wifi, bluetooth, mediaSound,systemSound, startTime, lat, lng));



            } while (mCursor.moveToNext());
        }

        db.close(); // закрыли транзакцию
        return arr; // вернули коллекцию
    }

    public void deleteState (String id){
        Log.d("DB", "delete id = "+id);
        db.delete(TABLE_NAME, StateDBHelper.COLUMN_ID+"=?",new String[] { id });
    }

    private int boolToInt(boolean in){
        if (in) return 2;
        else return 1;
    }

    private boolean intToBool(int in){
        if (in==2) return true;
        else return false;

    }

}
