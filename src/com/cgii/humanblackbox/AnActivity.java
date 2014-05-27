package com.cgii.humanblackbox;

import com.google.android.glass.media.CameraManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

public class AnActivity extends Activity{
	
	public final static int recordingTimeInSeconds = 15;
	private static final int TAKE_VIDEO_REQUEST = 1;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Services.mActivity = this;
	}
	
	private static void launchCamera(Activity activity){
		Log.v(Services.TAG, "LaunchCamera called called");
    	Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, recordingTimeInSeconds);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, TAKE_VIDEO_REQUEST);
		if (activity == null){
			Log.v(Services.TAG, "LaunchCamera activity is null");
		}
		activity.startActivityForResult(intent, 1);
		
    }
    
    public static Handler cameraHandler = new Handler(){
    	public void handleMessage(Message msg){
    		Log.v(Services.TAG, "Menu handler called");
    		boolean isRecording = msg.getData().getBoolean("message");
    		Log.v(Services.TAG, "Boolean is "+ isRecording);
    		if (isRecording){
    			launchCamera(Services.mActivity);
    		}
    	}
    };
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.v(Services.TAG, "onActivityResult called");
    	Log.v(Services.TAG, "requestCode: " + requestCode);
    	Log.v(Services.TAG, "resultCode: " + resultCode);
	    if (requestCode == TAKE_VIDEO_REQUEST && resultCode == RESULT_OK) {
	        String picturePath = data.getStringExtra(
	                CameraManager.EXTRA_PICTURE_FILE_PATH);
	        
//	        Services.isRecording = false;
			Log.v(Services.TAG, "The isRecording Booleans is now: "+Services.isRecording + " it should be false");
//	        processPictureWhenReady(picturePath);
	    }
	    Services.isRecording = false;
	    super.onActivityResult(requestCode, resultCode, data);
	}
}