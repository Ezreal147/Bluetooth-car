package com.example.administrator.lanyaxiaoche;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class dialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(statereciever,intentFilter);
        Timer timer=new Timer();
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                //Toast.makeText(dialog.this,"连接失败",Toast.LENGTH_SHORT).show();
                finish();
            }
        };
        timer.schedule(timerTask,5000);
    }

    private BroadcastReceiver statereciever =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED)){
                Toast.makeText(dialog.this,"连接成功",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(statereciever);
    }
}
