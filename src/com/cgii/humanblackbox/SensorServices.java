package com.cgii.humanblackbox;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorServices implements SensorEventListener{
	
//	private final SensorManager mSensorManager;
	
//	private SensorEvent mSensorEvent;
	
	public static boolean mTracking;
	
	public SensorServices() {
		Log.v(Services.TAG, "SensorServices constructor");
//		Services.mSensorManager = sensorManager;
		mTracking = false;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			Log.v(Services.TAG, "SensorServices onSensor Change");
			//Can write directly to view here with the values
			
			Services.mSensorEvent = event;
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
		
//		MainActivity.mAsyncCalculation = new AsyncCalculation();
//		MainActivity.mAsyncCalculation.start();
		
	}
	
	public void stop(){
		if (mTracking) {
			Services.mSensorManager.unregisterListener(this);
			mTracking = false;
//			MainActivity.mAsyncCalculation.interrupt();
		}
	}
	public static boolean getTracking(){
		return mTracking;
	}
//	public SensorEvent getSensorEvent(){
//		return mSensorEvent;
//	}
	
}
