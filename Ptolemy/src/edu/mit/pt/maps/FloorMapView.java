package edu.mit.pt.maps;

import java.util.List;

import com.google.android.maps.Overlay;

import edu.mit.pt.Config;
import edu.mit.pt.R;
import edu.mit.pt.data.Place;
import edu.mit.pt.widgets.FloorSeekBar;
import edu.mit.pt.widgets.FloorSeekBar.FloorSeekEvent;
import edu.mit.pt.widgets.FloorSeekBar.OnFloorSelectListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class FloorMapView extends RelativeLayout {
	public final static int MAP_VIEW_ID = 0;
	public final static int SEEK_BAR_ID = 1;

	private final PtolemyMapView mapView;
	private final FloorSeekBar seekBar;
	private final static int SEEK_WIDTH = 75;
	private final static int SEEK_HEIGHT = 400;

	private Context context;
	// Places
	private int floor;
	private List<Place> places;
	private PlacesItemizedOverlay placesOverlay;

	public FloorMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		mapView = new PtolemyMapView(context, attrs, defStyle);
		seekBar = new FloorSeekBar(context, attrs, defStyle);
		setup();
	}

	public FloorMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		mapView = new PtolemyMapView(context, attrs);
		seekBar = new FloorSeekBar(context, attrs);
		setup();
	}

	public void setup() {
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
				updateToFloor(event.getFloor());
			}

		});
		
		Drawable defaultMarker = getResources().getDrawable(
				R.drawable.green_point);
		// Places
		places = Place.getPlacesExceptClassrooms(context);
		Log.v(Config.TAG, "Setting up FloorMapView with " + places.size()
				+ " places.");
		placesOverlay = new PlacesItemizedOverlay(defaultMarker);
		
		updateMinMax();
		//setFloor(0);
		
	}
	private void updateToFloor(int floor){
		this.floor = floor;
		//seekBar.setFloor(floor);
		List<Overlay> overlays = mapView.getOverlays();
		Resources resources = getContext().getResources();

		// Remove old places
		overlays.remove(placesOverlay);

		// Add places that are on the specified floor
		placesOverlay.clear();
		
		int count =0;
		for (Place p : places) {
			Log.v(Config.TAG, p.getName()+" "+p.getFloor());
			if (p.getFloor() == this.floor) {
				PlacesOverlayItem item = new PlacesOverlayItem(p, p.getName(),
						p.getName(), p.getMarker(resources));
				count++;
				placesOverlay.addOverlayItem(item);
			}
		}
		Log.v(Config.TAG, "Adding "+count+" places on floor "+floor);
		overlays.add(placesOverlay);
		mapView.invalidate();
	}
	public void setFloor(int floor) {
		updateToFloor(floor);
		seekBar.setFloor(floor);
		seekBar.snapY();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_UP:
			// Refresh floors based on what is visible.
			updateMinMax();
		}
		return false;
	}
	public void updateMinMax(){
		int maxFloor = 0;
		int minFloor = 0;
		Point pt = new Point();
		for (Place p : places) {
			mapView.getProjection().toPixels(p.getPoint(), pt);
			if (0 <= pt.x && pt.x <= getWidth() && 0 <= pt.y
					&& pt.y <= getHeight()) {
				maxFloor = Math.max(maxFloor, p.getFloor());
				minFloor = Math.min(minFloor, p.getFloor());
			}
		}
		seekBar.setMin(minFloor);
		seekBar.setMax(maxFloor);
	}
	public PtolemyMapView getMapView() {
		return mapView;
	}

	public FloorSeekBar getSeekBar() {
		return seekBar;
	}

	public PlacesItemizedOverlay getPlacesOverlay() {
		return placesOverlay;
	}
}
