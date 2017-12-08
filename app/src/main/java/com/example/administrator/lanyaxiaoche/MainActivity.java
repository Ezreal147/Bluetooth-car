package com.example.administrator.lanyaxiaoche;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.LocalSocket;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.system.Os;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener {

    private BluetoothAdapter localdevice = BluetoothAdapter.getDefaultAdapter();
    private Button scan;
    private Button forward;
    private Button back;
    private Button left;
    private Button right;
    private Button menu;
    private Button finish;
    private BluetoothDevice remotedevice;
    private BluetoothSocket socket;
    private final static UUID MYUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                        Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private void devicecheck(){
        if(localdevice==null){
            Toast.makeText(MainActivity.this,"设备不支持蓝牙",Toast.LENGTH_LONG).show();
        }
        if (!localdevice.isEnabled()){
            localdevice.enable();
        }
    }
    private void connectedreciever(){
        IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        IntentFilter intentFilter2=new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(connectedstate,intentFilter);
        registerReceiver(connectedstate,intentFilter2);
    }
    private void init_view() {
        scan = (Button) findViewById(R.id.scan);
        forward=(Button)findViewById(R.id.forward);
        back=(Button)findViewById(R.id.back);
        left=(Button)findViewById(R.id.left);
        right=(Button)findViewById(R.id.right);
        menu=(Button)findViewById(R.id.menu);
        finish=(Button)findViewById(R.id.finish);
    }

    private void init_click() {
        scan.setOnClickListener(this);
        finish.setOnClickListener(this);
        forward.setOnTouchListener(this);
        back.setOnTouchListener(this);
        left.setOnTouchListener(this);
        right.setOnTouchListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_view();
        init_click();
        devicecheck();
        connectedreciever();
    }

    private void send(final String str){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    DataOutputStream os=new DataOutputStream(socket.getOutputStream());
                    os.write(str.getBytes());
                }catch (IOException e){}
            }
        }).start();
    }
    private void connected() {
        try {
            socket = remotedevice.createInsecureRfcommSocketToServiceRecord(MYUUID);
        } catch (IOException e) {
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket.connect();
                } catch (IOException f) {
                    try {
                        Log.d("connneted state","fail");
                        socket.close();
                    } catch (IOException g) {
                    }
                    Message message=Message.obtain();
                    message.what=0;
                    message.obj="cancel";
                    mhandler.sendMessage(message);
                    return;
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan:
                if(socket==null||(!socket.isConnected())) {
                    Intent intent = new Intent(MainActivity.this, devicelist_activity.class);
                    startActivityForResult(intent, 0);
                }else {
                    try {
                        socket.close();
                    }catch (IOException e){}
                    scan.setText("搜\n索");
                }
                break;
            case R.id.finish:
                finish();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(socket!=null&&socket.isConnected()){
            switch (view.getId()){
                case R.id.forward:
                    if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                        send("1");
                    }
                    else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                        send("0");
                    }
                    break;
                case R.id.back:
                    if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                        send("2");
                    }
                    else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                        send("0");
                    }
                    break;
                case R.id.left:
                    if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                        send("3");
                    }
                    else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                        send("0");
                    }
                    break;
                case R.id.right:
                    if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                        send("4");
                    }
                    else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                        send("0");
                    }
                    break;
            }
        }else{
            Toast.makeText(MainActivity.this,"请先连接蓝牙设备",Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                remotedevice = data.getParcelableExtra("device");
                connected();
                Intent intent=new Intent(MainActivity.this,dialog.class);
                startActivity(intent);
            }
        }
    }

    private BroadcastReceiver connectedstate=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
                Toast.makeText(MainActivity.this,remotedevice.getName()+"断开连接",Toast.LENGTH_SHORT).show();
                scan.setText("搜\n索");
            }
            else if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED)){
                scan.setText("断\n开");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectedstate);
        Toast.makeText(MainActivity.this,"destroy",Toast.LENGTH_SHORT).show();
    }
}
