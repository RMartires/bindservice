package com.example.rohit.bindservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {

    private String TAG="mainact";
    private Button button;
    private boolean started =false;
    private bindservice service=null;
    private boolean bound=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!started){
                    Intent intent =new Intent(getBaseContext(), bindservice.class);
                    startService(intent);
                    bindService(intent,connection, Context.BIND_AUTO_CREATE);
                    started=true;
                }else {
                    unbindService(connection);
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

            if(service.getObservable()==null)
                Log.d(TAG, "onServiceConnected: errorbitch");

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound=false;
        }

    };


}
