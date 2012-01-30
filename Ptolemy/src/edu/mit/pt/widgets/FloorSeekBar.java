package edu.mit.pt.widgets;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import edu.mit.pt.R;

public class FloorSeekBar extends View {
	private List<OnFloorSelectListener> listeners;

	private int trackPad = 5;
	private int trackWidth = 24;
	private int tickHeight = 4;
	private int tickWidth = 8;

	private int topBarHeight = 10;
	private int topBarWidth = 24;

	private int thumbHeight = 12;
	private int thumbWidth = 24;
	private int leftShift = 6;

	private float scrollDotRadius = 3;

	private int min;
	private int max;
	private int userSetFloor;
	private int floor;
	private int unsnappedY;
	private TextPaint minMaxTxt;
	private TextPaint scrollFloorTxt;
	private float textHeight;
	private Paint scrollTrackPaint;
	private Paint scrollThumbPaint;

	private boolean firstDraw;
	private Context context;

	private TextPaint indicTxt;
	private Paint circlePaint;

	private float indicHeight;

	private float indicPad;

	private Paint topBarPaint;

	private float extraPad;

	private Paint ghostScrollThumbPaint;

	public FloorSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		setup();
		loadAttributes(attrs);
	}

	public FloorSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setup();
		loadAttributes(attrs);
	}

	public FloorSeekBar(Context context) {
		super(context);
		this.context = context;
		setup();
	}

	private void setup() {
		listeners = new ArrayList<OnFloorSelectListener>();
		firstDraw = true;
		initText();
	}

	private void loadAttributes(AttributeSet attrs) {
		// Get attributes and store them.
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.FloorSeekBar);
		setMin(a.getInteger(R.styleable.FloorSeekBar_minFloor, 0));
		setMax(a.getInteger(R.styleable.FloorSeekBar_maxFloor, 0));
		setFloor(a.getInteger(R.styleable.FloorSeekBar_floor, 0));
		a.recycle();
	}

	private void initText() {

		indicTxt = new TextPaint();
		indicTxt.setAntiAlias(true);
		indicTxt.setTextSize(48 * getResources().getDisplayMetrics().density);
		indicTxt.setTextAlign(Paint.Align.CENTER);
		indicTxt.setColor(0xFF000000);
		indicPad = 15;
		indicHeight = -indicTxt.ascent();

		circlePaint = new Paint();
		circlePaint.setColor(0xFFBBBBBB);

		topBarPaint = new Paint();
		topBarPaint.setColor(0xFFBBBBBB);

		minMaxTxt = new TextPaint();
		minMaxTxt.setAntiAlias(true);
		minMaxTxt.setTextSize(20 * getResources().getDisplayMetrics().density);
		minMaxTxt.setColor(0xFF666666);

		scrollFloorTxt = new TextPaint();
		scrollFloorTxt.setAntiAlias(true);
		scrollFloorTxt
				.setTextSize(20 * getResources().getDisplayMetrics().density);
		scrollFloorTxt.setColor(0xFF222222);

		textHeight = -minMaxTxt.ascent();

		scrollTrackPaint = new Paint();
		scrollTrackPaint.setColor(0x99CCCCCC);

		scrollThumbPaint = new Paint();
		scrollThumbPaint.setColor(0xFF222222);

		ghostScrollThumbPaint = new Paint();
		ghostScrollThumbPaint.setColor(0xFF999999);

		extraPad = 100;
		invalidate();
	}

	public int getUserSetFloor(){
		return userSetFloor;
	}
	private void setUnsnappedY(int y) {
		unsnappedY = Math.max(getTrackTop(), Math.min(getTrackBottom(), y));
		// setFloor(getFloorFromY(unsnappedY)); // live update floor.
		invalidate();
	}

	private int getFloorFromY(float y) {
		int rawFloor = (min - 1)
				- Math.round((y - getTrackBottom()) / getSpacing());
		return Math.min(max, Math.max(min, rawFloor));
	}

	private int getYFromFloor(int floor) {
		return (int) (getTrackBottom() - (floor - (min - 1)) * getSpacing());

	}

	private float getSpacing() {
		return (float) (getTrackBottom() - getTrackTop()) / (max - min + 2);
	}

	private int getTrackBottom() {
		return (int) (this.getHeight() - getPaddingBottom() - textHeight
				- trackPad - extraPad);
	}

	private int getTrackTop() {
		return (int) (getPaddingTop() + trackPad + indicPad + extraPad);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Initialize unsnappedY on first draw.
		if (firstDraw) {
			firstDraw = false;
			setUnsnappedY(getYFromFloor(floor));
		}

		int targetFloor = getFloorFromY(unsnappedY);

		int centerXLine = this.getWidth() / 2;
		float textHeight = -minMaxTxt.ascent();

		// Draw floor indicator
		Paint strokePaint = new Paint(indicTxt);
		strokePaint.setARGB(160, 255, 255, 255);
		strokePaint.setTextAlign(Paint.Align.CENTER);
		//strokePaint.setTextSize(16);
		strokePaint.setTypeface(Typeface.DEFAULT_BOLD);
		strokePaint.setStyle(Paint.Style.STROKE);
		strokePaint.setStrokeWidth(4);

		Paint textPaint = new Paint(indicTxt);
		textPaint.setARGB(255, 51, 51, 51);
		textPaint.setTextAlign(Paint.Align.CENTER);
		//textPaint.setTextSize(16);
		textPaint.setTypeface(Typeface.DEFAULT_BOLD);
		
		canvas.drawText(Integer.toString(targetFloor), centerXLine,
				getTrackTop() + indicHeight - indicPad - extraPad, strokePaint);
		canvas.drawText(Integer.toString(targetFloor), centerXLine,
				getTrackTop() + indicHeight - indicPad - extraPad, textPaint);

		// Add numerical labels above and below.

		if (targetFloor != min) {
			canvas.drawText(Integer.toString(min),
					centerXLine + thumbWidth / 2 + leftShift, getYFromFloor(min)
							+ textHeight / 2, minMaxTxt);
		}
		if (targetFloor != max) {
			canvas.drawText(Integer.toString(max),
					centerXLine + thumbWidth / 2 + leftShift, getYFromFloor(max)
							+ textHeight / 2, minMaxTxt);
		}
		// Scroll

		// Draw scroll track
		canvas.drawRect(centerXLine - trackWidth / 2, getTrackTop(),
				centerXLine + trackWidth / 2, getTrackBottom(),
				scrollTrackPaint);

		// Draw graduated ticks
		for (int i = min - 1; i <= max + 1; i++) {
			int centerLine = getYFromFloor(i);
			int width, height;
			if (i == min - 1 || i == max + 1) {
				width = topBarWidth;
				height = topBarHeight;
				canvas.drawRect(new Rect(centerXLine - width / 2, centerLine
						- height / 2, centerXLine + width / 2, centerLine
						+ height / 2), topBarPaint);
			} else {
				height = tickHeight;
				width = tickWidth;
				canvas.drawCircle(centerXLine, centerLine, scrollDotRadius,
						circlePaint);
			}
			// canvas.drawRect(new Rect(centerXLine - width / 2, centerLine
			// - height / 2, centerXLine + width / 2, centerLine
			// + height / 2), i == floor ? selTxt : scrollTrackPaint);
			// canvas.drawCircle(centerXLine, centerLine, scrollDotRadius,
			// circlePaint);
		}
		// Draw scroll thumb with floor number



		canvas.drawText(Integer.toString(getFloorFromY(unsnappedY)),
				centerXLine + thumbWidth / 2 + leftShift, unsnappedY + textHeight / 2,
				scrollFloorTxt);

		canvas.drawRect(new Rect(centerXLine - thumbWidth / 2, unsnappedY
				- thumbHeight / 2, centerXLine + thumbWidth / 2, unsnappedY
				+ thumbHeight / 2), scrollThumbPaint);

		canvas.drawRect(new Rect(centerXLine - thumbWidth / 2,
				getYFromFloor(floor) - thumbHeight / 2, centerXLine
						+ thumbWidth / 2, getYFromFloor(floor) + thumbHeight
						/ 2), ghostScrollThumbPaint);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_UP:
			setUnsnappedY((int) event.getY());
			setFloor(getFloorFromY(unsnappedY));
			snapY();
			break;
		case MotionEvent.ACTION_MOVE:
			setUnsnappedY((int) event.getY());
			break;
		}
		return super.onTouchEvent(event);
	}

	// TODO: Make sure this is correct.
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
		if (userSetFloor >= min) {
			setFloor(userSetFloor);
		} else {
			setFloor(floor, true); // refresh to within bounds
		}
		snapY();
		invalidate();
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
		if (userSetFloor <= max) {
			setFloor(userSetFloor);
		} else {
			setFloor(floor, true); // refresh to within bounds
		}
		snapY();
		invalidate();
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor, boolean forced) {
		if (!forced) {
			userSetFloor = floor;
		}
		int oldFloor = this.floor;
		this.floor = Math.max(min, Math.min(max, floor));
		if (oldFloor != this.floor) {
			fireFloorListeners();
		}
		invalidate();
	}

	public void setFloor(int floor) {
		setFloor(floor, false);
	}

	public void snapY() {
		unsnappedY = getYFromFloor(floor);
		invalidate();
	}

	public void addFloorListener(OnFloorSelectListener l) {
		listeners.add(l);
	}

	public void removeFloorListener(OnFloorSelectListener l) {
		listeners.remove(l);
	}

	private void fireFloorListeners() {
		FloorSeekEvent event = new FloorSeekEvent(this, floor);
		for (OnFloorSelectListener l : listeners) {
			l.onFloorSelect(event);
		}
	}

	public class FloorSeekEvent extends EventObject {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int floor;

		public FloorSeekEvent(Object source, int floor) {
			super(source);
			this.floor = floor;
		}

		public int getFloor() {
			return floor;
		}
	}

	public interface OnFloorSelectListener {
		public void onFloorSelect(FloorSeekEvent event);
	}
}
