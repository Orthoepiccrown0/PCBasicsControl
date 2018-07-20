package com.pcbasics.epiccrown.pcbasiccontrol;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class PC_Connection extends AppCompatActivity {

    CheckBox isToSkip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        skip();
        setContentView(R.layout.activity_pc_connection);
        tryStyle();
        isToSkip = findViewById(R.id.skipLauncher);
        isToSkip.setChecked(DataHelper.Preferences.isToSkipHome(this));
        isToSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHelper.Preferences.setSkipHome(PC_Connection.this,isToSkip.isChecked());
            }
        });

    }

    private void skip() {
        if(DataHelper.Preferences.isToSkipHome(this)) {
            Intent intent = new Intent(this, LanSearch.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void tryStyle() {
        try {
            getSupportActionBar().setElevation(0);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void OnLanClick(View view){
        Intent intent = new Intent(this, LanSearch.class);
        startActivity(intent);
    }

    public void OnWanClick(View view){

    }
}
