package com.example.administrator.lanyaxiaoche;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

public class devicelist_activity extends AppCompatActivity implements AdapterView.OnItemClickListener{


    private BluetoothAdapter localdevice=BluetoothAdapter.getDefaultAdapter();
    Set<BluetoothDevice> pairedDevices = localdevice.getBondedDevices();
    private ListView deviceView;
    private ArrayAdapter deviceadapter;
    private ArrayList<String> devicename=new ArrayList<>();
    private ArrayList<BluetoothDevice> devicelist=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicelist_activity);
        deviceView=(ListView)findViewById(R.id.devicelist);
        deviceView.setOnItemClickListener(this);
        devicelist.clear();
        devicename.clear();
        scanexistdevice();
        localdevice.startDiscovery();
        IntentFilter intentFilter= new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(devicereciever,intentFilter);
    }

    private void scanexistdevice(){
        if(pairedDevices.size()>0){
            for(BluetoothDevice device:pairedDevices){
                devicelist.add(device);
                if(device.getName()==null){
                    devicename.add(device.getAddress());
                }else{
                    devicename.add(device.getName());
                }
            }
        }
        deviceadapter=new ArrayAdapter(devicelist_activity.this,android.R.layout.simple_list_item_1,devicename);
        deviceView.setAdapter(deviceadapter);
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        localdevice.cancelDiscovery();
        BluetoothDevice connectingdevice=devicelist.get(i);
        Intent intent=new Intent();
        intent.putExtra("device",connectingdevice);
        setResult(RESULT_OK,intent);
        finish();
    }

    private final BroadcastReceiver devicereciever=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BluetoothDevice.ACTION_FOUND)){
                Log.d("tag", "onReceive");
               BluetoothDevice remotedevice=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
               if(!devicelist.contains(remotedevice)){
                   devicelist.add(remotedevice);
                   if(remotedevice.getName()!=null) {
                        devicename.add(remotedevice.getName());
                   }else {
                       devicename.add(remotedevice.getAddress());
                   }
                   deviceadapter=new ArrayAdapter(devicelist_activity.this,android.R.layout.simple_list_item_1,devicename);
                   deviceView.setAdapter(deviceadapter);
               }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(devicereciever);
    }
}
