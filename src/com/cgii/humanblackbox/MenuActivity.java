package com.cgii.humanblackbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.glass.media.CameraManager;
import com.logancai.humanblackbox.R;

public class MenuActivity extends Activity{
	
	private final Handler mHandler = new Handler();
	
	public final static int recordingTimeInSeconds = 15;
	private static final int TAKE_VIDEO_REQUEST = 1;
	
	public MenuActivity(){
		Services.mActivity = this;
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Services.mActivity = this;
	}
	
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
//            	stopService(new Intent(MenuActivity.this, Services.class));
                return true;
            case R.id.record_video:
            	Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, recordingTimeInSeconds);
        		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        		this.startActivityForResult(intent, TAKE_VIDEO_REQUEST);
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
    
//    private static void launchCamera(Activity activity){
//    	Log.v(Services.TAG, "LaunchCamera called called");
//    	Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, recordingTimeInSeconds);
//		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, TAKE_VIDEO_REQUEST);
//		if (activity == null){
//			Log.v(Services.TAG, "LaunchCamera activity is null");
//		}
//		activity.startActivityForResult(intent, 1);
//    }
    
//    public static Handler cameraHandler = new Handler(){
//    	public void handleMessage(Message msg){
//    		Log.v(Services.TAG, "Menu handler called");
//    		boolean isRecording = msg.getData().getBoolean("message");
//    		if (isRecording){
//    			launchCamera(Services.mActivity);
//    		}
//    	}
//    };
//    
//    @Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    	Log.v(Services.TAG, "onActivityResult called");
//    	Log.v(Services.TAG, "requestCode: " + requestCode);
//    	Log.v(Services.TAG, "resultCode: " + resultCode);
//	    if (requestCode == TAKE_VIDEO_REQUEST && resultCode == RESULT_OK) {
//	        String picturePath = data.getStringExtra(
//	                CameraManager.EXTRA_PICTURE_FILE_PATH);
////	        processPictureWhenReady(picturePath);
//	    }
//	    Services.isRecording = false;
//	    super.onActivityResult(requestCode, resultCode, data);
//	}
}
