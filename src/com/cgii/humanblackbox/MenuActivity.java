package com.cgii.humanblackbox;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.logancai.humanblackbox.R;

public class MenuActivity extends Activity{
	
	private final Handler mHandler = new Handler();
	
	public static boolean shouldFinishOnMenuClose = true;
	
	public final static int recordingTimeInSeconds = 15;
	private static final int TAKE_VIDEO_REQUEST = 2;
	private static final int TAKE_IMAGE_REQUEST = 1;
	private static final int SPEECH_REQUEST = 0;
	
	public MenuActivity(){
		Log.v(Services.TAG, "MenuActivity constructor");
		Services.mActivity = this;
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.v(Services.TAG, "MenuActivity onCreate");
		Services.mActivity = this;
	}
	
//	@Override
//	protected void onResume(){
//		super.onResume();
//		Log.v(Services.TAG, "MenuActivity onResume");
//		Services.mActivity = this;
//	}
	
//	@Override
//	protected void onPause(){
//		super.onPause();
//		Log.v(Services.TAG, "MenuActivity onPause");
//		Services.mActivity = this;
//	}
	
	@Override
    public void onAttachedToWindow() {
		Log.v(Services.TAG, "MenuActivity onAttach");
		Services.mActivity = this;
		super.onAttachedToWindow();
        openOptionsMenu();
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(Services.TAG, "MenuActivity onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(Services.TAG, "MenuActivity onOptionSelected");
		shouldFinishOnMenuClose = true;
		Services.mActivity = this;
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
            case R.id.demo_mode:
            	if(Services.demoMode){
            		Services.demoMode = false;
            	}
            	else{
            		Services.demoMode = true;
            	}
            	return true;
            case R.id.send_location:
            	Thread thread1 = new Thread(new SMSService(), "thread1");
            	thread1.start();
            	return true;
            case R.id.my_name:
            	shouldFinishOnMenuClose = false;
            	Intent intentName = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                this.startActivityForResult(intentName, SPEECH_REQUEST);
                return true;
            case R.id.record_video:
            	Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        		Services.fileUri = getOutputMediaFileUri(TAKE_VIDEO_REQUEST);
        		intent.putExtra(MediaStore.EXTRA_OUTPUT, Services.fileUri);
        		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
        		this.startActivityForResult(intent, TAKE_VIDEO_REQUEST);
        		return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	@Override
    public void onOptionsMenuClosed(Menu menu) {
        // Nothing else to do, closing the Activity.
		Log.v(Services.TAG, "MenuActivity onOptionMenuClosed");
		if (shouldFinishOnMenuClose){
			finish();
		}
    }
	
	/**
     * Posts a {@link Runnable} at the end of the message loop, overridable for testing.
     */
    protected void post(Runnable runnable) {
        mHandler.post(runnable);
    }
    
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }
    
    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == TAKE_IMAGE_REQUEST){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_"+ timeStamp + ".jpg");
        } else if(type == TAKE_VIDEO_REQUEST) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
    
    private static void launchCamera(Activity activity){
		Log.v(Services.TAG, "LaunchCamera called called");
//		shouldFinishOnMenuClose = false;
//    	Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, recordingTimeInSeconds);
//		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, TAKE_VIDEO_REQUEST);
//		if (activity == null){
//			Log.v(Services.TAG, "LaunchCamera activity is null");
//			Services.isRecording = false;
//		}
//		else{
//			activity.startActivityForResult(intent, 1);
//			Services.isRecording = false;
//		}
		
		
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		Services.fileUri = getOutputMediaFileUri(TAKE_VIDEO_REQUEST);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Services.fileUri);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
		activity.startActivityForResult(intent, TAKE_VIDEO_REQUEST);
    }
    
    public static Handler cameraHandler = new Handler(){
    	public void handleMessage(Message msg){
    		Log.v(Services.TAG, "camera handler called");
    		launchCamera(Services.mActivity);
    	}
    };
    
    public static Handler locationHandler = new Handler(){
    	public void handleMessage(Message msg){
    		Log.v(Services.TAG, "location handler called");
    		findAddress(Services.mActivity);
    	}
    };
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	Services.isRecording = false;
    	Log.v(Services.TAG, "onActivityResult called");
    	Log.v(Services.TAG, "requestCode: " + requestCode);
    	Log.v(Services.TAG, "resultCode: " + resultCode);
    	
    	if (requestCode == TAKE_VIDEO_REQUEST && resultCode == RESULT_OK) {
//	        String picturePath = data.getStringExtra(
//	                CameraManager.EXTRA_PICTURE_FILE_PATH);
//	        Log.v(Services.TAG, "Path to vide is: " + picturePath);
	        Services.isRecording = false;
	    }
    	
    	if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
	        List<String> results = data.getStringArrayListExtra(
	                RecognizerIntent.EXTRA_RESULTS);
	        String spokenText = results.get(0);
	        Services.name = spokenText;
	        Log.v(Services.TAG, "Name changed to: " + Services.name);
	    }
    	finish();
	}
    
    public static void findAddress(Activity activity){
    	List<Address> addressses = null;
		try 
		{
			Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
			addressses = geocoder.getFromLocation(Services.mLocation.getLatitude(), Services.mLocation.getLongitude(), 1);
		}
		catch (IOException e){
			e.printStackTrace();
			Log.v(Services.TAG, "SensorServices unable to get location");
		}
		catch (Exception e){
			e.printStackTrace();
			Log.v(Services.TAG, "SensorServices an exception occured");
		}
		if (addressses != null){
			Services.address = addressses.get(0).getAddressLine(0);
			Services.city = addressses.get(0).getAddressLine(1);
			Services.country = addressses.get(0).getAddressLine(2);
			Services.zipCode = addressses.get(0).getPostalCode();
		}
		else{
			Log.e(Services.TAG, "Address is null");
		}
    }
    
}
