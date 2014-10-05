package com.cgii.humanblackbox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

public class Services extends Service implements SensorEventListener, LocationListener{
	
	private static final String LIVE_CARD_TAG = "humanblackbox";
	public static final String TAG = "com.cgii.humanblackbox";
	
	private LiveCard mLiveCard;
	
	private Drawer mDrawer;
	
	public static SensorManager mSensorManager;
	public static SensorServices mSensorServices;
	public static SensorEvent mSensorEvent;
	public static float mHeading;
	
	private float[] mRotationMatrix;
	private float[] mOrientation;
	private float mPitch;
	
	public static LocationManager mLocationManager;
	public static Location mLocation;
	public static String address;
	public static String city;
	public static String country;
	public static String zipCode;
	
	public static boolean isRecording;
	public static boolean demoMode;
	public static Activity mActivity;
	MenuActivity mMenuActivity;
	public static float lastHeading;
	public static float speed;
	
	public static ArrayList<SensorEventValues> mArrayList;
	public static String name = "N/A";
	public static final int MAX_ARRAY_LENGTH = 45;
	
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
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate(){
		Log.v(TAG, "onCreate called");
		super.onCreate();
		if (mSensorManager == null){
			mSensorManager = 
					(SensorManager) getSystemService(Context.SENSOR_SERVICE);
		}
		if (mLocationManager == null){
			mLocationManager = 
					(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}
		
		isRecording = false;
		mRotationMatrix = new float[16];
		mOrientation = new float[9];
		
		//Register Sensors
		mSensorManager.registerListener(this, 
				Services.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
				5000);
		
		mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                5000*2);
		
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
        		MILLIS_BETWEEN_LOCATIONS, METERS_BETWEEN_LOCATIONS, this,
                Looper.getMainLooper());
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "onCreate called");
		if (mLiveCard == null) {
			Log.v(TAG, "LiveCard is null. Creating card...");
			
			/*
			 * I need a fake activity in order for the camera to launch
			 */
			Intent anIntent = new Intent(this, MenuActivity.class);
			anIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(anIntent);
			
//			mSensorServices = new SensorServices();
//			mSensorServices.start();
			
			mLiveCard = new LiveCard(this, LIVE_CARD_TAG);

            // Keep track of the callback to remove it before unpublishing.
            mDrawer = new Drawer(this);
            mLiveCard.setDirectRenderingEnabled(true).getSurfaceHolder().addCallback(mDrawer);

            Intent menuIntent = new Intent(this, MenuActivity.class);
            menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
            mLiveCard.attach(this);
            mLiveCard.publish(PublishMode.REVEAL);
        } else {
            mLiveCard.navigate();
        }
		return START_STICKY;
	}
	
	@Override
    public void onDestroy() {
//		mSensorServices.stop();
		Services.mSensorManager.unregisterListener(this);
		Services.mLocationManager.removeUpdates(this);
		if (mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.unpublish();
            mLiveCard = null;
        }
        super.onDestroy();
    }
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			//The average sample rate is 20 milliseconds between samples
			mSensorEvent = event;
			if (Services.isRecording == false){
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
				    if(Services.speed < 10 && Services.speed > 0){
				        typeofMovement="walking";
				    }
				    else if(Services.speed < 20 && Services.speed > 10){
				        typeofMovement="bicycle";
				    }
				}
				else {
				    typeofMovement="car";
				}

				if(typeofMovement.equals("walking")){
				    if(event.values[1] < 3){
				        if(Math.abs(mHeading - Services.lastHeading) > 20){
				            // you are probably free falling
				        	Log.v(Services.TAG, "Walking mode");
				        	Services.isRecording = true;
							startRecording();
				        }
				    }
				}
				else if(typeofMovement.equals("bicycle")){
				    if( event.values[2] < 3){
				        if(Math.abs(mHeading - Services.lastHeading) > 20){
				        	Log.v(Services.TAG, "Bike mode");
				        	Services.isRecording = true;
							startRecording();
				        }
				    }
				}
				else if(typeofMovement.equals("car")){
				    if(event.values[2] > 60){
				    	Log.v(Services.TAG, "car mode");
				    	Services.isRecording = true;
						startRecording();
				    }
				}
			}
			else{
				Log.v(Services.TAG, "Recording a video");
			}
		}
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
			//The average sample rate is 19.42 millisconds between samples
			//Current mHeading = old
			lastHeading = mHeading;
			
			// Get the current heading from the sensor, then notify the listeners of the
            // change.
            SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
            SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_X,
                    SensorManager.AXIS_Z, mRotationMatrix);
            SensorManager.getOrientation(mRotationMatrix, mOrientation);

            // Store the pitch (used to display a message indicating that the user's head
            // angle is too steep to produce reliable results.
//            mPitch = (float) Math.toDegrees(mOrientation[1]);

            // Convert the heading (which is relative to magnetic north) to one that is
            // relative to true north, using the user's current location to compute this.
            float magneticHeading = (float) Math.toDegrees(mOrientation[0]);
            mHeading = MathUtils.mod(magneticHeading, 360.0f)
                    - ARM_DISPLACEMENT_DEGREES;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//Do nothing
	}
	
	public static void getAddress(){
		Message msgObj = MenuActivity.locationHandler.obtainMessage();
        MenuActivity.locationHandler.sendMessage(msgObj);
	}
	
	/*
	 * We cannot call the camera directly. We can only send
	 * a message for the camera to launch
	 */
	public static void startRecording(){
		Message msgObj = MenuActivity.cameraHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putBoolean("message", Services.isRecording);
        msgObj.setData(b);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		mLocation = location;
		//geocoder will convert latitude/longitude to address
		Log.v(Services.TAG, "SensorServices onLocationChanged");
		speed = location.getSpeed();
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
}
