package com.cgii.humanblackbox;

import java.util.ArrayList;
import java.util.Date;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;

class SensorEventValues {
	public double[] values;
	Date date;
	public SensorEventValues(double a, double b, double c){
		values = new double[3];
		values[0] = a;
		values[1] = b;
		values[2] = c;
		date = new Date();
	}
}

public class SensorServices extends Services implements SensorEventListener{
	
	public static boolean mTracking;
	
	/*
	 * Olivia code
	 */
	private double X,Y,Z,mag;
	private double jerk;
	private double jerkx;
	private double jerky;
	private double jerkz;
	private double time;
	private int count=0;// every new time the class is called or services.java, it will start with count 0; letting us take an inital reading 
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private double now;
	
	/*
	 * Olivia code
	 */
	public SensorServices() {
		Log.v(Services.TAG, "SensorServices constructor");
		mTracking = false;
		Services.isRecording = false;
		mArrayList = new ArrayList<SensorEventValues>();
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
		for(int i = 0; i < mArrayList.size(); i++){
			sum += mArrayList.get(i).values[0];
		}
		meanValueX = sum/mArrayList.size();
		return meanValueX;
	}
	private double meanY(){
		double sum = 0;
		for(int i = 0; i < mArrayList.size(); i++){
			sum += mArrayList.get(i).values[1];
		}
		meanValueY = sum/mArrayList.size();
		return meanValueY;
	}
	private double meanZ(){
		double sum = 0;
		for(int i = 0; i < mArrayList.size(); i++){
			sum += mArrayList.get(i).values[2];
		}
		meanValueZ = sum/mArrayList.size();
		return meanValueZ;
	}
	private double varianceX(){
		double sum = 0;
		for(int i = 0; i < mArrayList.size(); i++){
			if(meanValueX == 0){
				//In case you forget to call mean
				meanX();
			}
			double value = mArrayList.get(i).values[0] - meanValueX;
			value = value * value;
			sum += value;
		}
		varianceValueX = sum/mArrayList.size();
//		standardDeviationValueX = Math.sqrt(varianceValueX);
		return varianceValueX;
	}
	private double varianceY(){
		double sum = 0;
		if(meanValueY == 0){
			//In case you forget to call mean
			meanY();
		}
		for(int i = 0; i < mArrayList.size(); i++){
			double value = mArrayList.get(i).values[1] - meanValueY;
			value = value * value;
			sum += value;
		}
		varianceValueY = sum/mArrayList.size();
//		standardDeviationValueY = Math.sqrt(varianceValueY);
		return varianceValueY;
	}
	private double varianceZ(){
		double sum = 0;
		if(meanValueZ == 0){
			//In case you forget to call mean
			meanZ();
		}
		for(int i = 0; i < mArrayList.size(); i++){
			double value = mArrayList.get(i).values[2] - meanValueZ;
			value = value * value;
			sum += value;
		}
		varianceValueZ = sum/mArrayList.size();
//		standardDeviationValueZ = Math.sqrt(varianceValueZ);
		return varianceValueZ;
	}
	
//	int count = 0;
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			if (Services.isRecording == false){
				//Can write directly to view here with the values
				Services.mSensorEvent = event;
				
				
				/*
				 * START: Math calculations goes here.
				 * 
				 * To call camera, you must do these 2 commands.
				 * 1) Services.isRecording = true;
				 * 2) startRecording();
				 */
				
				/*
				 * Start: This is to add the current values to the ArrayList
				 */
				if (mArrayList.size() > MAX_ARRAY_LENGTH){
					mArrayList.remove(0);
				}
				SensorEventValues mSensorEventValues = 
						new SensorEventValues(event.values[0],event.values[1],event.values[2]);
				mArrayList.add(mSensorEventValues);
				/*
				 * End: This is to add the current values to the ArrayList
				 */
				
				/*
				 * Start: calculate the mean and variance
				 * Note: meanX() must be called before varianceX()
				 * or else your variance will be wrong.
				 * Optimization tip; if you ever need to call meanX() again
				 * use meanValueX so it does not need to recalculate
				 */
				
//				meanX();
//				meanY();
//				Log.v(Services.TAG, "Mean" +":"+ Double.toString(meanX()));
//				Log.v(Services.TAG, "Variance" +":"+ Double.toString(varianceX()));
//				Log.v(Services.TAG, "Variance" +":"+ Double.toString(varianceY()));
				/*
				 * End: of mean and variance calculation
				 */
				
				/*
				 * End: Math calculation goes here.
				 */
				
				/*
				 * START: Keep this code for demo.
				 */
				double vector = Math.sqrt(event.values[0]*event.values[0]+
						event.values[1]*event.values[1]+
						event.values[2]*event.values[2]);
				double variancez=varianceZ();
				double variancey=varianceY();
				double variancex=varianceX();
				if (vector > 15){
					if(varianceX()>40||varianceY()>40||varianceZ()>40){
					Log.v(Services.TAG, ">15 launching camera...");
					}
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
	public static void startRecording(){
//		Message msgObj = AnActivity.cameraHandler.obtainMessage();
		Message msgObj = MenuActivity.cameraHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putBoolean("message", Services.isRecording);
        msgObj.setData(b);
//        AnActivity.cameraHandler.sendMessage(msgObj);
        MenuActivity.cameraHandler.sendMessage(msgObj);
	}
}
