/*
 * What needs to be done
 * 
 * [x]Get ACCELEROMETER
 * [] Get other sensors
 * [x] Create background service
 * [] Math of when the camera is invoked
 * [] Create camera and store
 * [] Code to draw the stuff on the screen of last invoked
 */

package com.cgii.humanblackbox;

import com.logancai.humanblackbox.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    
	public static String TAG = "com.cgii.humanblackbox";
	
	public static SensorServices mSensorServices;
	public static SensorEvent mSensorEvent;
	public static CameraServices mCameraServices;
	public static Thread mAsyncCalculation;
	public static Handler mHandler;
	private static boolean isRecording;
	
	public final static int recordingTimeInSeconds = 15;
	public final static long recordingTimeInMilSec = recordingTimeInSeconds * 1000;
	public final static long REFRESH_RATE_FPS = 45;
	public final static long DELAY = 1/45*1000; //in milliseconds
	
    /** Layout stuff*/
    public static TextView textView = null;
	public static TextView countView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		textView = (TextView) findViewById(R.id.debugTextView);
		
		//Begin Camera services
		Log.v(MainActivity.TAG, "Main onCreate Called");
		
		/*
		 * What is Handler?
		 * Handler communicates between the async thread and this thread.
		 * The thread is not allowed to directly touch the user interface
		 * so we must use this if we are using a thread.
		 */
		mHandler = new Handler(){
			public void handleMessage(Message msg){
				boolean isRecording = msg.getData().getBoolean("RECORDING");
				boolean requestUpdate = msg.getData().getBoolean("UPDATE");
				if(isRecording){
					calledCamera();
				}
				if (requestUpdate){
					debugSensorSetText();
				}
			}
		};
		Intent intent = new Intent(this, Services.class);
		startService(intent);
		
		mCameraServices = new CameraServices();
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		Log.v(MainActivity.TAG, "Main onStart Called");
		if (!(mSensorServices == null)){
			mSensorServices.start();
		}
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		Log.v(MainActivity.TAG, "Main onStop Called");
//		mSensorServices.stop(); //Need to remove later
	}
	
	@Override
	protected void onResume(){
		super.onResume();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	public void stopServices(View view){
		mSensorServices.stop();
		
	}
	public void restartServices(View view){
		mSensorServices.stop();
		mSensorServices.start();
	}
	public void launchCamera(View view){
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, recordingTimeInSeconds);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(intent, CameraServices.TAKE_VIDEO_REQUEST);
	}
	public void updateValues(View view){
		if (mSensorEvent != null){
			MainActivity.textView.setText("X: "+ mSensorEvent.values[0] +
					"\nY: "+ mSensorEvent.values[1] +
					"\nX: "+ mSensorEvent.values[2]);
		}
		else{
			MainActivity.textView.setText("Sensor is null");
		}
	}
	
	public static void changeTextInTextView(String text){
		MainActivity.textView.setText(text);
	}
	public static SensorEvent getSensorEvent(){
		return mSensorEvent;
	}
	public static long getDelay(){
		return DELAY;
	}
	public static Handler getHandler(){
		return mHandler;
	}
	public static boolean getRecodringStatus(){
		return isRecording;
	}
	public static boolean setRecodringStatus(boolean a){
		isRecording = a;
		return isRecording;
	}
	public static void debugSensorSetText(){
		MainActivity.textView.setText("X: "+ mSensorEvent.values[0] +
				"\nY: "+ mSensorEvent.values[1] +
				"\nZ: "+ mSensorEvent.values[2]);
	}
	public void calledCamera(){
//		try{
//			Intent intent = new Intent(this, CameraServices.class);
//			startActivity(intent);
//		}
//		catch(Exception e){
//			e.printStackTrace();
//			Log.e(TAG, "An exception has occured.");
//		}
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, recordingTimeInSeconds);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(intent, CameraServices.TAKE_VIDEO_REQUEST);
		isRecording = false;
		
	}
	
}
