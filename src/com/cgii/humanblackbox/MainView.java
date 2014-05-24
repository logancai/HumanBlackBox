package com.cgii.humanblackbox;

import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
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
    
    private final TextView mAccelerationView;
    
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
    
    private boolean mStarted;
    private boolean mForceStart;
    private boolean mVisible;
    private boolean mRunning;
    
    private long mBaseMillis;

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

        mAccelerationView = (TextView) findViewById(R.id.acceleration);

//        setBaseMillis(getElapsedRealtime());
        updateText();
    }
	
	/**
     * Sets the base value of the chronometer in milliseconds.
     */
    public void setBaseMillis(long baseMillis) {
        mBaseMillis = baseMillis;
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
    }
    
    /**
     * Stops the chronometer.
     */
    public void stop() {
        if (mRunning) {
            removeCallbacks(mUpdateTextRunnable);
        }
        mRunning = false;
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
    
    /**
     * Updates the value of the chronometer, visible for testing.
     */
    void updateText() {
    	mAccelerationView.setText("X: "+ 1 +
				"\nY: "+ 2 +
				"\nZ: "+ 3);
        if (mChangeListener != null) {
            mChangeListener.onChange();
        }
    }
    
}
