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
import edu.mit.pt.bookmarks.AddBookmarkActivity;
import edu.mit.pt.bookmarks.Bookmark;
import edu.mit.pt.bookmarks.BookmarksActivity;
import edu.mit.pt.bookmarks.EditBookmarkActivity;
import edu.mit.pt.data.Place;
import edu.mit.pt.tutorial.TourActivity;
import edu.mit.pt.tutorial.TourItemActivity;
import edu.mit.pt.tutorial.TourMapActivity;

public class PtolemyMapActivity extends PtolemyBaseMapActivity {
	protected PlacesItemizedOverlay placesItemizedOverlay;

	private final String ACTIVITY_TITLE = "Ptolemy";
	private final int TUTORIAL_MAP_RESULT = 0;
	private final int TUTORIAL_ITEM_RESULT = 1;
	// MAKE SURE THIS ROOM EXISTS!
	private final String TUTORIAL_ROOM = "36-212";
	
	private PtolemyMapView mapView;
	private FloorMapView floorMapView;
	private XPSOverlay meOverlay;
	// Stores the bookmarkId corresponding to the focused place, if applicable.
	private long focusedBookmarkId = -1;

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
		meOverlay = new XPSOverlay(mapView);
		mapView.getOverlays().add(meOverlay);

		// Start Location data
		LocationSetter.getInstance(this, meOverlay).resume();

		ImageButton compassButton = (ImageButton) getLayoutInflater().inflate(
				R.layout.menu_nav_button, null);
		compassButton.setImageResource(R.drawable.ic_menu_compass);
		compassButton.setContentDescription(getString(R.string.centre));
		final Context c = this;
		compassButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				GeoPoint gp = LocationSetter.getInstance(
						PtolemyMapActivity.this, null).getPoint(c);
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

		if (!handleIntent(getIntent())) {
			mapView.getController().setCenter(Config.DEFAULT_POINT);
		}

		if (!Config.isTourTaken(this)) {
			startActivityForResult(new Intent(this, TourActivity.class), TUTORIAL_MAP_RESULT);
		}

	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);
	}

	private boolean handleIntent(Intent intent) {
		Log.v(Config.TAG, "INTENT: " + intent.getAction());
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			List<String> segments = intent.getData().getPathSegments();
			if (segments.size() == 1) {
				String room = segments.get(0);
				Place place = Place.getClassroom(this, room);
				if (place != null) {
					showClassroom(place);
					return true;
				}
			}
		}
		return false;
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

		focusedBookmarkId = Bookmark.findInBookmarks(this, focusedPlace);

		ImageButton extraBtn = ((ImageButton) findViewById(R.id.place_extra_button));
		if (focusedBookmarkId == -1) {
			extraBtn.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_menu_bookmark_add));
		} else {
			extraBtn.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_menu_edit));
		}

		metaView.setVisibility(View.VISIBLE);
	}

	public void moveToFocusedPlace(View v) {
		if (focusedPlace == null) {
			return;
		}
		mapView.getController().animateTo(focusedPlace.getPoint());
	}

	public void handleExtraButtonClick(View v) {
		if (focusedPlace == null) {
			return;
		}
		Intent intent;
		if (focusedBookmarkId != -1) {
			intent = new Intent(this, EditBookmarkActivity.class);
			intent.putExtra(BookmarksActivity.BOOKMARK_ID, focusedBookmarkId);
		} else {
			intent = new Intent(this, AddBookmarkActivity.class);
			intent.putExtra(BookmarksActivity.PLACE_ID, focusedPlace.getId());
		}
		startActivity(intent);
	}

	@Override
	void showClassroom(final Place place) {
		/*
		 * // FIXME: _All_ animations need to call updateMinMax after finishing
		 * // animation. mapView.getController().animateTo(place.getPoint(), new
		 * Runnable(){
		 * 
		 * @Override public void run() { Log.v(Config.TAG,
		 * "We updating after move!"); floorMapView.updateMinMax();
		 * floorMapView.setFloor(place.getFloor());
		 * floorMapView.getPlacesOverlay().setFocusedTitle(place.getName()); }
		 * });
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
		switch (requestCode) {
		case TUTORIAL_MAP_RESULT:
			switch (resultCode) {
			case RESULT_OK:
				startActivityForResult(new Intent(this, TourMapActivity.class), TUTORIAL_ITEM_RESULT);
			}
			break;
		case TUTORIAL_ITEM_RESULT:
			switch (resultCode) {
			case RESULT_OK:
				showClassroom(Place.getClassroom(this, TUTORIAL_ROOM));
				startActivityForResult(new Intent(this, TourItemActivity.class), TUTORIAL_ITEM_RESULT);
			}
			break;
		}
		Log.v(Config.TAG, "Received requestCode " + requestCode);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LocationSetter.getInstance(this, null).stop();
		mapView.stop();
	}
}
