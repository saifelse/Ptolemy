package edu.mit.pt.maps;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import edu.mit.pt.ActionBar;
import edu.mit.pt.R;
import edu.mit.pt.bookmarks.AddBookmarkActivity;
import edu.mit.pt.data.Place;

public class BrowsePlaceActivity extends PtolemyBaseMapActivity {

	private final String ACTIVITY_TITLE = "Pick a location";
	private Place place;
	private PtolemyMapView mapView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_browse);
		mapView = (PtolemyMapView) findViewById(R.id.mapview);

		ActionBar.setTitle(this, ACTIVITY_TITLE);
		ActionBar.setDefaultBackAction(this);

		ImageButton searchButton = (ImageButton) getLayoutInflater().inflate(
				R.layout.menu_nav_button, null);
		searchButton.setImageResource(R.drawable.ic_menu_search);
		searchButton.setContentDescription(getString(R.string.search));
		searchButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onSearchRequested();
			}
		});

		ActionBar.setButton(this, searchButton);

		String customName = getIntent().getStringExtra(
				AddBookmarkActivity.CUSTOM_NAME);
		if (customName != null && customName.length() != 0) {
			Button nameButton = (Button) findViewById(R.id.backToAddTitle);
			nameButton.setText(customName);
		}

		findViewById(R.id.backToAddTitle).setOnClickListener(
				new OnClickListener() {

					public void onClick(View v) {
						if (place != null) {
							finishWithPlace(place);
						}
						finish();
					}
				});

		mapView.getPlacesOverlay().setOnTapListener(new OnTapListener() {

			public void onTap(Place p) {
				setPlace(p);
			}
		});

		Object prevObject = getIntent().getParcelableExtra(
				AddBookmarkActivity.PLACE);

		if (prevObject != null) {
			Place prevPlace = (Place) prevObject;
			mapView.getController().setCenter(prevPlace.getPoint());
		}
	}

	void setPlace(Place p) {
		TextView lowerText = (TextView) findViewById(R.id.backToAddTitle);
		lowerText.setText(Html.fromHtml("&#171; " + p.getName()));
		lowerText.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.green_button_bg));
		place = p;
	}

	void finishWithPlace(Place p) {
		Intent data = new Intent();
		data.putExtra(AddBookmarkActivity.PLACE, p);
		setResult(RESULT_OK, data);
		finish();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}


	@Override
	void showClassroom(final Place p) {
		List<PlacesOverlayItem> places = new ArrayList<PlacesOverlayItem>();
		places.add(new PlacesOverlayItem(p, p.getName(), p.getName(), p
				.getMarker(getResources())));
		mapView.getPlacesOverlay().setExtras(places);
		mapView.getController().animateTo(p.getPoint());
		setPlace(p);
	}

}
