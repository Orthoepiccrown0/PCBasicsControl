package com.pcbasics.epiccrown.pcbasiccontrol;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class PC_Connection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pc_connection);
        getSupportActionBar().setElevation(0);
    }

    public void OnLanClick(View view){
        Intent intent = new Intent(this,LanSearch.class);
        startActivity(intent);
    }

    public void OnWanClick(View view){

    }
}
