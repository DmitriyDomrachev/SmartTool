package com.example.dima.smarttool.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.dima.smarttool.State;

import java.util.ArrayList;

import static com.example.dima.smarttool.DB.DBHelper.TABLE_NAME;

/**
 * Created by dima on 25.03.2018.
 */

public class StateHelper {

    SQLiteDatabase db; // объект для манипулирования данными в базе.

    public StateHelper(Context context) {
        DBHelper dbHelper = new DBHelper(context); //инициализируем нашего помошника базы данных
        db = dbHelper.getWritableDatabase(); // получаем доступ к базе с возможностью записи/чтения
    }

    //метод для того, чтобы положить данные в базу
    public long insert(String name, long condition, boolean wifi, boolean bluetooth, boolean mobile) {
        ContentValues cv = new ContentValues();// хранилище с принципом ключ-значени

        cv.put(DBHelper.COLUMN_NAME, name);
        cv.put(DBHelper.COLUMN_CONDITION, condition);
        cv.put(DBHelper.COLUMN_WIFI, wifi);
        cv.put(DBHelper.COLUMN_BLUETOOTH, bluetooth);
        cv.put(DBHelper.COLUMN_MOBILE, mobile);

        return db.insert(TABLE_NAME, null, cv);// метод insert возвращает id, помещенного объекта в таблицу.
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
                long id = mCursor.getLong(DBHelper.NUM_COLUMN_ID);
                String name = mCursor.getString(DBHelper.NUM_COLUMN_NAME);
//                boolean wifi = mCursor.get

                // получем значения соотвествующих полей и формируем объект, добавив его в коллекцию.
                arr.add(new State(name, true, true, true, 46,47));



            } while (mCursor.moveToNext());
        }
        db.close(); // закрыли транзакцию
        return arr; // вернули коллекцию
    }

    public int getCount (){
        return getAll().size();
    }

//    public void updateContact (String id,String name, String phone, String birthday){
//        Log.d("test", "update id = "+id+" name "+name);
//        ContentValues cv = new ContentValues();// хранилище с принципом ключ-значени
//        cv.put(DBHelper.COLUMN_NAME, name);
//        cv.put(DBHelper.COLUMN_PHONE, phone);
//        cv.put(DBHelper.COLUMN_BIRTHDAY, birthday);
//        db.update(TABLE_NAME,cv,DBHelper.COLUMN_ID+"=?",new String[] { id });
//    }
//
//    public void deleteContact (String id){
//        Log.d("test", "delete id = "+id);
//        db.delete(TABLE_NAME,DBHelper.COLUMN_ID+"=?",new String[] { id });
//    }

}
