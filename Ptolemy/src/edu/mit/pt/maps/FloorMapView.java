package edu.mit.pt.maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;

import edu.mit.pt.Config;
import edu.mit.pt.R;
import edu.mit.pt.data.LinkedList;
import edu.mit.pt.data.Place;
import edu.mit.pt.data.PlaceManager;
import edu.mit.pt.data.PlaceManager.MinMax;
import edu.mit.pt.widgets.FloorSeekBar;
import edu.mit.pt.widgets.FloorSeekBar.FloorSeekEvent;
import edu.mit.pt.widgets.FloorSeekBar.OnFloorSelectListener;

public class FloorMapView extends RelativeLayout {
	private Context context;
	private final PtolemyMapView mapView;
	private final FloorSeekBar seekBar;
	
	public final static int MAP_VIEW_ID = 0;
	public final static int SEEK_BAR_ID = 1;
	public final static int FLOOR_INDICATOR_ID = 2;

	private final static int SEEK_TOP_MARGIN = 0;
	private final static int SEEK_WIDTH = 75;
	private final static int SEEK_HEIGHT = LayoutParams.MATCH_PARENT;
	private final static int MIN_ZOOM_LEVEL = 19;

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
	public PlaceManager getPlaceManager(){
		return placeManager;
	}
	public void resumeUpdate() {
		Log.v(Config.TAG + "_f", "Resuming update!");
		timer.cancel();
		timer = new Timer();
		timer.schedule(new CheckUpdateTask(), 500);
		//timer.scheduleAtFixedRate(updateTask, 500, 500);
	}

	public void pauseUpdate() {
		Log.v(Config.TAG + "_f", "Cancelled update!");
		timer.cancel();
		//updateTask.cancel();
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if (hasWindowFocus) {
			updateMinMax();
			resumeUpdate();
		} else {
			pauseUpdate();
		}
	}

	// Periodically check if scrolling has taken place, if so, update.
	public class CheckUpdateTask extends TimerTask {
		private GeoPoint p;

