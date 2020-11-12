package com.example.hw2;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    private static final int POLL_INTERVAL = 500;
    private static final int INTERVAL_MS = 20;
    private Handler hdr = new Handler();
    private PowerManager.WakeLock wl;
    SensorInfo sensor_info = new SensorInfo();
    SensorInfo previous_sensor_info = new SensorInfo();
    private static final int shake_threshold = 10;
    private static final int light_threshold = 50;
    private int shake_count = 0;
    private long previousTimestamp;
    private Runnable pollTask = new Runnable() {
        public void run() {
            showDialog();
            hdr.postDelayed(pollTask, POLL_INTERVAL);
        }
    };

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Sensors Info");
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy){
        // TO DO
    }


    public void onSensorChanged(SensorEvent event){
        int type = event.sensor.getType();
        if (type == Sensor.TYPE_ACCELEROMETER) {
            sensor_info.accX=event.values[0];
            sensor_info.accY=event.values[1];
            sensor_info.accZ=event.values[2];
        }
        if (type == Sensor.TYPE_LIGHT) {
            sensor_info.light=event.values[0];
        }
        if (type == Sensor.TYPE_ORIENTATION) {
            sensor_info.orX=event.values[0];
            sensor_info.orY=event.values[1];
            sensor_info.orZ=event.values[2];
        }
        if (type == Sensor.TYPE_PROXIMITY) {
            sensor_info.proximity=event.values[0];
        }
    }

    public void showDialog() {
        TextView text1 = (TextView) findViewById(R.id.text_accelerometer);
        LinearLayout layout = (LinearLayout) findViewById(R.id.linear_layout);

        if( (Math.abs(sensor_info.accX-previous_sensor_info.accX)>shake_threshold) || (Math.abs(sensor_info.accY-previous_sensor_info.accY)>shake_threshold) || (Math.abs(sensor_info.accZ-previous_sensor_info.accZ)>shake_threshold) ) {
            final long now = System.currentTimeMillis();
            if (now - previousTimestamp > INTERVAL_MS) {
                shake_count++;
                String message1 = String.format(getResources().getString(R.string.shake_count)+" %1$d", shake_count);
                text1.setText(message1);
                previousTimestamp = now;
            }
            previous_sensor_info.accX = sensor_info.accX;
            previous_sensor_info.accY = sensor_info.accY;
            previous_sensor_info.accZ = sensor_info.accZ;

        }//end if
    }

    @SuppressLint("WakelockTimeout")
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        if (!wl.isHeld()) {
            wl.acquire();
        }
        hdr.postDelayed(pollTask, POLL_INTERVAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

        if (wl.isHeld()) {
            wl.release();
        }
        hdr.removeCallbacks(pollTask);
    }

    static class SensorInfo{
        float accX, accY, accZ;
        float light;
        float orX, orY, orZ;
        float proximity;
    }
}//end MainActivity