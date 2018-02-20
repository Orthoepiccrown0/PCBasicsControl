package com.pcbasics.epiccrown.pcbasiccontrol;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Epiccrown on 19.02.2018.
 */

public class ServerConnection {

    public static String downloadJSON(String url_used) {
        String final_object = "0 results";
        try {
            URL url = new URL(url_used);
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;

            while ((str = in.readLine()) != null)
                final_object += str;

            in.close();

        } catch (Exception e) {
            e.printStackTrace();
            e.toString();
        }
        return final_object;
    }
}

