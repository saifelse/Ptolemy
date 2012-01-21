package edu.mit.pt;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.google.android.maps.Overlay;

import edu.mit.pt.bookmarks.BookmarksActivity;
import edu.mit.pt.data.Place;
import edu.mit.pt.data.RoomLoader;
import edu.mit.pt.maps.LocationSetter;
import edu.mit.pt.maps.PlacesItemizedOverlay;
import edu.mit.pt.maps.PtolemyMapActivity;
import edu.mit.pt.maps.XPSOverlay;

public class PtolemyActivity extends PtolemyMapActivity {

	private final String ACTIVITY_TITLE = "Ptolemy";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		List<Overlay> mapOverlays = super.mapView.getOverlays();
		// TODO: change blue arrow
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.arrow_up_blue);
		super.placesItemizedOverlay = new PlacesItemizedOverlay(drawable);
		mapOverlays.add(placesItemizedOverlay);

		// load rooms
		RoomLoader roomLoader = new RoomLoader(this);
		roomLoader.execute(placesItemizedOverlay);

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
				onSearchRequested();
				startActivity(new Intent(v.getContext(),
						BookmarksActivity.class));
			}
		});

		ActionBar.setButtons(this, new View[] { compassButton, searchButton,
				bookmarksButton });

	}

	@Override
	public void onMarkerSelected(Place p) {
		// TODO Auto-generated method stub

	}

}
