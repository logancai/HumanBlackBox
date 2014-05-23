package com.logancai.humanblackbox;

import java.text.NumberFormat;
import java.util.List;

import com.logancai.humanblackbox.R;
//import com.logancai.humanblackbox.util.MathUtils;
import com.logancai.humanblackbox.OrientationManager;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class HumanBlackBoxView extends View{
	
	private static final float PLACE_TEXT_HEIGHT = 22.0f;
	
	private OrientationManager mOrientation;

	/** The actual heading that represents the direction that the user is facing. */
    private float mHeading;
	
    private final TextPaint mPlacePaint;
//    private final Bitmap mPlaceBitmap;
//    private final Rect mTextBounds;
//    private final List<Rect> mAllBounds;
//    private final NumberFormat mDistanceFormat;
//    private final String[] mDirections;
    private final ValueAnimator mAnimator;
    
    public HumanBlackBoxView(Context context) {
        this(context, null, 0);
    }

    public HumanBlackBoxView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
	public HumanBlackBoxView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mPlacePaint = new TextPaint();
        mPlacePaint.setStyle(Paint.Style.FILL);
        mPlacePaint.setAntiAlias(true);
        mPlacePaint.setColor(Color.WHITE);
        mPlacePaint.setTextSize(PLACE_TEXT_HEIGHT);
        mPlacePaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        
        mAnimator = new ValueAnimator();
//        setupAnimator();
	}
	
	public void setOrientationManager(OrientationManager orientationManager) {
        mOrientation = orientationManager;
    }
	
	public float getHeading() {
        return mHeading;
    }
	
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        drawWords(canvas);
        
        canvas.save();
    }
	
	private void drawWords(Canvas canvas) {
        canvas.drawText(Float.toString(mHeading), 0, 0, mPlacePaint);
        canvas.drawText("Hello Word", 0, 0, mPlacePaint);
    }
	

}
