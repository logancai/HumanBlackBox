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
	int count = 0;
	
	public SensorServices() {
		Log.v(Services.TAG, "SensorServices constructor");
		mTracking = false;
		Services.isRecording = false;
//		mArrayList = new ArrayList<SensorEvent>();
		mArrayList = new SensorEvent[MAX_ARRAY_LENGTH];
	}
	
	/*
	 * This calculates the average/mean of X, Y, Z. 
	 * Warning: Values from SensorEvent can be positive and negative.
	 * So in theory of large numbers, X, Y, Z, should = 0 but this is
	 * not going to be true because the user is usually sitting in 1 position
	 */
	
	private double meanValueX, meanValueY, meanValueZ;
	private double varianceValueX, varianceValueY, varianceValueZ;
	private double standardDeviationValueX, standardDeviationValueY, standardDeviationValueZ;
	
	private double meanX(){
		double sum = 0;
		for(int i = 0; i < mArrayList.length; i++){
			if(mArrayList[i] != null){
				sum += mArrayList[i].values[0];
			}
		}
		meanValueX = sum/mArrayList.length;
		return meanValueX;
	}
//	private double meanY(){
//		double sum = 0;
//		for(int i = 0; i < mArrayList.size(); i++){
//			sum += mArrayList.get(i).values[1];
//		}
//		meanValueY = sum/mArrayList.size();
//		return meanValueY;
//	}
//	private double meanZ(){
//		double sum = 0;
//		for(int i = 0; i < mArrayList.size(); i++){
//			sum += mArrayList.get(i).values[2];
//		}
//		meanValueZ = sum/mArrayList.size();
//		return meanValueZ;
//	}
	private double varianceX(){
		double sum = 0;
		for(int i = 0; i < mArrayList.length; i++){
			if (mArrayList[i] != null){
				if(meanValueX == 0){
					//In case you forget to call mean
					meanX();
				}
				double value = mArrayList[i].values[0] - meanValueX;
				value = value * value;
				sum += value;
			}
		}
		varianceValueX = sum/mArrayList.length;
//		standardDeviationValueX = Math.sqrt(varianceValueX);
		return varianceValueX;
	}
//	private double varianceY(){
//		double sum = 0;
//		if(meanValueY == 0){
//			//In case you forget to call mean
//			meanY();
//		}
//		for(int i = 0; i < mArrayList.size(); i++){
//			double value = mArrayList.get(i).values[1] - meanValueY;
//			value = value * value;
//			sum += value;
//		}
//		varianceValueY = sum/mArrayList.size();
////		standardDeviationValueY = Math.sqrt(varianceValueY);
//		return varianceValueY;
//	}
//	private double varianceZ(){
//		double sum = 0;
//		if(meanValueZ == 0){
//			//In case you forget to call mean
//			meanZ();
//		}
//		for(int i = 0; i < mArrayList.size(); i++){
//			double value = mArrayList.get(i).values[2] - meanValueZ;
//			value = value * value;
//			sum += value;
//		}
//		varianceValueZ = sum/mArrayList.size();
////		standardDeviationValueZ = Math.sqrt(varianceValueZ);
//		return varianceValueZ;
//	}
	
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
				
				if (count > MAX_ARRAY_LENGTH){
					count = 0;
					mArrayList[count] = event;
				}
				else{
					mArrayList[count] = event;
					count++;
				}
				
				if(count < 10){
					Log.v(Services.TAG, "////////Start of list");
					for (int i = 0; i < mArrayList.length; i++) {
						if(mArrayList[i] != null){
							Log.v(Services.TAG, i +":"+ Double.toString(mArrayList[i].values[0]));
						}
					}
					Log.v(Services.TAG, "Mean" +":"+ Double.toString(meanX()));
					Log.v(Services.TAG, "Variance" +":"+ Double.toString(varianceX()));
					Log.v(Services.TAG, "////////End of list");
					count++;
				}
				
				
				
				
//				Log.v(Services.TAG, "X variance is: "+ Double.toString(varianceX()));
//				Log.v(Services.TAG, "Y average is: "+ Double.toString(meanY()));
//				Log.v(Services.TAG, "Z average is: "+ Double.toString(meanZ()));
				
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
//					Services.isRecording = true;
//					startRecording();
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
					SensorManager.SENSOR_DELAY_GAME);
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
