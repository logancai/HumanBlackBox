package com.cgii.humanblackbox;

import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

public class Services extends Service {
	
	private static final String LIVE_CARD_TAG = "humanblackbox";
	public static final String TAG = "com.cgii.humanblackbox";
	
	private LiveCard mLiveCard;
	
	private Drawer mDrawer;
	
	public static SensorManager mSensorManager;
	public static SensorServices mSensorServices;
	public static SensorEvent mSensorEvent;
	public static float mHeading;
	
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
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "onCreate called");
		if (mLiveCard == null) {
			Log.v(TAG, "LiveCard is null. Creating card...");
			
			/*
			 * I need a fake activity in order for the camera to launch
			 */
//			Intent anIntent = new Intent(this, AnActivity.class);
			Intent anIntent = new Intent(this, MenuActivity.class);
			anIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(anIntent);
			
			
			mSensorServices = new SensorServices();
			mSensorServices.start();
			
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
		mSensorServices.stop();
		if (mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.unpublish();
            mLiveCard = null;
        }
        super.onDestroy();
    }
}
