package com.cgii.humanblackbox;

import android.content.Context;
import android.hardware.SensorEvent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.logancai.humanblackbox.R;

public class MainView extends FrameLayout{
	
	/**
     * Interface to listen for changes on the view layout.
     */
    public interface Listener {
        /** Notified of a change in the view. */
        public void onChange();
    }
    
    /** About 24 FPS, visible for testing. */
    static final long DELAY_MILLIS = 41;
    
    private final TextView xAccelerationView;
    private final TextView yAccelerationView;
    private final TextView zAccelerationView;
    private final TextView carModeView;
    private final TextView addressView;
    
    private final Handler mHandler = new Handler();
    private final Runnable mUpdateTextRunnable = new Runnable() {

        @Override
        public void run() {
            if (mRunning) {
                updateText();
                postDelayed(mUpdateTextRunnable, DELAY_MILLIS);
            }
        }
    };
    
    private boolean mRunning;

    private Listener mChangeListener;
	
	public MainView(Context context) {
//		super(context);
		this(context, null, 0);
	}
	
	public MainView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
	
	public MainView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        LayoutInflater.from(context).inflate(R.layout.card_view, this);

        xAccelerationView = (TextView) findViewById(R.id.Xacceleration);
        yAccelerationView = (TextView) findViewById(R.id.Yacceleration);
        zAccelerationView = (TextView) findViewById(R.id.Zacceleration);
        carModeView = (TextView) findViewById(R.id.CarMode);
        addressView = (TextView) findViewById(R.id.addresss);
        TextView appName = (TextView) findViewById(R.id.footer);
//        TextView timeView = (TextView) findViewById(R.id.timestamp);
		appName.setText("HumanBlackBox");
//		Time time =new Time(Time.getCurrentTimezone());	
//		
//		timeView.setText(Integer.toString(time.hour) + Integer.toString(time.minute) 
//				+ Integer.toString(time.second));
		
        updateText();
    }
    
    
    /**
     * Sets a {@link Listener}.
     */
    public void setListener(Listener listener) {
        mChangeListener = listener;
    }
    
    /**
     * Returns the set {@link Listener}.
     */
    public Listener getListener() {
        return mChangeListener;
    }
    
    /**
     * Starts the accelorometer.
     */
    public void start() {
        if (!mRunning) {
            postDelayed(mUpdateTextRunnable, DELAY_MILLIS);
        }
        mRunning = true;
        
//        //Launch SensorServices
//		Services.mSensorServices.start();
		
    }
    
    /**
     * Stops the chronometer.
     */
    public void stop() {
        if (mRunning) {
            removeCallbacks(mUpdateTextRunnable);
        }
        mRunning = false;
//        Services.mSensorServices.stop();
    }
    
    @Override
    public boolean postDelayed(Runnable action, long delayMillis) {
        return mHandler.postDelayed(action, delayMillis);
    }
    
    @Override
    public boolean removeCallbacks(Runnable action) {
        mHandler.removeCallbacks(action);
        return true;
    }
    
    int count = 1;
    
    /**
     * Updates the value of the chronometer, visible for testing.
     */
    void updateText() {
    	SensorEvent event = Services.mSensorEvent;
//    	mAccelerationView.setText("X: "+ Float.toString(event.values[0]) +
//				" Y: "+ Float.toString(event.values[1]) +
//				" Z: "+ Float.toString(event.values[2]));
//    	mAccelerationView.setText(Integer.toString(count));
//    	count += 1;
    	if (event != null){
    		//Always show stuff
    		carModeView.setText("Demo Mode: "+Boolean.toString(Services.demoMode));
    		addressView.setText(/*"Current location: " + */Services.address + " " + 
    				Services.zipCode);
    		if (Services.demoMode){
    			xAccelerationView.setVisibility(View.VISIBLE);
    			yAccelerationView.setVisibility(View.VISIBLE);
    			zAccelerationView.setVisibility(View.VISIBLE);
    			xAccelerationView.setText("X: "+ Float.toString(event.values[0]));
        		yAccelerationView.setText("Y: "+ Float.toString(event.values[1]));
        		zAccelerationView.setText("Z: "+ Float.toString(event.values[2]));
    		}
    		else{
    			xAccelerationView.setVisibility(View.INVISIBLE);
    			yAccelerationView.setVisibility(View.INVISIBLE);
    			zAccelerationView.setVisibility(View.INVISIBLE);
    		}
    		
    	}
    	
        if (mChangeListener != null) {
            mChangeListener.onChange();
        }
    }
    
}
