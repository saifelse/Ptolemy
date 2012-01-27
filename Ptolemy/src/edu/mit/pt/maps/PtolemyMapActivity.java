package edu.mit.pt.maps;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;

import edu.mit.pt.ActionBar;
import edu.mit.pt.Config;
import edu.mit.pt.R;
import edu.mit.pt.bookmarks.BookmarksActivity;
import edu.mit.pt.data.Place;

public class PtolemyMapActivity extends PtolemyBaseMapActivity {
	protected PlacesItemizedOverlay placesItemizedOverlay;

	private final String ACTIVITY_TITLE = "Ptolemy";
	private PtolemyMapView mapView;
	private FloorMapView floorMapView;

	@Override
	public void onPause() {
		super.onPause();
		LocationSetter.getInstance(this, null).pause();
	}

	@Override
	public void onResume() {
		super.onResume();
		LocationSetter.getInstance(this, null).resume();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.map_main);
		floorMapView = (FloorMapView) findViewById(R.id.floormapview);
		mapView = (PtolemyMapView) floorMapView.getMapView();
		configureFloorMapView(floorMapView);

		ActionBar.setTitle(this, ACTIVITY_TITLE);

		// Set up meOverlay:
		// Show user
		XPSOverlay meOverlay = new XPSOverlay(mapView);
		mapView.getOverlays().add(meOverlay);

		// Start Location data
		String skyhookUsername = getString(R.string.skyhook_username);
		String skyhookRealm = getString(R.string.skyhook_realm);

		LocationSetter.getInstance(this, meOverlay).resume();

		ImageButton compassButton = (ImageButton) getLayoutInflater().inflate(
				R.layout.menu_nav_button, null);
		compassButton.setImageResource(R.drawable.ic_menu_compass);
		compassButton.setContentDescription(getString(R.string.centre));
		final Context c = this;
		compassButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				GeoPoint gp = LocationSetter.getInstance(PtolemyMapActivity.this, null).getPoint(c);
				if (gp != null)
					mapView.getController().animateTo(gp);
			}
		});

		ImageButton searchButton = (ImageButton) getLayoutInflater().inflate(
				R.layout.menu_nav_button, null);
		searchButton.setImageResource(R.drawable.ic_menu_search);
		searchButton.setContentDescription(getString(R.string.search));
		searchButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onSearchRequested();
			}
		});

		ImageButton bookmarksButton = (ImageButton) getLayoutInflater()
				.inflate(R.layout.menu_nav_button, null);
		bookmarksButton.setImageResource(R.drawable.ic_menu_bookmark);
		bookmarksButton.setContentDescription(getString(R.string.bookmarks));
		bookmarksButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(v.getContext(),
						BookmarksActivity.class));
			}
		});

		ActionBar.setButtons(this, new View[] { compassButton, searchButton,
				bookmarksButton });

		Intent intent = getIntent();
		Log.v(Config.TAG, "INTENT: " + intent.getAction());
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			List<String> segments = intent.getData().getPathSegments();
			if (segments.size() == 1) {
				String room = segments.get(0);
				Place place = Place.getClassroom(this, room);
				if (place != null) {
					showClassroom(place);
					return;
				}
			}
		}
		mapView.getController().setCenter(Config.DEFAULT_POINT);

	}

	@Override
	protected void setPlace(Place place) {
		focusedPlace = place;
		View metaView = findViewById(R.id.meta_view);
		if (place == null) {
			metaView.setVisibility(View.GONE);
			return;
		}
		((TextView) findViewById(R.id.place_confirm_text)).setText(place
				.getName());
		Log.v(Config.TAG, "TYPE: " + place.getPlaceType().name());
		metaView.setVisibility(View.VISIBLE);
	}

	public void moveToFocusedPlace(View v) {
		if (focusedPlace == null) {
			return;
		}
		mapView.getController().animateTo(focusedPlace.getPoint());
	}

	@Override
	void showClassroom(final Place place) {
		/*
		// FIXME: _All_ animations need to call updateMinMax after finishing
		// animation.
		mapView.getController().animateTo(place.getPoint(), new Runnable(){

		@Override
		public void run() {
			Log.v(Config.TAG, "We updating after move!");
			floorMapView.updateMinMax();
			floorMapView.setFloor(place.getFloor());
			floorMapView.getPlacesOverlay().setFocusedTitle(place.getName());
		}
		});
		*/
		floorMapView.showPlace(place);
		setPlace(place);
	}

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
		LocationSetter.getInstance(this, null).stop();
		mapView.stop();
	}
}
