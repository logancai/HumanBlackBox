package com.cgii.humanblackbox;

import java.util.ArrayList;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

public class SensorServices extends Services implements SensorEventListener{
	
	public static boolean mTracking;
	private ArrayList<SensorEvent> mArrayList;
	private final int MAX_ARRAY_LENGTH = 45;
	
	public SensorServices() {
		Log.v(Services.TAG, "SensorServices constructor");
		mTracking = false;
		Services.isRecording = false;
	}
	
	/*
	 * This calculates the average/mean of X, Y, Z. 
	 * Warning: Values from SensorEvent can be positive and negative.
	 * So in theory of large numbers, X, Y, Z, should = 0 but this is
	 * not going to be true because the user is usually sitting in 1 position
	 */
	
	private double meanX(){
		double sum = 0;
		for(int i = 0; i < mArrayList.size(); i++){
			sum += mArrayList.get(i).values[0];
		}
		return sum/mArrayList.size();
	}
	private double meanY(){
		double sum = 0;
		for(int i = 0; i < mArrayList.size(); i++){
			sum += mArrayList.get(i).values[1];
		}
		return sum/mArrayList.size();
	}
	private double meanZ(){
		double sum = 0;
		for(int i = 0; i < mArrayList.size(); i++){
			sum += mArrayList.get(i).values[2];
		}
		return sum/mArrayList.size();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			if (Services.isRecording == false){
				//Can write directly to view here with the values
				Services.mSensorEvent = event;
				
				/*
				 * START: Math calculations goes here.
				 * To call camera, you must do these 2 commands.
				 * 1) Services.isRecording = true;
				 * 2) startRecording();
				 */
				
				if (mArrayList.size() > MAX_ARRAY_LENGTH){
					mArrayList.remove(0);
				}
				else{
					mArrayList.add(event);
				}
				
				/*
				 * End: Math calculation goes here.
				 */
				
				/*
				 * START: Keep this code for demo.
				 */
				double vector = Math.sqrt(event.values[0]*event.values[0]+
						event.values[1]*event.values[1]+
						event.values[2]*event.values[2]);
				if (vector > 15){
					Log.v(Services.TAG, ">15 launching camera...");
					Services.isRecording = true;
					startRecording();
					
				}
				/*
				 * END: Keep this code for demo.
				 */
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
