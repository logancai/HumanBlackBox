package com.cgii.humanblackbox;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

public class SensorServices extends Services implements SensorEventListener{
	
	public static boolean mTracking;
//	public static boolean mRecording;
	
	public SensorServices() {
		Log.v(Services.TAG, "SensorServices constructor");
		mTracking = false;
		Services.isRecording = false;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
//		Log.v(Services.TAG, "SensorServices onSensor Change");
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			if (Services.isRecording == false){
				//Can write directly to view here with the values
				Services.mSensorEvent = event;
				
				double vector = Math.sqrt(event.values[0]*event.values[0]+
						event.values[1]*event.values[1]+
						event.values[2]*event.values[2]);
				if (vector > 15){
					Log.v(Services.TAG, ">15 launching camera...");
					Services.isRecording = true;
					startRecording();
					
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//Do nothing
	}
	
	public void start(){
		if (!mTracking) {
			Services.mSensorManager.registerListener(this, 
					Services.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
					SensorManager.SENSOR_DELAY_FASTEST);
		}
		mTracking = true;
		
	}
	
	public void stop(){
		if (mTracking) {
			Services.mSensorManager.unregisterListener(this);
			mTracking = false;
		}
	}
	
	/*
	 * We cannot call the camera directly. We can only send
	 * a message for the camera to launch
	 */
	private void startRecording(){
		Message msgObj = AnActivity.cameraHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putBoolean("message", Services.isRecording);
        msgObj.setData(b);
        AnActivity.cameraHandler.sendMessage(msgObj);
	}
}
