package com.pcbasics.epiccrown.pcbasiccontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Control extends AppCompatActivity {

    public String user_ip = "";
    public Socket clientSocket;
    DataOutputStream outToServer;

    /*  CODSET
        0x19 - pause
        0x20 - play
        0x21 - stop
        0x22 - prev
        0x23 - next
        0x24 - screenOff
        0x25 - screenOn
        END CODSET
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        user_ip = getIntent().getStringExtra("ip");
        tryConnect();
    }

    private void tryConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket(user_ip, 38745);
                    outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    outToServer.writeUTF("Connected");
                }catch (Exception ex){ex.printStackTrace();
                }
            }
        }).start();

    }

    public void onPauseB(View view){
        sendCommand("0x19");
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

    public void onScreenOff(View view){
        sendCommand("0x24");
    }

    public void onScreenOn(View view){
        sendCommand("0x25");
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
                }
            }
        }).start();
        else{
            Toast.makeText(Control.this,"You choosed wrond IP/PC, server cannot respond",Toast.LENGTH_LONG).show();
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
