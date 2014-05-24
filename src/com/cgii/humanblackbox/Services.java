package com.cgii.humanblackbox;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
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
	public static boolean isRecording;
	
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
		isRecording = false;
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "onCreate called");
		if (mLiveCard == null) {
			Log.v(TAG, "LiveCard is null. Creating card...");
			mSensorServices = new SensorServices();
			
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
	public void onDestroy(){
		mSensorServices.stop();
	}
}
