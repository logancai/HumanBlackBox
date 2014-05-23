package com.cgii.humanblackbox;

import java.util.Date;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorServices implements SensorEventListener{
	
	private final SensorManager mSensorManager;
	
	public static boolean mTracking;
	
	public SensorServices(SensorManager sensorManager) {
		mSensorManager = sensorManager;
		mTracking = false;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
//			MainActivity.textView.setText("X: "+ event[0] +
//					"\nY: "+ event[1] +
//					"\nX: "+ event[2] +);
			MainActivity.mSensorEvent = event;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//Do nothing
	}
	
	public void start(){
		if (!mTracking) {
			mSensorManager.registerListener(this, 
					mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
					SensorManager.SENSOR_DELAY_FASTEST);
		}
		mTracking = true;
		
		MainActivity.mAsyncCalculation = new AsyncCalculation();
		MainActivity.mAsyncCalculation.start();
		
	}
	
	public void stop(){
		if (mTracking) {
			mSensorManager.unregisterListener(this);
			mTracking = false;
			MainActivity.mAsyncCalculation.interrupt();
		}
	}
	public static boolean getTracking(){
		return mTracking;
	}
	
}
