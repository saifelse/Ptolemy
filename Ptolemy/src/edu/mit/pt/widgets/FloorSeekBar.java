package edu.mit.pt.widgets;

import edu.mit.pt.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class FloorSeekBar extends View {
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
		requestLayout();
		invalidate();
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
		requestLayout();
		invalidate();
	}
	public int getFloor() {
		return floor;
	}
	public void setFloor(int floor) {
		this.floor = Math.max(min, Math.min(max, floor));
		invalidate();
	}
	private int min;
	private int max;
	private int floor;
	
	
	// With XML
	public FloorSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// Get attributes and store them.
		TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.FloorSeekBar);
		setMin(a.getInteger(R.styleable.FloorSeekBar_minFloor, 0));
		setMax(a.getInteger(R.styleable.FloorSeekBar_maxFloor, 0));
		setFloor(a.getInteger(R.styleable.FloorSeekBar_floor, 0));
		a.recycle();
	}

	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		

		// TODO: draw shit.
	}
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		setMeasuredDimension(measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec));
	}
	private int measureWidth(int measureSpec){
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if(specMode == MeasureSpec.EXACTLY){
			return specSize;
		}else {
			// TODO: Calculate calcSize.
			int calcSize = measureSpec;
			if(specMode == MeasureSpec.AT_MOST){
				return Math.min(calcSize, specSize);
			}else{
				return calcSize;
			}
		}
	}
	private int measureHeight(int measureSpec){
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if(specMode == MeasureSpec.EXACTLY){
			return specSize;
		}else {
			// TODO: Calculate calcSize.
			int calcSize = measureSpec;
			if(specMode == MeasureSpec.AT_MOST){
				return Math.min(calcSize, specSize);
			}else{
				return calcSize;
			}
		}
	}
	
	
}
