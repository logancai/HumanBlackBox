package com.cgii.humanblackbox;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
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
	
	/**
     * The sensors used by the compass are mounted in the movable arm on Glass. Depending on how
     * this arm is rotated, it may produce a displacement ranging anywhere from 0 to about 12
     * degrees. Since there is no way to know exactly how far the arm is rotated, we just split the
     * difference.
     */
    private static final int ARM_DISPLACEMENT_DEGREES = 6;
	
	/**
     * The maximum age of a location retrieved from the passive location provider before it is
     * considered too old to use when the compass first starts up.
     */
    private static final long MAX_LOCATION_AGE_MILLIS = TimeUnit.MINUTES.toMillis(30);
    
    /**
     * The minimum elapsed time desired between location notifications.
     */
    private static final long MILLIS_BETWEEN_LOCATIONS = TimeUnit.SECONDS.toMillis(3);
    
    /**
     * The minimum distance desired between location notifications.
     */
    private static final long METERS_BETWEEN_LOCATIONS = 2;
	
	public static boolean mTracking;
	private final float[] mRotationMatrix;
	private final float[] mOrientation;
	private GeomagneticField mGeomagneticField;
	private float mPitch;
	
	/*
	 * Start: Byron code
	 */
	double v_0x = 0;
	double v_0y = 0;
	double v_0z = 0;
	Date timeStart;
//	double meanx = meanX();
//	double meany = meanY();
//	double means = meanZ();
	double Dstoppedx;
	double Dstoppedy; 
	double Dstoppedz;
	double D_sx;
	double D_sy;
	double D_sz;
	double deltaT = 50d/1000d;
	/*
	 * End: Byron code
	 */
	public SensorServices() {
		Log.v(Services.TAG, "SensorServices constructor");
		mTracking = false;
		Services.isRecording = false;
		mArrayList = new ArrayList<SensorEventValues>();
		timeStart = new Date();
		mRotationMatrix = new float[16];
		mOrientation = new float[9];
		Log.v(Services.TAG, "SensorServices start time" + Long.toString(timeStart.getTime()));
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
	
	@Deprecated
	private double meanX(){
		double sum = 0;
		for(int i = 0; i < mArrayList.size(); i++){
			sum += mArrayList.get(i).values[0];
		}
		meanValueX = sum/mArrayList.size();
		return meanValueX;
	}
	@Deprecated
	private double meanY(){
		double sum = 0;
		for(int i = 0; i < mArrayList.size(); i++){
			sum += mArrayList.get(i).values[1];
		}
		meanValueY = sum/mArrayList.size();
		return meanValueY;
	}
	@Deprecated
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
				
				Services.mSensorEvent = event;
				/*
				 * Start: This is to add the current values to the ArrayList
				 */
				if (mArrayList.size() > MAX_ARRAY_LENGTH){
					SensorEventValues mSensorEventValues = mArrayList.remove(0);
					meanValueX = meanValueX * Services.MAX_ARRAY_LENGTH;
					meanValueY = meanValueY * Services.MAX_ARRAY_LENGTH;
					meanValueZ = meanValueZ * Services.MAX_ARRAY_LENGTH;
					meanValueX = meanValueX - mSensorEventValues.values[0];
					meanValueY = meanValueY - mSensorEventValues.values[1];
					meanValueZ = meanValueZ - mSensorEventValues.values[2];
				}
				SensorEventValues mSensorEventValues = 
						new SensorEventValues(event.values[0],event.values[1],event.values[2]);
				meanValueX = meanValueX + event.values[0];
				meanValueY = meanValueY + event.values[1];
				meanValueZ = meanValueZ + event.values[2];
				meanValueX = meanValueX / Services.MAX_ARRAY_LENGTH;
				meanValueY = meanValueY / Services.MAX_ARRAY_LENGTH;
				meanValueZ = meanValueZ / Services.MAX_ARRAY_LENGTH;
				
				mArrayList.add(mSensorEventValues);
				
				/*
				 * End: This is to add the current values to the ArrayList
				 */
				
				if (Services.demoMode){
					/*
					 * START: Keep this code for demo.
					 */
					double vector = Math.sqrt(event.values[0]*event.values[0]+
							event.values[1]*event.values[1]+
							event.values[2]*event.values[2]);
					if (vector > 15){
						if(varianceX() > 40||varianceY() > 40||varianceZ() > 40){
						Log.v(Services.TAG, "variance > 40, launching camera...");
						}
//						Services.isRecording = true;
//						startRecording();
					}
					/*
					 * END: Keep this code for demo.
					 */
				}
				else{
					/*
					 * START: Math calculations goes here.
					 * 
					 * To call camera, you must do these 2 commands.
					 * 1) Services.isRecording = true;
					 * 2) startRecording();
					 */
							
					String typeofMovement = "walking";
					// add get location.get speed right here you can get speed from location manager 
					if(Services.speed < 20){
					    // walking or driving
					    if(Services.speed<10 && Services.speed>0){
					        typeofMovement="walking";
					    }
					    else if(Services.speed < 20 && Services.speed>10){
					        typeofMovement="bicycle";
					    }
					}
					else {
					    typeofMovement="car";
					}

					if(typeofMovement.equals("walking")){
					    if(event.values[1]< 3){
					        if(Math.abs(mHeading - Services.lastHeading) > 20){
					            // you are probably free falling
					        	Log.v(Services.TAG, "Walking mode");
					        	Services.isRecording = true;
								startRecording();
					        }
					    }
					}
					else if(typeofMovement.equals("bicycle")){
					    if( event.values[2]<3){
					        if(Math.abs(mHeading - Services.lastHeading) > 20){
					        	Log.v(Services.TAG, "Bike mode");
					        	Services.isRecording = true;
								startRecording();
					        }
					    }
					}
					else if(typeofMovement.equals("car")){
					    if(varianceZ()>60){
					    	Log.v(Services.TAG, "car mode");
					    	Services.isRecording = true;
							startRecording();
					    }
					}
				}
			}
		}
		if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
			//Current mHeading = old
			Services.lastHeading = mHeading;
			
			// Get the current heading from the sensor, then notify the listeners of the
            // change.
            mSensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
            mSensorManager.remapCoordinateSystem(mRotationMatrix, mSensorManager.AXIS_X,
                    mSensorManager.AXIS_Z, mRotationMatrix);
            mSensorManager.getOrientation(mRotationMatrix, mOrientation);

            // Store the pitch (sed to display a message indicating that the user's head
            // angle is too steep to produce reliable results.
            mPitch = (float) Math.toDegrees(mOrientation[1]);

            // Convert the heading (which is relative to magnetic north) to one that is
            // relative to true north, using the user's current location to compute this.
            float magneticHeading = (float) Math.toDegrees(mOrientation[0]);
            mHeading = MathUtils.mod(magneticHeading, 360.0f)
                    - ARM_DISPLACEMENT_DEGREES;
