package com.pcbasics.epiccrown.pcbasiccontrol;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Control extends AppCompatActivity {

    public String user_ip = "";
    public Socket clientSocket;
    public static String timeToWait = "";
    public String volume_level = "0";
    DataOutputStream outToServer;
    DataInputStream fromServer;

    /*  CODSET
        0x19 - pause
        0x20 - play
        0x21 - stop
        0x22 - prev
        0x23 - next
        0x24 - screenOff
        0x25 - screenOn
        0x26 - timed
        0x27 - shutdown
        0x28 - restart
        0x29 - stand by
        0x30 - mute
        0x31 - cancel
        0x32 - vol up
        0x33 - vol down
        END CODSET
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        user_ip = getIntent().getStringExtra("ip");
        tryConnect();

        /*ImageButton down = findViewById(R.id.downVol);
        down.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        ImageButton up = findViewById(R.id.up);
        up.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });*/
    }

    private void tryConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket(user_ip, 38745);
                    outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    //fromServer = new DataInputStream(clientSocket.getInputStream());
                    outToServer.writeUTF("Connected");
                    //volume_level = fromServer.readUTF();
                }catch (Exception ex){ex.printStackTrace();
                }
            }
        }).start();

    }

    public void onPauseB(View view){
        sendCommand("0x19");
    }

    public void onMuteB(View view){
        sendCommand("0x30");
    }

    public void onPlayB(View view){
        sendCommand("0x20");
    }

    public void onStopB(View view){
        sendCommand("0x21");
    }

    public void onPrevB(View view){
        sendCommand("0x22");
    }

    public void onnextB(View view){
        sendCommand("0x23");
    }

    public void onCancelB(View view){
        sendCommand("0x31");
    }

    public void onScreenOff(View view){
        sendCommand("0x24");
    }

    public void onScreenOn(View view){
        sendCommand("0x25");
    }

    public void onVolumeUp(View view){
        sendCommand("0x32");
    }

    public void onVolumeDown(View view){
        sendCommand("0x33");
    }



    public void onSleep(View view){
        if(timeToWait.equals(""))
        sendCommand("0x29");
        else{
            sendCommand("0x26;"+timeToWait+";0x29");
            timeToWait="";
        }
    }

    public void onShutdown(View view){
        if(timeToWait.equals(""))
        sendCommand("0x27");
        else{
            sendCommand("0x26;"+timeToWait+";0x27");
            timeToWait="";
        }
    }

    public void onRestart(View view){
        if(timeToWait.equals(""))
        sendCommand("0x28");
        else{
            sendCommand("0x26;"+timeToWait+";0x28");
            timeToWait="";
        }
    }


    public void onChooseTime(View view){
        new PickerDialogFragment().show(getFragmentManager(), "dialog");
    }

    private void sendCommand(final String command){
        if(outToServer!=null)
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    outToServer.writeUTF(command);

                } catch (IOException e) {
                    e.printStackTrace();
                    tryConnect();
                }
            }
        }).start();
        else{
            Toast.makeText(Control.this,"Server cannot respond",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(clientSocket!=null&&outToServer!=null)
        try {
            clientSocket.close();
            outToServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
