package edu.mit.pt.maps;

import java.util.List;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.maps.MapActivity;
import com.google.android.maps.Overlay;

import edu.mit.pt.ActionBar;
import edu.mit.pt.R;
import edu.mit.pt.bookmarks.BookmarksActivity;
import edu.mit.pt.data.RoomLoader;

public class PtolemyMapActivity extends MapActivity {
	PtolemyMapView mapView;
	PlacesItemizedOverlay placesItemizedOverlay;

	private final String ACTIVITY_TITLE = "Ptolemy";

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
			System.out.println(query);
		}

		List<Overlay> mapOverlays = mapView.getOverlays();
		// TODO: change blue arrow
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.arrow_up_blue);
		placesItemizedOverlay = new PlacesItemizedOverlay(drawable);
		mapOverlays.add(placesItemizedOverlay);

		// load rooms
		RoomLoader roomLoader = new RoomLoader();
		roomLoader.execute(placesItemizedOverlay);

		ActionBar.setTitle(this, ACTIVITY_TITLE);

		findViewById(R.id.searchbutton).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						onSearchRequested();

					}
				});

		findViewById(R.id.bookmarksbutton).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(v.getContext(), BookmarksActivity.class);
						startActivity(intent);
					}
				});
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// PtolemyMapView mapView = (PtolemyMapView) findViewById(R.id.mapview);
		mapView.stop();
	}
}