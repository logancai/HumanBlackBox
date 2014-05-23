package com.cgii.humanblackbox;

import java.io.File;
import java.io.IOException;

import com.logancai.humanblackbox.R;

import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;
import android.widget.VideoView;

//https://www.youtube.com/watch?v=ZScE1aXS1Rs

public class CameraServices extends Activity implements SurfaceHolder.Callback{
	int recordingDuration = 15;
	
	public static final int TAKE_VIDEO_REQUEST = 1;
	private Uri cameraVideoURI;
	
	MediaRecorder recorder;
	SurfaceHolder holder;
	private VideoView videoView;
	Camera camera;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.recordingview);
		videoView = (VideoView) findViewById(R.id.videoView1);
				
	}
	
	@Override
	protected void onStart(){
		if (!initCamera()){
			finish();
		}
		initRecorder();
	}
	
	@Override
	protected void onPause(){
		MainActivity.setRecodringStatus(false);
	}
	
	public CameraServices(){
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(MainActivity.TAG, "surfaceCreated");
		try{
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		}
		catch (IOException e){
			Log.v(MainActivity.TAG, "Could not start the preview");
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		//Do nothing
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (MainActivity.getRecodringStatus()){
			recorder.stop();
			MainActivity.setRecodringStatus(false);
		}
		recorder.release();
	}
	
	boolean initCamera() {
		try{
			camera = Camera.open();
			Camera.Parameters camParams = camera.getParameters();
			/* Fix for Samsung Phones*/
			camParams.set("cam_mode", 1);
			camParams.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
			camera.setParameters(camParams);
			
			holder = videoView.getHolder();
			holder.addCallback(this);
		}
		catch(RuntimeException re){
			Toast.makeText(this, "Camera could not initialize", Toast.LENGTH_SHORT).show();
			Log.v(MainActivity.TAG, "Could not initialize the camera");
			re.printStackTrace();
			return false;
		}
		System.out.println(true);
		Toast.makeText(this, "Camera initialized", Toast.LENGTH_SHORT).show();
		return true;
	}
	
	private void initRecorder() {
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		String date = today.year + "_" + (today.month+1) + "_" + today.monthDay + "_" + 
				today.hour + "h" + today.minute + "m" + today.second + "s";
		File path = Environment.getExternalStorageDirectory(); //Returns something like "/mnt/sdcard"
		String pathToSDCard = path.toString();
		String filePath = pathToSDCard + "/DCIM/Camera/" + date + ".mp4";
		
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioEncoder.DEFAULT);
		recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		recorder.setVideoSize(1920, 1080);
		recorder.setVideoFrameRate(30); //we could change that later
		recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		recorder.setOutputFile(filePath);
		recorder.setMaxDuration((int)MainActivity.recordingTimeInMilSec);
		recorder.setPreviewDisplay(holder.getSurface());
		try{
			recorder.prepare();
			recorder.start();
		}
		catch(IllegalStateException e){
			e.printStackTrace();
			Log.e(MainActivity.TAG, "Surfacecreated illegalstate");
		}
		catch (IOException e){
			e.printStackTrace();
			Log.e(MainActivity.TAG, "Surfacecreated io exception");
		}
	}
	
//	protected void OnActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode == TAKE_VIDEO_REQUEST && resultCode == RESULT_OK) {
//			MainActivity.setRecodringStatus(false);
//	        //The following line is for Google Glass only
//			/*
//			String picturePath = data.getStringExtra(
//	                CameraManager.EXTRA_PICTURE_FILE_PATH);
//	        */
//			
////			String[] projection = 
////				   { MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE }; 
////			@SuppressWarnings("deprecation")
////			Cursor cursor = managedQuery(cameraVideoURI, projection, null, null, null); 
////			int column_index_data =
////			   cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA); 
////			int column_index_size =
////			   cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE); 
////			cursor.moveToFirst(); 
////			String recordedVideoFilePath = cursor.getString(column_index_data);
////			int recordedVideoFileSize = cursor.getInt(column_index_size);
//	    }
//
//	    super.onActivityResult(requestCode, resultCode, data);
//	}
//	
//	public void takeVideo(View View){
////		Time today = new Time(Time.getCurrentTimezone());
////		today.setToNow();
////		String date = today.year + "_" + (today.month + 1) + "_"
////				+ today.monthDay + "_" + today.hour + "h" + today.minute + "m"
////				+ today.second + "s";
////		String fileName = date + ".mp4";
//
//		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
////		ContentValues values = new ContentValues();
////		values.put(MediaStore.Video.Media.TITLE, fileName);
////		cameraVideoURI = getContentResolver().insert(
////				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
////		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, recordingDuration);
////		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
////		intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraVideoURI);
//		startActivityForResult(intent, TAKE_VIDEO_REQUEST);
//	}
}
