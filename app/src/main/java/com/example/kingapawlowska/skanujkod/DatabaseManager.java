package com.example.kingapawlowska.skanujkod;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kinga on 2016-09-28.
 */
public class DatabaseManager extends SQLiteOpenHelper {

    public DatabaseManager(Context context) {
        super(context, "wyszukania.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table wyszukania(" +
                        "nr integer primary key autoincrement," +
                        "wyszukanie text," +
                        "data text);" +
                        "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public void addResult(String result, String data) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues wartosci = new ContentValues();
        wartosci.put("wyszukanie", result);
        wartosci.put("data", data);
        db.insertOrThrow("wyszukania", null, wartosci);
    }

    public Cursor giveAll() {
        String[] kolumny = {"nr", "wyszukanie", "data"};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("wyszukania", kolumny, null, null, null, null, null);
        return cursor;
    }
}
