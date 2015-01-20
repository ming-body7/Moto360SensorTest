package com.example.mxu24.moto360sensortest;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity implements SensorEventListener {

    private TextView mTextView;
    private SensorManager mSensorManager;
    private static final String TAG = "Moto360SensorTest";
    private Sensor mSensor;
    private List<Sensor> deviceSensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor s : deviceSensors){
            Log.i(TAG, "" + s.getStringType());

        }
        int count = deviceSensors.size();
        //mTextView.setText(count);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        /*if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }*/
        Log.i(event.sensor.getStringType(),Float.toString(event.values[0]));
        //switch(event.sensor.getType()){
            /*case Sensor.TYPE_ACCELEROMETER:{
                getAccelerometer(event);
                break;
            }
            case Sensor.TYPE_STEP_COUNTER:{
                Log.i("Step Counter", Float.toString(event.values[0]));
                break;
            }
            case Sensor.TYPE_GYROSCOPE :{
                Log.i("Gesture", Float.toString(event.values[0]));
                break;
            }
            case Sensor.TYPE_MAGNETIC_FIELD :{
                Log.i("Magnetic", Float.toString(event.values[0]));
                break;
            }
            case Sensor.TYPE_LIGHT :{
                Log.i("Light", Float.toString(event.values[0]));
                break;
            }
            case Sensor.TYPE_HEART_RATE :{
                Log.i("Gesture", Float.toString(event.values[0]));
                break;
            }
        }*/


    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        Log.i("Acceleration", Float.toString(accelationSquareRoot));
    }

    private void getStepCounter(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        Log.i(TAG, Float.toString(accelationSquareRoot));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        for(Sensor s : deviceSensors){
            Log.i(TAG, "" + s.getStringType());
            mSensorManager.registerListener(this,
                    mSensorManager.getDefaultSensor(s.getType()),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
