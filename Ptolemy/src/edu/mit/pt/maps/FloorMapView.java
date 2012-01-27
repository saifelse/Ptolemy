package edu.mit.pt.maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;

import edu.mit.pt.Config;
import edu.mit.pt.R;
import edu.mit.pt.data.Place;
import edu.mit.pt.location.PlaceManager;
import edu.mit.pt.widgets.FloorSeekBar;
import edu.mit.pt.widgets.FloorSeekBar.FloorSeekEvent;
import edu.mit.pt.widgets.FloorSeekBar.OnFloorSelectListener;

// TODO: Fix updateMinMax behaviour.
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
	private PlaceManager placeManager;
	private PlacesItemizedOverlay placesOverlay;

	// Timer code
	protected Handler updateHandler;
	private Timer timer;
	private TimerTask updateTask;
	
	
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
	
	public void resumeUpdate(){
		Log.v(Config.TAG+"_f", "Resuming update!");
		updateTask.cancel();
		updateTask = new CheckUpdateTask();
		timer.scheduleAtFixedRate(updateTask, 1000, 1000);
	}
	public void pauseUpdate(){
		Log.v(Config.TAG+"_f", "Cancelled update!");
		updateTask.cancel();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus){
		if(hasWindowFocus){
			resumeUpdate();
		}else{
			pauseUpdate();
		}
	}
	// Periodically check if scrolling has taken place, if so, update.
	public class CheckUpdateTask extends TimerTask {
		private GeoPoint p;
		@Override
		public void run() {
			Log.v(Config.TAG+"_f", "We moved?");
			updateHandler.post(new Runnable(){
				@Override
				public void run() {
					Log.v(Config.TAG+"_f", "Idk... let's check");
					if(p!=null && p.equals(mapView.getProjection().fromPixels(0, 0))){
						return;
					}
					Log.v(Config.TAG+"_f", "We moved!");
					p = mapView.getProjection().fromPixels(0, 0);
					updateMinMax();
				}
			});
		}
		
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
		placeManager = new PlaceManager(context);
		// places = Place.getPlaces(context);
		placesOverlay = new PlacesItemizedOverlay(defaultMarker);

		Log.v(Config.TAG+"_f","FIRST TIME?");
		GeoPoint topLeft = mapView.getProjection().fromPixels(0, 0);
		GeoPoint bottomRight = mapView.getProjection().fromPixels(mapView.getWidth(), mapView.getHeight());
		Log.v(Config.TAG, topLeft+" : "+bottomRight);
		updateMinMax();
		
		// Set update.
		Log.v(Config.TAG+"_f", "Let's make a new Timer!");
		updateHandler = new Handler();
		timer = new Timer();
		updateTask = new CheckUpdateTask();
		
		//resumeUpdate();
	}

	private void updateToFloor(int floor) {
		this.floor = floor;
		// seekBar.setFloor(floor);
		List<Overlay> overlays = mapView.getOverlays();
		Resources resources = getContext().getResources();

		// Remove old places
		overlays.remove(placesOverlay);

		// Add places that are on the specified floor
		placesOverlay.clear();

		Log.v(Config.TAG, "Looking up visible places");
		List<Place> places = getVisiblePlaces();
		for (Place p : places) {
			Log.v(Config.TAG, p.getName() + " " + p.getFloor());
			PlacesOverlayItem item = new PlacesOverlayItem(p, p.getName(),
					p.getName(), p.getMarker(resources, false), p.getMarker(
							resources, true));
			placesOverlay.addOverlayItem(item);
		}
		Log.v(Config.TAG, "Adding " + places.size() + " places on F " + floor);
		overlays.add(placesOverlay);
		
		mapView.invalidate();
	}

	public void setFloor(int floor) {
		updateToFloor(floor);
		seekBar.setFloor(floor);
		seekBar.snapY();
	}

	private List<Place> getVisiblePlaces() {
		if (mapView.getZoomLevel() < 20)
			return new ArrayList<Place>();
		GeoPoint topLeft = mapView.getProjection().fromPixels(0, 0);
		GeoPoint bottomRight = mapView.getProjection().fromPixels(mapView.getWidth(), mapView.getHeight());
		return placeManager.getPlaces(topLeft, bottomRight, this.floor);
	}

	private List<Place> getPlaces() {
		if (mapView.getZoomLevel() < 20)
			return new ArrayList<Place>();
		GeoPoint topLeft = mapView.getProjection().fromPixels(0, 0);
		GeoPoint bottomRight = mapView.getProjection().fromPixels(mapView.getWidth(), mapView.getHeight());
		return placeManager.getPlaces(topLeft, bottomRight);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_UP:
			// Refresh floors based on what is visible.
			if (mapView.getZoomLevel() < 20)
				seekBar.setVisibility(INVISIBLE);
			else
				seekBar.setVisibility(VISIBLE);

			updateMinMax();

		}
		return false;
	}

	public void updateMinMax() {
		int maxFloor = 0;
		int minFloor = 0;
		
		Log.v(Config.TAG, "LatSpan: "+mapView.getLatitudeSpan()+", LonSpan: "+mapView.getLongitudeSpan());
		for (Place p : getPlaces()) {
			maxFloor = Math.max(maxFloor, p.getFloor());
			minFloor = Math.min(minFloor, p.getFloor());
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
