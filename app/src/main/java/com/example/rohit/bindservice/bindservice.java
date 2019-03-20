package com.example.rohit.bindservice;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ROHIT on 3/7/2019.
 * A service with 2 gesture detection methods
 * 1) chop gesture that is when the device is shook twice in the x and y directions
 * 2) z direction shake once
 */

public class bindservice extends Service implements SensorEventListener{

    private String TAG="bindservice";
    private IBinder mbinder;
    private boolean bound=false;
    private Sensor sensor;
    private SensorManager sensorManager;
    private Boolean chopgesturetrigerd=false, zgesturetrigered =false ,secondmovementtrigger=false;
    private double chopprevtime=0, zprevtime =0,secondprevtime=0;
    private IBinder Ibinder=new localbinder();
    private callbacks activity;
    private ArrayList<Integer> colors;
    private int i=0,j=2;

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

        colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.WHITE);
        colors.add(Color.BLACK);
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
        Log.d(TAG, "onunbind: ");
        sensorManager.unregisterListener(this,sensor);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind: ");
        super.onRebind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return Ibinder;
    }


    public void setbound(boolean b){
        bound=b;
    }

    public void setcallback(Activity activity){
        this.activity=(callbacks) activity;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        double currrnttime = SystemClock.currentThreadTimeMillis();


        float ax = sensorEvent.values[0];
        float ay = sensorEvent.values[1];
        float az = sensorEvent.values[2];

        double chopvalue = ax + ay;
        double zvalue = Math.sqrt(az * az);


        if ((chopgesturetrigerd | zgesturetrigered | secondmovementtrigger) == false) {


            //checks the first shake of the chop gesture
            if (chopvalue < -25) {

                Log.d("shakeEvent", "onSensorChanged: " + chopvalue);
                //Toast.makeText(getBaseContext(),"shake side",Toast.LENGTH_SHORT).show();

                chopgesturetrigerd = true;
                chopprevtime = currrnttime;


            }

            //checks the first and only shake in the z direction
            if (zvalue > 20) {

                Log.d("z", "onSensorChanged: " + (Math.sqrt(az * az)));
                //Toast.makeText(getBaseContext(),"shake up",Toast.LENGTH_SHORT).show();

                zgesturetrigered = true;
                zprevtime = currrnttime;

                if (bound)
                    activity.updateclient(colors.get(j));

                j++;
                if (j > 3)
                    j = 2;


            }

        }

        //reser all 3 triggers after 50ms
        if (chopgesturetrigerd) {
            double choptimeint = currrnttime - chopprevtime;
            //reset triger time gap more than 50ms
            if (choptimeint > 50)
                chopgesturetrigerd = false;

            //second shake of the chop gesture
            if ((choptimeint > 15 && choptimeint < 50) && chopvalue < -25) {
                if (bound)
                    activity.updateclient(colors.get(i));

                i++;
                if (i > 1)
                    i = 0;

                secondmovementtrigger = true;
                secondprevtime = currrnttime;
                chopgesturetrigerd=false;

            }

        }

        //the z axis gesture
        if (zgesturetrigered) {
            double rolltimeint = currrnttime - zprevtime;
            //reset triger time gap more than 50ms
            if (rolltimeint > 50)
                zgesturetrigered = false;

        }

        //reset the secondmovement gesture in 50ms
        if (secondmovementtrigger) {
            double secondtimeint = currrnttime - secondprevtime;
            //reset triger time gap more than 0.75s
            if (secondtimeint > 50) {
                secondmovementtrigger = false;
            }

        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    public class localbinder extends Binder{

        public bindservice getService(){
            return bindservice.this;
        }
    }

    public interface callbacks{
        public void updateclient(int i);
    }

}
