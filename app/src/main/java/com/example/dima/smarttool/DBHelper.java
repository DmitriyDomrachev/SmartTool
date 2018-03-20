package com.example.dima.smarttool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dima on 20.03.2018.
 */

//класс для инициализации нашей базы данных, а так же её обновления в случаии добавления новых полей
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "statesDB.db";
    public static final String TABLE_NAME = "states";
    public static int DATABASE_VERSION = 1;

    //имена для колонок таблицы контактов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONDITION = "condition";
    public static final String COLUMN_WIFI = "wifi";
    public static final String COLUMN_BLUETOOTH = "bluetooth";
    public static final String COLUMN_MOBILE = "mobile";
    public static final String COLUMN_SOUND = "sound";
    public static final String COLUMN_BATTERY = "battery";
    public static final String COLUMN_TIMEWRITE = "timewrite";

    //индексы расположения этих колонок в таблице
    public static final int NUM_COLUMN_ID = 0;
    public static final int NUM_COLUMN_CONDITION = 1;
    public static final int NUM_COLUMN_WIFI= 2;
    public static final int NUM_COLUMN_BLUETOOTH = 3;
    public static final int NUM_COLUMN_MOBILE = 4;
    public static final int NUM_COLUMN_SOUND = 5;
    public static final int NUM_COLUMN_BATTERY = 6;
    public static final int NUM_COLUMN_TIMEWRITE = 7;

    //конструктор, куда передаётся контекст, имя базы и её версия
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //при помомощи DDL выполняем иницицализацию первой таблицы в нашей базе данных
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CONDITION + " INTEGER, " +
                COLUMN_WIFI + " NUMERIC, " +
                COLUMN_BLUETOOTH + " NUMERIC, " +
                COLUMN_MOBILE + " NUMERIC, " +
                COLUMN_SOUND + " NUMERIC, " +
                COLUMN_BATTERY + " NUMERIC, " +
                COLUMN_TIMEWRITE + "TIMESTAMP localtime); ";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
