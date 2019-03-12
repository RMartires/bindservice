package com.example.rohit.bindservice;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ROHIT on 3/7/2019.
 */

public class bindservice extends Service implements SensorEventListener{

    private String TAG="bindservice";
    private IBinder mbinder;
    private Sensor sensor;
    private SensorManager sensorManager;
    private Boolean chopgesturetrigerd=false, zgesturetrigered =false;
    private double chopprevtime=0, zprevtime =0;
    private IBinder Ibinder=new localbinder();
    private rx.Observable<String> observable;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        sensorManager.unregisterListener(this,sensor);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onDestroy: ");
        sensorManager.unregisterListener(this,sensor);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return Ibinder;
    }

    public boolean getchopgesturestate(){
        Log.d(TAG, "getchopgesturestate: ");
        return chopgesturetrigerd;
    }

    public boolean getzgesturestate(){
        return zgesturetrigered;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        double currrnttime = SystemClock.currentThreadTimeMillis();



        float ax = sensorEvent.values[0];
        float ay = sensorEvent.values[1];
        float az = sensorEvent.values[2];

        double temp = ax + ay;

        if ((chopgesturetrigerd | zgesturetrigered) == false) {


            if (temp < -25) {

                Log.d("shakeEvent", "onSensorChanged: " + temp);
                //Toast.makeText(getBaseContext(),"shake side",Toast.LENGTH_SHORT).show();

                chopgesturetrigerd=true;
                chopprevtime=currrnttime;

                observable.just("shake");
            }

            if((Math.sqrt(az*az))>20){

                Log.d("z", "onSensorChanged: " + (Math.sqrt(az*az)));
                //Toast.makeText(getBaseContext(),"shake up",Toast.LENGTH_SHORT).show();

                zgesturetrigered =true;
                zprevtime =currrnttime;

                observable.just("upshake");
            }

        }

        if(chopgesturetrigerd==true){
            double choptimeint = currrnttime-chopprevtime;
            if(choptimeint>100)
                chopgesturetrigerd=false;
        }

        if(zgesturetrigered ==true){
            double rolltimeint = currrnttime- zprevtime;
            if(rolltimeint>100)
                zgesturetrigered =false;
        }



    }

    public rx.Observable<String> getObservable(){
        return observable;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public class localbinder extends Binder{

        public bindservice getService(){
            return bindservice.this;
        }
    }


}
