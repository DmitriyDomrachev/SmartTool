package com.example.dima.smarttool.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dima on 24.04.2018.
 */

public class HistotyDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "historyDB.db";
    public static final String TABLE_NAME = "history";
    public static int DATABASE_VERSION = 1;

    //имена для колонок таблицы контактов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEXT = "name";

    //индексы расположения этих колонок в таблице
    public static final int NUM_COLUMN_ID = 0;
    public static final int NUM_COLUMN_TEXT = 1;


    //конструктор, куда передаётся контекст, имя базы и её версия
    public HistotyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //при помомощи DDL выполняем иницицализацию первой таблицы в нашей базе данных
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TEXT + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


}