//            Log.v(Services.TAG, "Heading: " + Float.toString(mHeading));
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
					5000);
			
			Services.mSensorManager.registerListener(this,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                    5000*2);
			
			
//			Services.mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Location lastLocation = Services.mLocationManager
                    .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
			
            if (lastLocation != null) {
                long locationAge = lastLocation.getTime() - System.currentTimeMillis();
                if (locationAge < MAX_LOCATION_AGE_MILLIS) {
                    Services.mLocation = lastLocation;
                }
            }
            else{
            	Log.v(Services.TAG, "lastLocation is null");
            }

            Services.mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
            		MILLIS_BETWEEN_LOCATIONS, METERS_BETWEEN_LOCATIONS, mLocationListener,
                    Looper.getMainLooper());
		}
		mTracking = true;
		
	}
	
	public void stop(){
		if (mTracking) {
			Services.mSensorManager.unregisterListener(this);
			Services.mLocationManager.removeUpdates(mLocationListener);
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
	
	public static void getAddress(){
		Message msgObj = MenuActivity.locationHandler.obtainMessage();
        MenuActivity.locationHandler.sendMessage(msgObj);
	}
	
	/*
	 * Location services
	 * onLocation, onStatusChanged, 
	 */
	
	LocationListener mLocationListener = new LocationListener(){

		@Override
		public void onLocationChanged(Location location) {
			Services.mLocation = location;
			//geocoder will convert latitude/longitude to address
			Log.v(Services.TAG, "SensorServices onLocationChanged");
			Services.speed = location.getSpeed();
			getAddress();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d("Latitude + Longitude","status");
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.d("Latitude + Longitude","enable");
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.d("Latitude + Longitude","disable");
		}
		
	};
}
