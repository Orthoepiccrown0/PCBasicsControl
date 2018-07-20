package com.pcbasics.epiccrown.pcbasiccontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

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
//---------------------PREFERENCES------------------------------------------------------------------
    public static class Preferences {
        public final static String LAST_IP_USED = "LASTIP";
        private static final String skiphome = "homeskip";

    public static String getLastIp(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(LAST_IP_USED,  null);
    }

    public static void setLastIp(Context context, String ip) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(LAST_IP_USED, ip)
                .apply();
    }



    public static boolean isToSkipHome(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(skiphome,false);
    }

    public  static void setSkipHome(Context context, boolean isToSkip){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(skiphome,isToSkip)
                .apply();
    }
}
}
