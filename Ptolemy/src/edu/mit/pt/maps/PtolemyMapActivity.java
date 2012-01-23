package edu.mit.pt.maps;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.google.android.maps.Overlay;

import edu.mit.pt.ActionBar;
import edu.mit.pt.Config;
import edu.mit.pt.R;
import edu.mit.pt.bookmarks.BookmarksActivity;
import edu.mit.pt.data.Place;
import edu.mit.pt.data.RoomLoader;

public class PtolemyMapActivity extends PtolemyBaseMapActivity {
	protected PlacesItemizedOverlay placesItemizedOverlay;

	private final String ACTIVITY_TITLE = "Ptolemy";
	private PtolemyMapView mapView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.map_main);
		mapView = (PtolemyMapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		List<Overlay> mapOverlays = mapView.getOverlays();
		// TODO: change blue arrow
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.arrow_up_blue);
		placesItemizedOverlay = new PlacesItemizedOverlay(drawable);
		mapOverlays.add(placesItemizedOverlay);

		ActionBar.setTitle(this, ACTIVITY_TITLE);

		// Set up meOverlay:
		// Show user
		XPSOverlay meOverlay = new XPSOverlay(mapView);
		mapView.getOverlays().add(meOverlay);

		// Start Location data
		String skyhookUsername = getString(R.string.skyhook_username);
		String skyhookRealm = getString(R.string.skyhook_realm);

		LocationSetter.init(this, skyhookUsername, skyhookRealm, meOverlay);
		LocationSetter.resume();

		ImageButton compassButton = (ImageButton) getLayoutInflater().inflate(
				R.layout.menu_nav_button, null);
		compassButton.setImageResource(R.drawable.ic_menu_compass);
		compassButton.setContentDescription(getString(R.string.centre));
		compassButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mapView.getController().animateTo(LocationSetter.getPoint());
			}
		});

		ImageButton searchButton = (ImageButton) getLayoutInflater().inflate(
				R.layout.menu_nav_button, null);
		searchButton.setImageResource(R.drawable.ic_menu_search);
		searchButton.setContentDescription(getString(R.string.search));
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onSearchRequested();
			}
		});

		ImageButton bookmarksButton = (ImageButton) getLayoutInflater()
				.inflate(R.layout.menu_nav_button, null);
		bookmarksButton.setImageResource(R.drawable.ic_menu_bookmark);
		bookmarksButton.setContentDescription(getString(R.string.bookmarks));
		bookmarksButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(v.getContext(),
						BookmarksActivity.class));
			}
		});

		ActionBar.setButtons(this, new View[] { compassButton, searchButton,
				bookmarksButton });

	}

	@Override
	void showClassroom(final Place p) {
		List<PlacesOverlayItem> places = new ArrayList<PlacesOverlayItem>();
		places.add(new PlacesOverlayItem(p, p.getName(), p.getName(), p
				.getMarker(getResources())));
		mapView.getPlacesOverlay().setExtras(places);
		mapView.getController().animateTo(p.getPoint());
		Log.v(Config.TAG, "PLACE: " + p.getName());
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(Config.TAG, "Received requestCode " + requestCode);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// PtolemyMapView mapView = (PtolemyMapView) findViewById(R.id.mapview);
		LocationSetter.stop();
		mapView.stop();
	}
}
