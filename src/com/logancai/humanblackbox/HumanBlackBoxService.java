package com.logancai.humanblackbox;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

public class HumanBlackBoxService extends Service {
	
	private static final String LIVE_CARD_TAG = "humanblackbox";
	
	private OrientationManager mOrientationManager;
	private TextToSpeech mSpeech;
	private LiveCard mLiveCard;
    private HumanBlackBoxRender mRenderer;
	
    /**
     * A binder that gives other components access to the speech capabilities provided by the
     * service.
     */
    public class CompassBinder extends Binder {
        /**
         * Read the current heading aloud using the text-to-speech engine.
         */
        public void readHeadingAloud() {
            float heading = mOrientationManager.getHeading();
            String headingText = Float.toString(heading);
            headingText = "Hello world";
            mSpeech.speak(headingText, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private final CompassBinder mBinder = new CompassBinder();
    
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	public void onCreate(){
		super.onCreate();
		
		mSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // Do nothing.
            }
        });
		
		SensorManager sensorManager =
                (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mOrientationManager = new OrientationManager(sensorManager, locationManager);
		
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLiveCard == null) {
            mLiveCard = new LiveCard(this, LIVE_CARD_TAG);
            //Call Render
            mRenderer = new HumanBlackBoxRender(this, mOrientationManager);

            mLiveCard.setDirectRenderingEnabled(true).getSurfaceHolder().addCallback(mRenderer);

            // Display the options menu when the live card is tapped.
            Intent menuIntent = new Intent(this, HumanBlackBoxMenuActivity.class);
            menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
            mLiveCard.attach(this);
            mLiveCard.publish(PublishMode.REVEAL);
        } else {
            mLiveCard.navigate();
        }

        return START_STICKY;
    }
	
	@Override
    public void onDestroy() {
        if (mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.unpublish();
            mLiveCard = null;
        }

        mSpeech.shutdown();

        mSpeech = null;
        mOrientationManager = null;

        super.onDestroy();
    }
	
}
