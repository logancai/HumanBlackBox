package com.cgii.humanblackbox;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class Services extends Service {
	
	private static final String LIVE_CARD_TAG = "humanblackbox";
	
	private LiveCard mLiveCard;
	
	private Drawer mDrawer;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		if (mLiveCard == null) {
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
	
}
