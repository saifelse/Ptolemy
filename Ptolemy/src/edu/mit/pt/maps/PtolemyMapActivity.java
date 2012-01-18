package edu.mit.pt.maps;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.MapActivity;
import com.google.android.maps.Overlay;

import edu.mit.pt.R;
import edu.mit.pt.data.RoomLoader;

public class PtolemyMapActivity extends MapActivity {
	PtolemyMapView mapView;
	PlacesItemizedOverlay placesItemizedOverlay;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_main);
		mapView = (PtolemyMapView) findViewById(R.id.mapview);
		List<Overlay> mapOverlays = mapView.getOverlays();
		//TODO: change blue arrow
		Drawable drawable = this.getResources().getDrawable(R.drawable.arrow_up_blue);
		placesItemizedOverlay = new PlacesItemizedOverlay(drawable);
		mapOverlays.add(placesItemizedOverlay);
		
		//load rooms
    	RoomLoader roomLoader = new RoomLoader();
    	roomLoader.execute(placesItemizedOverlay);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//PtolemyMapView mapView = (PtolemyMapView) findViewById(R.id.mapview);
		mapView.stop();
	}
}