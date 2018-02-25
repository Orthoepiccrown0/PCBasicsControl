package com.pcbasics.epiccrown.pcbasiccontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Epiccrown on 25.02.2018.
 */

public class DataHelper extends SQLiteOpenHelper {
    public static final String nameDB = "PCRemoteEpic";
    public static final int DB_VERSION = 1;

    public DataHelper(Context context) {
        super(context, nameDB, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "Create Table IP_FAVOURITE("+
                "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "IP TEXT);";
        db.execSQL(query);
    }

    public static void insertIP(SQLiteDatabase db,String IP){
        ContentValues values = new ContentValues();
        values.put("IP",IP);
        db.insert("IP_FAVOURITE",null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
