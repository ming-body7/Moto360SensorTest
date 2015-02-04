package com.example.mxu24.moto360sensortest;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItemAsset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends ActionBarActivity implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final static String TAG = "Main";
    private Button pushButton;
    private GoogleApiClient mGoogleApiClient;

    private ArrayList<String> sensorsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pushButton = (Button)findViewById(R.id.pushButton);

        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        init();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onPause(){
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient,this);
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStop(){
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }
    @Override
    public void onConnected(Bundle bundle) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Connected to Google Api Service");
        }
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void init()
    {
        pushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int notificationId = 001;
                Intent viewIntent = new Intent(MainActivity.this, NotificationActivity.class);
                //viewIntent.putExtra(EXTRA_EVENT_ID, eventId);
                PendingIntent viewPendingIntent =
                        PendingIntent.getActivity(MainActivity.this, 0, viewIntent, 0);

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(MainActivity.this)
                                .setSmallIcon(R.drawable.ic_event)
                                .setContentTitle("1")
                                .setContentText("2")
                                .setContentIntent(viewPendingIntent);

// Get an instance of the NotificationManager service
                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(MainActivity.this);

// Build the notification and issues it with notification manager.
                notificationManager.notify(notificationId, notificationBuilder.build());
            }
        });
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());
                //event.getDataItem().getAssets().get("1");
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {

                Log.d(TAG, "DataItem changed: " + event.getDataItem().getUri());
                Uri dataItemUri = event.getDataItem().getUri();

                DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem())
                        .getDataMap();

                Set sensorKeys  = dataMap.keySet();

                //need some more check
                String sensorName = (String)sensorKeys.iterator().next();
                float[] values = dataMap.getFloatArray(sensorName);

                Log.i(sensorName, Float.toString(values[0]));
                /*if(sensorsList == null){
                    sensorsList = dataMap.getStringArrayList("SensorsList");
                }
                else{
                    createDataStream(dataMap);
                }*/

                /*float acceleration = dataMap.getFloat("Accelerometer");
                Log.i(TAG, String.valueOf(acceleration));
                JSONObject jsonObj = new JSONObject();
                try {
                    jsonObj.put("Accelerometer",acceleration);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpPost httppostreq = new HttpPost("https://dweet.io/dweet/for/wearable");
                try {
                    StringEntity se = new StringEntity(jsonObj.toString());
                    httppostreq.setEntity(se);
                    httppostreq.setHeader("Accept", "application/json");
                    httppostreq.setHeader("Content-type", "application/json");
                    try {
                        HttpResponse httpresponse = httpclient.execute(httppostreq);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }*/




            }
        }
    }

    private float[] createDataStream(DataMap dataMap) {
        float[] values = null;
        for(String sensorType:sensorsList){

            values = dataMap.getFloatArray(sensorType);
            if(values!=null) {
                Log.i(sensorType,Float.toString(values[0]));
                return values;
            }
        }
        return values;
    }
}
