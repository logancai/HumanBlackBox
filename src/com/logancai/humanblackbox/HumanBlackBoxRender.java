package com.logancai.humanblackbox;

import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.logancai.humanblackbox.HumanBlackBoxRender;
import com.logancai.humanblackbox.R;
import com.google.android.glass.sample.compass.OrientationManager;
import com.google.android.glass.sample.compass.model.Place;
import com.google.android.glass.timeline.DirectRenderingCallback;

public class HumanBlackBoxRender implements DirectRenderingCallback {
	
	private static final String TAG = HumanBlackBoxRender.class.getSimpleName();
	
	 /** The refresh rate, in frames per second, of the compass. */
    private static final int REFRESH_RATE_FPS = 45;

    /** The duration, in milliseconds, of one frame. */
    private static final long FRAME_TIME_MILLIS = TimeUnit.SECONDS.toMillis(1) / REFRESH_RATE_FPS;
    
    private boolean mRenderingPaused;
    
    private final FrameLayout mLayout;
    private final RelativeLayout mTipsContainer;
    private final HumanBlackBoxView mHumanBlackBoxView;
    private final TextView mTipsView;
    private SurfaceHolder mHolder;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private RenderThread mRenderThread;
    private final OrientationManager mOrientationManager;
	
    public HumanBlackBoxRender (Context context, OrientationManager orientationManager){
    	LayoutInflater inflater = LayoutInflater.from(context);
        mLayout = (FrameLayout) inflater.inflate(R.layout.humanblackbox, null);
        mLayout.setWillNotDraw(false);

        mHumanBlackBoxView = (HumanBlackBoxView) mLayout.findViewById(R.id.humanblackbox);
        mTipsContainer = (RelativeLayout) mLayout.findViewById(R.id.tips_container);
        mTipsView = (TextView) mLayout.findViewById(R.id.tips_view);

        mOrientationManager = orientationManager;

        mHumanBlackBoxView.setOrientationManager(mOrientationManager);
    }
    
    /*
     * surfaceCreated, surfaceChanged, surfaceDestroyed, renderingPaused
     * are from DirectRenderingCallback
     * 
     */
    
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mRenderingPaused = false;
        mHolder = holder;
        updateRenderingState();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mSurfaceWidth = width;
        mSurfaceHeight = height;
        doLayout();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mHolder = null;
        updateRenderingState();
	}

	@Override
	public void renderingPaused(SurfaceHolder arg0, boolean arg1) {
		mRenderingPaused = arg1;
        updateRenderingState();
	}
	
	/**
     * Requests that the views redo their layout. This must be called manually every time the
     * tips view's text is updated because this layout doesn't exist in a GUI thread where those
     * requests will be enqueued automatically.
     */
    private void doLayout() {
        // Measure and update the layout so that it will take up the entire surface space
        // when it is drawn.
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(mSurfaceWidth,
                View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(mSurfaceHeight,
                View.MeasureSpec.EXACTLY);

        mLayout.measure(measuredWidth, measuredHeight);
        mLayout.layout(0, 0, mLayout.getMeasuredWidth(), mLayout.getMeasuredHeight());
    }
	
	/**
     * Repaints.
     */
    private synchronized void repaint() {
        Canvas canvas = null;

        try {
            canvas = mHolder.lockCanvas();
        } catch (RuntimeException e) {
            Log.d(TAG, "lockCanvas failed", e);
        }

        if (canvas != null) {
            canvas.drawColor(Color.BLACK);
            mLayout.draw(canvas);

            try {
                mHolder.unlockCanvasAndPost(canvas);
            } catch (RuntimeException e) {
                Log.d(TAG, "unlockCanvasAndPost failed", e);
            }
        }
    }
	
	/**
     * Shows or hides the tip view with an appropriate message based on the current accuracy of the
     * measurments.
     */
	private void updateTipsView() {
//        int stringId = 0;

        // Only one message (with magnetic interference being higher priority than pitch too steep)
        // will be displayed in the tip.
//        if (mInterference) {
//            stringId = R.string.magnetic_interference;
//        } else if (mTooSteep) {
//            stringId = R.string.pitch_too_steep;
//        }

//        boolean show = (stringId != 0);
//
//        if (show) {
//            mTipsView.setText(stringId);
//            doLayout();
//        }
//
//        if (mTipsContainer.getAnimation() == null) {
//            float newAlpha = (show ? 1.0f : 0.0f);
//            mTipsContainer.animate().alpha(newAlpha).start();
//        }
    }
	
	 private void updateRenderingState() {
	        boolean shouldRender = (mHolder != null) && !mRenderingPaused;
	        boolean isRendering = (mRenderThread != null);

	        if (shouldRender != isRendering) {
	            if (shouldRender) {
	                mOrientationManager.addOnChangedListener(mCompassListener);
	                mOrientationManager.start();

	                if (mOrientationManager.hasLocation()) {
	                    Location location = mOrientationManager.getLocation();
	                }

	                mRenderThread = new RenderThread();
	                mRenderThread.start();
	            } else {
	                mRenderThread.quit();
	                mRenderThread = null;

//	                mOrientationManager.removeOnChangedListener(mCompassListener);
	                mOrientationManager.stop();

	            }
	        }
	    }
	 
	 /**
     * Redraws the screen in the background.
     */
    private class RenderThread extends Thread {
        private boolean mShouldRun;

        /**
         * Initializes the background rendering thread.
         */
        public RenderThread() {
            mShouldRun = true;
        }

        /**
         * Returns true if the rendering thread should continue to run.
         *
         * @return true if the rendering thread should continue to run
         */
        private synchronized boolean shouldRun() {
            return mShouldRun;
        }

        /**
         * Requests that the rendering thread exit at the next opportunity.
         */
        public synchronized void quit() {
            mShouldRun = false;
        }

        @Override
        public void run() {
            while (shouldRun()) {
                long frameStart = SystemClock.elapsedRealtime();
                repaint();
                long frameLength = SystemClock.elapsedRealtime() - frameStart;

                long sleepTime = FRAME_TIME_MILLIS - frameLength;
                if (sleepTime > 0) {
                    SystemClock.sleep(sleepTime);
                }
            }
        }
    }
	
    private final OrientationManager.OnChangedListener mCompassListener =
            new OrientationManager.OnChangedListener() {

        @Override
        public void onOrientationChanged(OrientationManager orientationManager) {
//        	mHumanBlackBoxView.setHeading(orientationManager.getHeading());
//
//            boolean oldTooSteep = mTooSteep;
//            mTooSteep = (Math.abs(orientationManager.getPitch()) > TOO_STEEP_PITCH_DEGREES);
//            if (mTooSteep != oldTooSteep) {
//                updateTipsView();
//            }
        }

        @Override
        public void onLocationChanged(OrientationManager orientationManager) {
//            Location location = orientationManager.getLocation();
//            List<Place> places = mLandmarks.getNearbyLandmarks(
//                    location.getLatitude(), location.getLongitude());
//            mHumanBlackBoxView.setNearbyPlaces(places);
        }

        @Override
        public void onAccuracyChanged(OrientationManager orientationManager) {
//            mInterference = orientationManager.hasInterference();
//            updateTipsView();
        }
    };
    
}