		@Override
		public void run() {
			Log.v(Config.TAG + "_f", "We moved?");
			updateHandler.post(new Runnable() {
				public void run() {
					Log.v(Config.TAG + "_f", "Idk... let's check");
					if (p != null
							&& p.equals(mapView.getProjection()
									.fromPixels(0, 0))) {
						return;
					}
					Log.v(Config.TAG + "_f", "We moved!");
					p = mapView.getProjection().fromPixels(0, 0);
					updateMinMax(new Runnable(){
						@Override
						public void run() {
							Log.v(Config.TAG + "_hey", "Run check again!");
							timer.schedule(new CheckUpdateTask(), 500);							
						}
					});
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
		lpSeek.topMargin = SEEK_TOP_MARGIN;
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

		Log.v(Config.TAG + "_f", "FIRST TIME?");
		GeoPoint topLeft = mapView.getProjection().fromPixels(0, 0);
		GeoPoint bottomRight = mapView.getProjection().fromPixels(
				mapView.getWidth(), mapView.getHeight());
		Log.v(Config.TAG, topLeft + " : " + bottomRight);
		updateMinMax();

		// Set update.
		updateHandler = new Handler();
		timer = new Timer();
		updateTask = new CheckUpdateTask();

		resumeUpdate();
	}

	public void updateToFloor(int floor) {
		updateToFloor(floor, null);
	}
	public void updateToFloor(int floor, Runnable r) {
		this.floor = floor;
		seekBar.setFloor(floor);
		seekBar.snapY();
		if (mapView.getZoomLevel() < MIN_ZOOM_LEVEL){
			// handlePlaceData(new LinkedList<Place>());
			handlePlaceData(new ArrayList<Place>());
		}else {
			GeoPoint topLeft = mapView.getProjection().fromPixels(0, 0);
			GeoPoint bottomRight = mapView.getProjection().fromPixels(
					mapView.getWidth(), mapView.getHeight());
			new VisiblePlaceTask(r).execute(topLeft, bottomRight);
		}
	}
	/*
	public void setFloor(int floor, Runnable r) {
		updateToFloor(floor, r);
		seekBar.setFloor(floor);
		seekBar.snapY();
	}*/

	void showPlace(final Place place) {
		mapView.getController().animateTo(place.getPoint(), new Runnable() {
			public void run() {
				Log.v(Config.TAG, "We updating after move!");
				updateMinMax();
				updateToFloor(place.getFloor(), new Runnable(){
					@Override
					public void run() {
						Log.v(Config.TAG, "showPlace is setting floor to " + place.getFloor());
						placesOverlay.setFocusByPlace(place);
					}
				});
				
			}
		});
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		System.out.println(ev.getX());
		System.out.println(ev.getY());
		switch (ev.getAction()) {
		case MotionEvent.ACTION_UP:
			// Refresh floors based on what is visible.
			if (mapView.getZoomLevel() < MIN_ZOOM_LEVEL){
				seekBar.setVisibility(INVISIBLE);
			}else {
				seekBar.setVisibility(VISIBLE);
			}
			updateMinMax();
		}
		return false;
	}

	// getMinMax(), sets seekbars, then update to floor
	//  if zoom less than 20, (0,0), otherwise look it up.
	public void updateMinMax(){
		updateMinMax(null);
	}
	public void updateMinMax(Runnable r) {
		if (mapView.getZoomLevel() < MIN_ZOOM_LEVEL){
			handleMinMaxData(new MinMax(0,0));
			if(r != null) r.run();
		}else{
			GeoPoint topLeft = mapView.getProjection().fromPixels(0, 0);
			GeoPoint bottomRight = mapView.getProjection().fromPixels(
					mapView.getWidth(), mapView.getHeight());
			new UpdateMinMaxTask(r).execute(topLeft, bottomRight);
		}
		
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
	
	private void handleMinMaxData(MinMax minMax){
		seekBar.setMin(minMax.min);
		seekBar.setMax(minMax.max);
		updateToFloor(floor);
	}
	private class UpdateMinMaxTask extends AsyncTask<GeoPoint, Void, MinMax> {
		private Runnable postAction;
		public UpdateMinMaxTask(Runnable r){
			postAction = r;
		}
		 @Override
	     protected MinMax doInBackground(GeoPoint... gp) {
	    	 GeoPoint topLeft = gp[0];
	    	 GeoPoint bottomRight = gp[1];
	    	 return placeManager.getMinMax(topLeft, bottomRight);
	     }
		 
		 @Override
	     protected void onProgressUpdate(Void... x) {
	     }
		 
	     protected void onPostExecute(MinMax minMax) {
	    	 handleMinMaxData(minMax);
	    	 if(postAction != null) postAction.run();
	     }
	 }
	private void handlePlaceData(List<Place> places){
		List<Overlay> overlays = mapView.getOverlays();
		Resources resources = getContext().getResources();
		// Remove old places
		overlays.remove(placesOverlay);
		// Specify floor (floor-1 shows transparent, floor+1 shows outline)
		placesOverlay.setFloor(floor);
		// Add places that are on the specified floor
		placesOverlay.clear();
		
    	for (Place p : places) {
 			PlacesOverlayItem item = new PlacesOverlayItem(p, p.getName(),
 					p.getName(), p.getMarker(resources, false), p.getMarker(
 							resources, true), p.getMarkerDownBelow(resources),
 					placesOverlay);
 			placesOverlay.addOverlayItemNoUpdate(item);
 		}
 		placesOverlay.update();
 		Log.v(Config.TAG, "Adding " + places.size() + " places on F " + floor);
 		overlays.add(placesOverlay);
 		mapView.invalidate();
	}
	
	private class VisiblePlaceTask extends AsyncTask<GeoPoint, Void, List<Place>> {
		private Runnable postAction;
		public VisiblePlaceTask(Runnable r){
			postAction = r;
		}
		 @Override
	     protected List<Place> doInBackground(GeoPoint... gp) {
	    	 GeoPoint topLeft = gp[0];
	    	 GeoPoint bottomRight = gp[1];
	    	 return placeManager.getPlaces(topLeft, bottomRight, floor);
	     }
		 
		 @Override
	     protected void onProgressUpdate(Void... x) {
	     }
		 
	     protected void onPostExecute(List<Place> places) {
	 		handlePlaceData(places);
	 		if(postAction != null) postAction.run();
	     }
	 }
	
}
