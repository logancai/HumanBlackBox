package com.logancai.humanblackbox;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.glass.app.Card;
import com.logancai.humanblackbox.HumanBlackBoxMenuActivity;
import com.logancai.humanblackbox.R;
import com.logancai.humanblackbox.HumanBlackBoxService;

public class HumanBlackBoxMenuActivity extends Activity{
	
	private final Handler mHandler = new Handler();

    private HumanBlackBoxService.CompassBinder mHumanBlackBoxService;
    private boolean mAttachedToWindow;
    private boolean mOptionsMenuOpen;
	
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof HumanBlackBoxService.CompassBinder) {
                mHumanBlackBoxService = (HumanBlackBoxService.CompassBinder) service;
                openOptionsMenu();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Do nothing.
        }
    };
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bindService(new Intent(this, HumanBlackBoxService.class), mConnection, 0);
    }
	
	@Override
    public void onAttachedToWindow() {
		super.onAttachedToWindow();
		mAttachedToWindow = true;
        openOptionsMenu();
    }
	@Override
    public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
        mAttachedToWindow = false;
    }
	@Override
    public void openOptionsMenu() {
		if (!mOptionsMenuOpen && mAttachedToWindow && mHumanBlackBoxService != null) {
            super.openOptionsMenu();
        }
    }
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case R.id.read_aloud:
            mHumanBlackBoxService.readHeadingAloud();
            return true;
        case R.id.stop:
            // Stop the service at the end of the message queue for proper options menu
            // animation. This is only needed when starting an Activity or stopping a Service
            // that published a LiveCard.
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    stopService(new Intent(HumanBlackBoxMenuActivity.this, HumanBlackBoxService.class));
                }
            });
            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
    }
	@Override
    public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
        mOptionsMenuOpen = false;

        unbindService(mConnection);

        // We must call finish() from this method to ensure that the activity ends either when an
        // item is selected from the menu or when the menu is dismissed by swiping down.
        finish();
    }
}
