package edu.mit.pt.maps;

import edu.mit.pt.Config;
import edu.mit.pt.widgets.FloorSeekBar;
import edu.mit.pt.widgets.FloorSeekBar.FloorSeekEvent;
import edu.mit.pt.widgets.FloorSeekBar.OnFloorSelectListener;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

public class FloorMapView extends RelativeLayout {
	public final static int MAP_VIEW_ID = 0;
	public final static int SEEK_BAR_ID = 1;
	
	private final PtolemyMapView mapView;
	private final FloorSeekBar seekBar;
	private final static int SEEK_WIDTH = 75;
	private final static int SEEK_HEIGHT = 400;
	
	public FloorMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mapView = new PtolemyMapView(context, attrs, defStyle);
		seekBar = new FloorSeekBar(context, attrs, defStyle);
		setup();
	}
	public FloorMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mapView = new PtolemyMapView(context, attrs);
		seekBar = new FloorSeekBar(context, attrs);
		setup();
	}
	public void setup(){
		// Define layout
		mapView.setId(FloorMapView.MAP_VIEW_ID);
		seekBar.setId(FloorMapView.SEEK_BAR_ID);

		RelativeLayout.LayoutParams lpMap = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		addView(mapView, lpMap);

		RelativeLayout.LayoutParams lpSeek = new RelativeLayout.LayoutParams(
				FloorMapView.SEEK_WIDTH, FloorMapView.SEEK_HEIGHT);
		lpSeek.topMargin = 100;
		lpSeek.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lpSeek.addRule(RelativeLayout.ALIGN_PARENT_TOP);

		addView(seekBar, lpSeek);

		// Register listeners
		seekBar.addFloorListener(new OnFloorSelectListener() {

			public void onFloorSelect(FloorSeekEvent event) {
				Log.v(Config.TAG, "New floor!!!! " + event.getFloor());
			}

		});
	}
	public PtolemyMapView getMapView() {
		return mapView;
	}

	public FloorSeekBar getSeekBar() {
		return seekBar;
	}
}
