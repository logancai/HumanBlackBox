package com.cgii.humanblackbox;

import java.util.Date;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.IBinder;

public class Services extends Service {
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate(){
		SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		MainActivity.mSensorServices = new SensorServices(mSensorManager);
		MainActivity.mSensorServices.start();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.print("onStartCommand called");
		return START_STICKY;
    }
	
	
	
}
