package com.uraldroid.dima.smarttool.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dima on 25.03.2018.
 */

//класс для инициализации нашей базы данных, а так же её обновления в случаии добавления новых полей
public class StateDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "statesDB.db";
    public static final String TABLE_NAME = "states";
    public static int DATABASE_VERSION = 1;

    //имена для колонок таблицы контактов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TIME_START = "time_start";
    public static final String COLUMN_WIFI = "wifi";
    public static final String COLUMN_BLUETOOTH = "bluetooth";
    public static final String COLUMN_MEDIA_SOUND = "media_sound";
    public static final String COLUMN_SYSTEM_SOUND = "system_sound";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";

    //индексы расположения этих колонок в таблице
    public static final int NUM_COLUMN_ID = 0;
    public static final int NUM_COLUMN_NAME = 1;
    public static final int NUM_COLUMN_TIME_START = 2;
    public static final int NUM_COLUMN_WIFI= 3;
    public static final int NUM_COLUMN_BLUETOOTH = 4;
    public static final int NUM_COLUMN_MEDIA_SOUND = 5;
    public static final int NUM_COLUMN_SYSTEM_SOUND = 6;
    public static final int NUM_COLUMN_LAT = 7;
    public static final int NUM_COLUMN_LNG = 8;

    //конструктор, куда передаётся контекст, имя базы и её версия
    public StateDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //при помомощи DDL выполняем иницицализацию первой таблицы в нашей базе данных
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_TIME_START + " INTEGER, " +
                COLUMN_WIFI + " INTEGER, " +
                COLUMN_BLUETOOTH + " INTEGER, " +
                COLUMN_MEDIA_SOUND + " INTEGER, " +
                COLUMN_SYSTEM_SOUND + " INTEGER, " +
                COLUMN_LAT + " BLOB, " +
                COLUMN_LNG + " BLOB);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
