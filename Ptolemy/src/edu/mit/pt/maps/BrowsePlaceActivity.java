package edu.mit.pt.maps;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import edu.mit.pt.data.PlaceType;

public class BrowsePlaceActivity extends PtolemyBaseMapActivity {

	private final String ACTIVITY_TITLE = "Pick a location";
	
	private PtolemyMapView mapView;
	
	private CharSequence restoreText;
	private Drawable restoreDrawable;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_browse);
		floorMapView = (FloorMapView) findViewById(R.id.browsemapview);
		mapView = (PtolemyMapView) floorMapView.getMapView();

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
						if (focusedPlace != null) {
							finishWithPlace(focusedPlace);
						}
						finish();
					}
				});

		Object prevObject = getIntent().getParcelableExtra(
				AddBookmarkActivity.PLACE);

		if (prevObject != null) {
			Place prevPlace = (Place) prevObject;
			mapView.getController().setCenter(prevPlace.getPoint());
		}

		configureFloorMapView(floorMapView);
		
		floorMapView.getPlaceManager().addFilter(PlaceType.ATHENA);
		floorMapView.getPlaceManager().addFilter(PlaceType.CLASSROOM);
		floorMapView.getPlaceManager().addFilter(PlaceType.MTOILET);
		floorMapView.getPlaceManager().addFilter(PlaceType.FTOILET);
	}

	@Override
	protected void setPlaceMeta(Place p) {
		focusedPlace = p;
		TextView lowerText = (TextView) findViewById(R.id.backToAddTitle);
		CharSequence newText = null;
		Drawable newDrawable = null;
		if (p == null) {
			if (restoreText != null) newText = restoreText;
			if (restoreDrawable != null) newDrawable = restoreDrawable;
		} else {
			newText = Html.fromHtml("&#171; " + p.getName());
			newDrawable = getResources().getDrawable(
					R.drawable.green_button_bg);
			if (restoreText == null) restoreText = lowerText.getText();
			if (restoreDrawable == null) restoreDrawable = lowerText.getBackground();
		}
		lowerText.setText(newText);
		lowerText.setBackgroundDrawable(newDrawable);
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
	void showPlaceOnMap(final Place p) {
		floorMapView.showPlace(p);
		setPlaceMeta(p);
		/*
		mapView.getController().animateTo(p.getPoint());
		floorMapView.setFloor(p.getFloor());
		floorMapView.getPlacesOverlay().setFocusedTitle(p.getName());
		setPlace(p);
		*/
	}

}
