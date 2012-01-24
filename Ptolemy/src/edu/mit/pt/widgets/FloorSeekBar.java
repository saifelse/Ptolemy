package edu.mit.pt.widgets;

import edu.mit.pt.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class FloorSeekBar extends View {
	private int trackPad = 5;

	private int trackWidth = 4;

	private int tickHeight = 4;
	private int tickWidth = 8;

	private int thumbHeight = 6;
	private int thumbWidth = 10;

	private int min;
	private int max;
	private int floor;
	private int unsnappedY;
	private TextPaint mTxt;
	private float textHeight;
	private Paint scrollTrackPaint;
	private Paint scrollThumbPaint;

	public FloorSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Get attributes and store them.
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.FloorSeekBar);
		setMin(a.getInteger(R.styleable.FloorSeekBar_minFloor, 0));
		setMax(a.getInteger(R.styleable.FloorSeekBar_maxFloor, 0));
		setFloor(a.getInteger(R.styleable.FloorSeekBar_floor, 0));

		initText();

		a.recycle();
	}

	private void initText() {
		mTxt = new TextPaint();
		mTxt.setTextSize(20 * getResources().getDisplayMetrics().density);
		mTxt.setColor(0xFFFF0000);
		textHeight = -mTxt.ascent();

		scrollTrackPaint = new Paint();
		scrollTrackPaint.setColor(0xFFCCCCCC);

		scrollThumbPaint = new Paint();
		scrollThumbPaint.setColor(0xFF999999);

	}

	private void setUnsnappedY(int y) {
		unsnappedY = Math.max(getTrackTop(), Math.min(getTrackBottom(), y));
		invalidate();
	}

	private int getFloorFromY(float y) {
		return min - Math.round((y - getTrackBottom()) / getSpacing());
	}

	private int getYFromFloor(int floor) {
		return (int) (getTrackBottom() - (floor - min) * getSpacing());
	}

	private float getSpacing() {
		return (float) (getTrackBottom() - getTrackTop()) / (max - min);
	}

	private int getTrackBottom() {
		return (int) (this.getHeight() - getPaddingBottom() - textHeight - trackPad);
	}

	private int getTrackTop() {
		return getPaddingTop() + (int) textHeight + trackPad;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int centerXLine = this.getWidth() / 2;
		float textHeight = -mTxt.ascent();

		// Add numerical labels above and below.
		canvas.drawText(Integer.toString(max), getPaddingLeft(),
				getPaddingTop() + textHeight, mTxt);
		canvas.drawText(Integer.toString(min), getPaddingLeft(),
				this.getHeight() - getPaddingBottom(), mTxt);

		// Scroll

		// Draw scroll track
		canvas.drawRect(centerXLine - trackWidth / 2, getTrackTop(),
				centerXLine + trackWidth / 2, getTrackBottom(),
				scrollTrackPaint);

		// Draw graduated ticks
		for (int i = min; i <= max; i++) {
			int centerLine = getYFromFloor(i);
			canvas.drawRect(new Rect(centerXLine - tickWidth / 2, centerLine
					- tickHeight / 2, centerXLine + tickWidth / 2, centerLine
					+ tickHeight / 2), scrollTrackPaint);
			// canvas.drawBitmap(i == floor ? dot : arrow, getPaddingLeft(),
			// trackBottom-(i-min)*spacing, new Paint());
		}
		// Draw scroll thumb.
		canvas.drawRect(new Rect(centerXLine - thumbWidth / 2, unsnappedY
				- thumbHeight / 2, centerXLine + thumbWidth / 2, unsnappedY
				+ thumbHeight / 2), scrollThumbPaint);
		canvas.drawText(Integer.toString(getFloorFromY(unsnappedY)),
				centerXLine + thumbWidth / 2, unsnappedY + textHeight / 2, mTxt);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		case MotionEvent.ACTION_UP:
			setFloor(getFloorFromY(unsnappedY));
			break;
		case MotionEvent.ACTION_MOVE:
			setUnsnappedY((int) event.getY());
			break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			return specSize;
		} else {
			// TODO: Calculate calcSize.
			int calcSize = measureSpec;
			if (specMode == MeasureSpec.AT_MOST) {
				return Math.min(calcSize, specSize);
			} else {
				return calcSize;
			}
		}
	}

	private int measureHeight(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			return specSize;
		} else {
			// TODO: Calculate calcSize.
			int calcSize = measureSpec;
			if (specMode == MeasureSpec.AT_MOST) {
				return Math.min(calcSize, specSize);
			} else {
				return calcSize;
			}
		}
	}

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
		setUnsnappedY(getYFromFloor(this.floor));
		invalidate();
	}

}
