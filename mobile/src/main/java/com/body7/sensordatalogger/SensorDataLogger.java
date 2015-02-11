package com.body7.sensordatalogger;

/**
 * Created by MXU24 on 2/5/2015.
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.logging.Logger;

public class SensorDataLogger {

    private static final String TAG = "SensorDataLogger";

    //private ArrayList<Sensor> sensorArrayList;
    private ArrayList<String> sensorArrayList;
    private LoggerMode lm;
    private ArrayList<float[]> lastUpdatedData;
    private ArrayList<float[]> lastLazyUpdatedData;

    private ArrayList<Boolean> hasBeenSent;

    private int mode = 1;
    private int interval = 1000;

    public SensorDataLogger(ArrayList<String> sensors, LoggerMode log){
        sensorArrayList = sensors;
        this.lm = log;
        hasBeenSent = new ArrayList<>(sensorArrayList.size());
        lastUpdatedData = new ArrayList<>(sensorArrayList.size());
        lastLazyUpdatedData = new ArrayList<>(sensorArrayList.size());
        //for (Boolean b: hasBeenSent) {b = true;}
        float [] temp = {0,0,0};
        for(int i = 0; i<sensorArrayList.size();i++){
            //hasBeenSent.set(i,true);
            //lastLazyUpdatedData.set(i,temp);
            //lastUpdatedData.set(i,temp);
            hasBeenSent.add(true);
            lastUpdatedData.add(temp);
            lastLazyUpdatedData.add(temp);
        }
        //add runnable interval log

    }
    //private LoggerMode loggerMode = LoggerMode.NotSet;
    public void getNewSensorEvent(String sensorName, float[] values) throws JSONException {
        int i = 0;
        while(!sensorArrayList.get(i).equals(sensorName)) i++;

        switch (mode){
            case 0:{ break;}
            case 1:{
                lastUpdatedData.set(i, values);
                JSONObject jsonObj = new JSONObject();
                for(int j = 0; j<sensorArrayList.size(); j++){
                    float[] temp = lastUpdatedData.get(j);
                    JSONObject smallJson = new JSONObject();

                    for (int q = 0 ; q<3;q++){
                        smallJson.put("value"+q, temp[q]);
                    }
                    //jsonObj.put(sensorArrayList.get(j), lastUpdatedData.get(j));
                    jsonObj.put(sensorArrayList.get(j),smallJson);
                }
                lm.getActiveLog(jsonObj);
                break;
            }
            case 2:{
                if(hasBeenSent.get(i) == true){
                    lastLazyUpdatedData.set(i,values);
                    hasBeenSent.set(i, false);
                }else{
                    JSONObject oldJsonObj = new JSONObject();
                    for(int j = 0; j<sensorArrayList.size(); j++){
                        oldJsonObj.put(sensorArrayList.get(j), lastUpdatedData.get(j));
                        hasBeenSent.set(j, true);

                    }
                    lm.getLazyLog(oldJsonObj);
                }
                break;
            }
            case 3:{ break;}
        }
    }

    public void setLoggerMode(int mode){
        this.mode = mode;
    }
    public void setInterval(int interval){
        this.interval = interval;
    }



    public interface LoggerMode{
        public void getActiveLog(JSONObject jsonObj);
        public void getLazyLog(JSONObject jsonObj);
        public void getIntervalLog(JSONObject jsonObj);
    }

}
