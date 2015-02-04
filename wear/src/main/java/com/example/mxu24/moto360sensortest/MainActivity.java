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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements
        SensorEventListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{


    private GoogleApiClient mGoogleApiClient;

    private TextView mTextView;
    private SensorManager mSensorManager;
    private static final String TAG = "Moto360SensorTest";
    private Sensor mSensor;
    private List<Sensor> deviceSensors;
    private ArrayList<String> sensorsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                //the content in stub has to be set in the callback
                mTextView = (TextView) stub.findViewById(R.id.text);
                mTextView.setText("Sensor Data Transferring");
            }
        });
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor s : deviceSensors){
            Log.i(TAG, "" + s.getStringType());
            //sensorsList.add(s.getStringType());
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

            Log.i(event.sensor.getStringType(),Float.toString(event.values[0]));

            String path = "/location"+"/"+ System.currentTimeMillis();
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path);
            putDataMapRequest.getDataMap().putFloatArray(event.sensor.getStringType(),event.values);
            //putDataMapRequest.getDataMap().putFloat("Accelerometer", getAccelerometer(event));
            PutDataRequest request = putDataMapRequest.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
            .putDataItem(mGoogleApiClient, request);
    }

    private float getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelerationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        Log.i("Acceleration", Float.toString(accelerationSquareRoot));
        return(accelerationSquareRoot);
    }

    private void getStepCounter(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

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

    @Override
    public void onConnected(Bundle bundle) {


        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/SensorList");

        putDataMapReq.getDataMap().putStringArrayList("SensorsList",sensorsList);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
