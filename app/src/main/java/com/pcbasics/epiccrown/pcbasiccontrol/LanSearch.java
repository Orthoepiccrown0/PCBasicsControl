package com.pcbasics.epiccrown.pcbasiccontrol;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.Formatter;
import android.view.ContextMenu;
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
    ArrayList<String> fav_list = new ArrayList<>();
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
        ListView favs = findViewById(R.id.favorites);
        favs.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                user_ip = (fav_list.get(position).contains("/")) ?  fav_list.get(position).replace("/",""): fav_list.get(position);
                Intent intent = new Intent(LanSearch.this,Control.class);
                intent.putExtra("ip",user_ip);
                startActivity(intent);
            }});
        registerForContextMenu(favs);
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
                }else if(msg.arg1==4){
                    Toast.makeText(LanSearch.this,"You inserted invalid IP",Toast.LENGTH_LONG).show();
                }
            }
        };
        findFavs();
        refresh();
    }

    private void findFavs(){
        DataHelper helper = new DataHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query("IP_FAVOURITE",new String[]{"IP"},null,null,null,null,null);
        fav_list.clear();
        if(cursor.moveToFirst()){
            TextView text = findViewById(R.id.favs_text);
            text.setVisibility(View.VISIBLE);
            fav_list.add(cursor.getString(0));
            while(cursor.moveToNext())
                fav_list.add(cursor.getString(0));
            publishFavoriteIPs();
        }else{
            ListView favs = findViewById(R.id.favorites);
            favs.setAdapter(null);
            TextView text = findViewById(R.id.favs_text);
            text.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getId() == R.id.favorites){
            //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle("Your want to..");
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for(int i = 0;i<menuItems.length;i++){
                menu.add(Menu.NONE,i,i,menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String selectedIP = fav_list.get(info.position);
        String[] menuItems = getResources().getStringArray(R.array.menu);
        String selectedOperation = menuItems[item.getItemId()];
        proceedOperation(selectedIP,selectedOperation);
        findFavs();
        return true;
    }

    private void proceedOperation(String selectedIP, String operation) {
        if(!selectedIP.equals("")){
            DataHelper helper;
            SQLiteDatabase db;
            Cursor cursor;
            switch (operation){
                case "Edit":
                     helper = new DataHelper(this);
                     db = helper.getReadableDatabase();
                     cursor = db.query("IP_FAVOURITE",new String[]{"IP"},"IP=?",new String[]{selectedIP},null,null,null);
                    if(cursor.moveToFirst()){
                        editFav(cursor.getString(0));
                    }
                    break;
                case "Delete":
                     helper = new DataHelper(this);
                     db = helper.getReadableDatabase();
                     cursor = db.query("IP_FAVOURITE",new String[]{"IP"},"IP=?",new String[]{selectedIP},null,null,null);
                    if(cursor.moveToFirst()){
                        String query ="DELETE from IP_FAVOURITE WHERE IP='"+selectedIP+"'";
                        db.execSQL(query);
                    }
                    break;
            }
        }
    }

    private void editFav(final String fav){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("IP Address");


        final EditText input =(EditText) LayoutInflater.from(this).inflate(R.layout.ipinsert,null);
        input.setText(fav);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user_ip = input.getText().toString();
                if(validIP(user_ip)) {
                    DataHelper helper = new DataHelper(LanSearch.this);
                    SQLiteDatabase db = helper.getReadableDatabase();
                    String query = "UPDATE IP_FAVOURITE SET IP='"+user_ip+"' where IP='"+fav+"'";
                    db.execSQL(query);
                    findFavs();
                }else{
                    Message msg = Message.obtain();
                    msg.arg1 = 4;
                    handler.sendMessage(msg);
                }

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

    public void publishFavoriteIPs(){
        ListView listView = findViewById(R.id.favorites);
        ArrayAdapter<String> adapter = new ArrayAdapter(LanSearch.this,
                android.R.layout.simple_list_item_1, fav_list){

        };
        listView.setAdapter(adapter);
    }

    public void onUserIP(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("IP Address");

        final EditText input =(EditText) LayoutInflater.from(this).inflate(R.layout.ipinsert,null);

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

                            user_ip = input.getText().toString();
                            if(validIP(user_ip)) {
                                Message msg = Message.obtain();
                                msg.arg1 = 2;
                                handler.sendMessage(msg);
                            }else{
                                Message msg = Message.obtain();
                                msg.arg1 = 4;
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
        builder.setNeutralButton("Add to Favorite", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user_ip = input.getText().toString();
                if(validIP(user_ip)) {
                    DataHelper helper = new DataHelper(LanSearch.this);
                    SQLiteDatabase db = helper.getReadableDatabase();
                    DataHelper.insertIP(db,user_ip);
                    Message msg = Message.obtain();
                    msg.arg1 = 2;
                    handler.sendMessage(msg);
                }else{
                    Message msg = Message.obtain();
                    msg.arg1 = 4;
                    handler.sendMessage(msg);
                }
            }
        });

        builder.show();
    }

    public static boolean validIP (String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if ( ip.endsWith(".") ) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        findFavs();
        refresh();
    }
}
