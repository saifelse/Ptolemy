package edu.mit.pt.maps;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.MapActivity;

import edu.mit.pt.R;
import edu.mit.pt.data.Place;

abstract public class PtolemyMapActivity extends MapActivity {
	protected PtolemyMapView mapView;
	protected PlacesItemizedOverlay placesItemizedOverlay;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_main);
		mapView = (PtolemyMapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			if (query == null)
				System.out.println("I AM IN YOUR NULLZ");
			Log.i(PtolemyMapActivity.class.toString(), query);
		}
	}

	/*
	 * @Override public void onPause(){ LocationSetter.pause(); }
	 * 
	 * @Override public void onResume(){ LocationSetter.resume(); }
	 */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// PtolemyMapView mapView = (PtolemyMapView) findViewById(R.id.mapview);
		LocationSetter.stop();
		mapView.stop();
	}

	abstract public void onMarkerSelected(Place p);
}
