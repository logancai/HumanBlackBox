package com.cgii.humanblackbox;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.glass.app.Card;
import com.google.android.glass.media.CameraManager;
import com.logancai.humanblackbox.R;

public class MenuActivity extends Activity{
	
	private final Handler mHandler = new Handler();
	
	public final static int recordingTimeInSeconds = 15;
	private static final int TAKE_VIDEO_REQUEST = 1;
	
	public MenuActivity(){
		Log.v(Services.TAG, "MenuActivity constructor");
		Services.mActivity = this;
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.v(Services.TAG, "MenuActivity onCreate");
		Services.mActivity = this;
	}
	
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
//            	Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//        		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, recordingTimeInSeconds);
//        		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//        		this.startActivityForResult(intent, TAKE_VIDEO_REQUEST);
//            	Services.mActivity = this;
            	Services.isRecording = true;
            	SensorServices.startRecording();
        		return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	@Override
    public void onOptionsMenuClosed(Menu menu) {
        // Nothing else to do, closing the Activity.
		Log.v(Services.TAG, "MenuActivity onOptionMenu");
		finish();
    }
	
	/**
     * Posts a {@link Runnable} at the end of the message loop, overridable for testing.
     */
    protected void post(Runnable runnable) {
        mHandler.post(runnable);
    }
    
    private static void launchCamera(Activity activity){
//    	MenuActivity mMenuActivity = new MenuActivity();
    	
		Log.v(Services.TAG, "LaunchCamera called called");
    	Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, recordingTimeInSeconds);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, TAKE_VIDEO_REQUEST);
		if (Services.mActivity == null){
			Log.v(Services.TAG, "LaunchCamera activity is null");
			Services.isRecording = false;
		}
		else{
//			try{
//				Log.v(Services.TAG, Services.mActivity.getCallingActivity().getClassName());
//			}
//			catch(NullPointerException e){
//				Log.v(Services.TAG, "LaunchCamera activity null pointer");
//			}
			Services.mActivity.startActivityForResult(intent, TAKE_VIDEO_REQUEST);
			postActivity();
			Services.isRecording = false;
		}
    }
    
    public static Handler cameraHandler = new Handler(){
    	public void handleMessage(Message msg){
    		Log.v(Services.TAG, "camera handler called");
    		boolean isRecording = msg.getData().getBoolean("message");
    		Log.v(Services.TAG, "Boolean is "+ isRecording);
    		if (isRecording){
    			launchCamera(Services.mActivity);
    		}
    	}
    };
    
    public static void postActivity(){
    	Log.v(Services.TAG, "postActivity called");
    }
    
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
	    
//	    File file = new File(data.getStringExtra(
//                CameraManager.EXTRA_PICTURE_FILE_PATH));
//	    Uri uri = Uri.fromFile(file);
//	    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(uri.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
//	    Card card = new Card(this);
//	    card.setImageLayout(Card.ImageLayout.LEFT);
//	    card.addImage(bitmap);
//	    card.setText("A new recording!");
//	    card.setFootnote("HumanBlackBox");
//	    View cardView = card.getView();
	    
	    super.onActivityResult(requestCode, resultCode, data);
	}
    
}
