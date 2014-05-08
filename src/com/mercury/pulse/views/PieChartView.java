package com.mercury.pulse.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class PieChartView extends View {
	
	private int								mPercentage;
	private Paint							mCentreNumberPaint;
	private RectF							mRect;
	private int[]							mColours;
	private int 							mChartColor;


	public PieChartView(Context context) {
		super(context);
		init(context);
	}

	public PieChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context c){
		//define our colors array
		mColours = new int[3];
		mColours[0] = Color.parseColor("#99CC00");
		mColours[1] = Color.parseColor("#FFBB33");
		mColours[2] = Color.parseColor("#FF4444");

		mCentreNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCentreNumberPaint.setColor(Color.parseColor("#efefef"));
		mCentreNumberPaint.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
		mCentreNumberPaint.setTextSize(140); //use large text size
		mRect = new RectF();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mRect.set(0, 0, getWidth(), getHeight());
		//draw the outer circle
		canvas.drawArc(mRect, (float) 0f, (float) 360.00, true, paintPieChart());
		//draw the inner text
		canvas.drawText(Integer.toString(mPercentage) + "%", getWidth()/2-105,
				(getHeight()/2)+40, mCentreNumberPaint);
		canvas.save();
		invalidate();
	}

	private Paint paintPieChart(){
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(mChartColor);
		return paint;
	}
	
	public void setData(int pieChartValue) {
		mPercentage = pieChartValue;		
		if (pieChartValue <= 59) {
			mChartColor = mColours[0];
		} else if ((pieChartValue >= 60) && (pieChartValue <= 79)) {
			mChartColor = mColours[1];
		} else {
			mChartColor = mColours[2];
		}
	}

}