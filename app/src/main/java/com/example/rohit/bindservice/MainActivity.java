package com.example.rohit.bindservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements bindservice.callbacks {

    private String TAG="mainact";
    private Button button;
    private boolean started =false;
    private bindservice service=null;
    private boolean bound=false;
    private Intent intent=null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(intent!=null) {
            unbindService(connection);
            stopService(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!started){
                    intent =new Intent(getBaseContext(), bindservice.class);
                    startService(intent);
                    bindService(intent,connection, Context.BIND_AUTO_CREATE);
                    started=true;
                }else {
                    unbindService(connection);
                    stopService(intent);
                    bound=false;
                    started=false;
                }

            }
        });


    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bindservice.localbinder binder = (bindservice.localbinder) iBinder;
            service = binder.getService();
            bound=true;
            Log.d(TAG, "onServiceConnected: ");
            service.setbound(true);
            service.setcallback(MainActivity.this);


        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound=false;
        }

    };


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void updateclient(int i) {
        this.getWindow().getDecorView().setBackgroundColor(i);
    }


}
