package com.cgii.humanblackbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.logancai.humanblackbox.R;

public class MenuActivity extends Activity{
	
	private final Handler mHandler = new Handler();
	
	@Override
    public void onAttachedToWindow() {
		Log.v(Services.TAG, "MenuActivity onAttach");
		super.onAttachedToWindow();
        openOptionsMenu();
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(Services.TAG, "onCreateMenus");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(Services.TAG, "onOptionSelected");
		// Handle item selection.
        switch (item.getItemId()) {
            case R.id.stop:
                // Stop the service at the end of the message queue for proper options menu
                // animation. This is only needed when starting a new Activity or stopping a Service
                // that published a LiveCard.
                post(new Runnable() {

                    @Override
                    public void run() {
                    	stopService(new Intent(MenuActivity.this, Services.class));
                    }
                });
                return true;
            case R.id.record_video:
            	Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
        		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        		startActivityForResult(intent, 1);
        		return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	@Override
    public void onOptionsMenuClosed(Menu menu) {
        // Nothing else to do, closing the Activity.
        finish();
    }
	
	/**
     * Posts a {@link Runnable} at the end of the message loop, overridable for testing.
     */
    protected void post(Runnable runnable) {
        mHandler.post(runnable);
    }
}
