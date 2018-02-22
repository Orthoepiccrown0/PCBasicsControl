package com.pcbasics.epiccrown.pcbasiccontrol;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
    private String user_ip = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lan_search);
        setTitle("LAN Network");
        ListView myList = findViewById(R.id.lan_list);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                user_ip = (list.get(position).contains("/")) ?  list.get(position).replace("/",""): list.get(position);
                Intent intent = new Intent(LanSearch.this,Control.class);
                intent.putExtra("ip",user_ip);
                startActivity(intent);
            }});
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.arg1==1)
                    publishIPs();
                else if(msg.arg1==2){
                    Intent intent = new Intent(LanSearch.this,Control.class);
                    intent.putExtra("ip",user_ip);
                    startActivity(intent);
                }else if(msg.arg1==3){
                    Toast.makeText(LanSearch.this,"Cannoc connect to this host",Toast.LENGTH_LONG).show();
                }
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
        ListView listView = findViewById(R.id.lan_list);
        listView.setAdapter(null);
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
                        int z;
                        if(i==22)
                             z = 0;
                        if (address.isReachable(500))
                        {
                            if(!address.toString().replace("/","").equals(ipString));
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

    public void onUserIP(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("IP Address");

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            InetAddress address = InetAddress.getByName(input.getText().toString());
                            if(address.isReachable(500)) {
                                user_ip = input.getText().toString();
                                Message msg = Message.obtain();
                                msg.arg1 = 2;
                                handler.sendMessage(msg);
                            }else{
                                Message msg = Message.obtain();
                                msg.arg1 = 3;
                                handler.sendMessage(msg);
                            }
                        }catch (Exception ex){
                            ex.printStackTrace();}
                    }
                }).start();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


}
