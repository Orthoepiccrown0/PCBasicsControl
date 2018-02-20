package com.pcbasics.epiccrown.pcbasiccontrol;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

public class LanSearch extends AppCompatActivity {
    ArrayList<String> list = new ArrayList<>();
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lan_search);
        setTitle("LAN Network");
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.arg1==1)
                    publishIPs();
            }
        };
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lan_actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refresh() {
        Toast.makeText(this,"Refreshing...",Toast.LENGTH_SHORT).show();
        final WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        list.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String ipString = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

                    byte[] ip = {0,0,0,0};
                    String[] ipSArray = ipString.split("\\.");
                    for(int i=0;i<ipSArray.length;i++){
                        //int tmp = Integer.parseInt();
                        ip[i] = (byte)Integer.parseInt(ipSArray[i]);
                    }
                    for (int i = 2; i <= 254; i++)
                    {
                        ip[3] = (byte)i;
                        InetAddress address = InetAddress.getByAddress(ip);
                        if (address.isReachable(500))
                        {
                            if(!address.toString().equals(ipString));
                            {
                                list.add(address.toString());
                                Message msg = Message.obtain();
                                msg.arg1=1;
                                handler.sendMessage(msg);
                            }
                        }

                    }

                }catch (Exception ex){ex.printStackTrace();}
            }
        }).start();

    }

    public void publishIPs(){
        ListView listView = findViewById(R.id.lan_list);

        ArrayAdapter<String> adapter = new ArrayAdapter(LanSearch.this,
                android.R.layout.simple_list_item_1, list){

        };
        listView.setAdapter(adapter);
    }


}
