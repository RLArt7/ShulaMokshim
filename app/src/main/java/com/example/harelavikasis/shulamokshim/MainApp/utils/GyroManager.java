package com.example.harelavikasis.shulamokshim.MainApp.utils;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.example.harelavikasis.shulamokshim.MainApp.MainApplication;
import com.example.harelavikasis.shulamokshim.MainApp.bus.GeneralEvent;

/**
 * Created by harelavikasis on 10/01/2017.
 */

public class GyroManager implements SensorEventListener {

    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp;
    private float initState = 0;
    private int lastTimeStamp = 0;
    private long mStartTime = 0;
    private long mElapsedTime = 0;


    public GyroManager() {
    }

    public void onSensorChanged(SensorEvent event) {
        // This timestep's delta rotation to be multiplied by the current rotation
        // after computing it from the gyro sample data.
        if (timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S;
            // Axis of the rotation sample, not normalized yet.
            double axisX = event.values[0];
//            double axisY = event.values[1];
//            double axisZ = event.values[2];

            if (axisX > 0.1 || (axisX < -0.1 && axisX > -30.0)) {
                initState += axisX;
            }
            if (lastTimeStamp > 10) {
                // emit an event for knowing the UI to add mine when tilt accured.
               MainApplication.getGameBus().post(new GeneralEvent());
                mStartTime = 0;
                mElapsedTime = 0;
                lastTimeStamp = 0;
            }
            if (initState < 0.5 && initState > -0.5) {
                initState = 0;
                mStartTime = 0;
//                Log.d("gyroTest 3", "init lastTimeStamp: " + lastTimeStamp);
                mElapsedTime = 0;
                lastTimeStamp = 0;
            }

        }
        timestamp = event.timestamp;
        if (mStartTime == 0) {
            mStartTime = System.currentTimeMillis();
        } else {
            mElapsedTime += System.currentTimeMillis() - mStartTime;
            lastTimeStamp = (int) (mElapsedTime / 1000);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
