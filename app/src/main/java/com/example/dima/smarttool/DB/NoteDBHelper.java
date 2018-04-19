package com.example.dima.smarttool.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dima on 19.04.2018.
 */

public class NoteDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "notesDB.db";
    public static final String TABLE_NAME = "notes";
    public static int DATABASE_VERSION = 1;

    //имена для колонок таблицы контактов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TEXT = "wifi";
    public static final String COLUMN_TIME_START = "time_start";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";

    //индексы расположения этих колонок в таблице
    public static final int NUM_COLUMN_ID = 0;
    public static final int NUM_COLUMN_NAME = 1;
    public static final int NUM_COLUMN_TEXT = 2;
    public static final int NUM_COLUMN_TIME_START = 3;
    public static final int NUM_COLUMN_LAT = 4;
    public static final int NUM_COLUMN_LNG = 5;

    //конструктор, куда передаётся контекст, имя базы и её версия
    public NoteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //при помомощи DDL выполняем иницицализацию первой таблицы в нашей базе данных
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_TEXT + " TEXT, " +
                COLUMN_TIME_START + " INTEGER, " +
                COLUMN_LAT + " BLOB, " +
                COLUMN_LNG + " BLOB);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
