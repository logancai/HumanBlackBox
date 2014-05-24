package com.cgii.humanblackbox;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import com.cgii.humanblackbox.MainView;
import com.google.android.glass.timeline.DirectRenderingCallback;

public class Drawer implements DirectRenderingCallback{
	
	private static final String TAG = Drawer.class.getSimpleName();
    private static final int COUNT_DOWN_VALUE = 3;
	
    private final CountDownView mCountDownView;
    private final MainView mView;
    
    private SurfaceHolder mHolder;
    private boolean mCountDownDone;
    private boolean mRenderingPaused;
    
    private final CountDownView.Listener mCountDownListener = new CountDownView.Listener() {

        @Override
        public void onTick(long millisUntilFinish) {
            if (mHolder != null) {
                draw(mCountDownView);
            }
        }

        @Override
        public void onFinish() {
            mCountDownDone = true;
//            mView.setBaseMillis(SystemClock.elapsedRealtime());
            updateRenderingState();
        }
    };
    
    private final MainView.Listener mViewListener = new MainView.Listener() {

        @Override
        public void onChange() {
            if (mHolder != null) {
                draw(mView);
            }
        }
    };
    
    public Drawer(Context context){
    	this(new CountDownView(context), new MainView(context));
    }
    
    public Drawer(CountDownView countDownView, MainView view) {
        mCountDownView = countDownView;
        mCountDownView.setCountDown(COUNT_DOWN_VALUE);
        mCountDownView.setListener(mCountDownListener);

        mView = view;
        mView.setListener(mViewListener);
    }
    
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// The creation of a new Surface implicitly resumes the rendering.
        mRenderingPaused = false;
        mHolder = holder;
        updateRenderingState();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// Measure and layout the view with the canvas dimensions.
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        mCountDownView.measure(measuredWidth, measuredHeight);
        mCountDownView.layout(
                0, 0, mCountDownView.getMeasuredWidth(), mCountDownView.getMeasuredHeight());

        mView.measure(measuredWidth, measuredHeight);
        mView.layout(
                0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mHolder = null;
        updateRenderingState();
	}

	@Override
	public void renderingPaused(SurfaceHolder holder, boolean paused) {
		mRenderingPaused = paused;
        updateRenderingState();
	}
	
	private void updateRenderingState() {
        if (mHolder != null && !mRenderingPaused) {
            if (mCountDownDone) {
                mView.start();
            } else {
                mCountDownView.start();
            }
        } else {
            mView.stop();
        }
    }
	
	private void draw(View view) {
        Canvas canvas;
        try {
            canvas = mHolder.lockCanvas();
        } catch (Exception e) {
            Log.e(TAG, "Unable to lock canvas: " + e);
            return;
        }
        if (canvas != null) {
            view.draw(canvas);
            mHolder.unlockCanvasAndPost(canvas);
        }
    }

}
