package edu.mit.pt.bookmarks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.MapActivity;

import edu.mit.pt.ActionBar;
import edu.mit.pt.Config;
import edu.mit.pt.R;
import edu.mit.pt.data.Place;
import edu.mit.pt.data.PtolemyDBOpenHelperSingleton;
import edu.mit.pt.maps.BrowsePlaceActivity;
import edu.mit.pt.maps.PlacesItemizedOverlay;
import edu.mit.pt.maps.PlacesOverlayItem;
import edu.mit.pt.maps.PtolemyMapView;

public class AddBookmarkActivity extends MapActivity {

	public final static String CUSTOM_NAME = "customName";
	public final static String PLACE = "place";
	private final int BROWSE_REQUEST = 1;

	protected BookmarkType type = BookmarkType.OTHER;
	private SQLiteDatabase db;
	protected Place place;
	private boolean userHasEditedType = false;
	private boolean userHasEditedPlace = false;

	private PlacesItemizedOverlay showPlaceItemizedOverlay;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_bookmark);
		ActionBar.setTitle(this, getNavTitle());
		ActionBar.setDefaultBackAction(this);

		// Disable add button until data is valid.
		findViewById(R.id.addBookmarkButton).setEnabled(false);

		// Configure button to show list of types to choose from.
		Button typeButton = (Button) findViewById(R.id.typeButton);
		final BookmarkType[] types = BookmarkType.values();
		final ArrayAdapter<BookmarkType> adapter = new ArrayAdapter<BookmarkType>(
				this, R.layout.bookmark_type_item, types);

		typeButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				new AlertDialog.Builder(v.getContext())
						.setTitle(
								getResources().getString(
										R.string.bookmark_type_prompt))
						.setAdapter(adapter,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										changeType(types[which], true);
										dialog.dismiss();
									}
								}).create().show();
			}
		});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Autocomplete on title.
		TitleAutoCompleteTextView autoComplete = (TitleAutoCompleteTextView) findViewById(R.id.editBookmarkTitle);
		db = PtolemyDBOpenHelperSingleton.getPtolemyDBOpenHelper(this)
				.getReadableDatabase();
		autoComplete.setup(db, this);
		autoComplete.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				checkShouldEnableButton();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

		});

		// Set up overlay
		Drawable defaultMarker = getResources().getDrawable(
				R.drawable.green_point);
		showPlaceItemizedOverlay = new PlacesItemizedOverlay(defaultMarker);

		if (Config.shouldShowBookmarkHelp(this)) {
			findViewById(R.id.bookmark_help).setVisibility(View.VISIBLE);
			Config.setShouldShowBookmarkHelp(this, false);
		}

		completeSetup();
	}

	protected String getNavTitle() {
		return "Add Bookmark";
	}

	protected void completeSetup() {
		long placeId = getIntent().getLongExtra(BookmarksActivity.PLACE_ID, -1);
		if (placeId == -1) {
			return;
		}
		place = Place.getPlace(this, placeId);
		setPlace(place, false);
	}

	void changeType(BookmarkType newType, boolean byUser) {
		if (!userHasEditedType || byUser) {
			type = newType;
			Button typeButton = (Button) findViewById(R.id.typeButton);
			typeButton.setText(type.getShortName());
			if (byUser) {
				userHasEditedType = true;
			}
		}
		checkShouldEnableButton();
	}

	void setPlace(Place place, boolean byUser) {
		if (userHasEditedPlace && !byUser) {
			return;
		}
		PtolemyMapView mapView = (PtolemyMapView) findViewById(R.id.mapview);
		mapView.getController().setCenter(place.getPoint());

		showPlaceItemizedOverlay.clear();

		// Add current place.
		Resources resources = getResources();

		PlacesOverlayItem item = new PlacesOverlayItem(place, place.getName(),
				place.getName(), place.getMarker(resources, false),
				place.getMarker(resources, true),
				place.getMarkerDownBelow(resources), showPlaceItemizedOverlay);

		showPlaceItemizedOverlay.setFloor(place.getFloor());
		showPlaceItemizedOverlay.addOverlayItem(item);
		showPlaceItemizedOverlay.setFocusedTitle(place.getName());
		mapView.getOverlays().add(showPlaceItemizedOverlay);

		this.place = place;
		Button placeButton = (Button) findViewById(R.id.pickedPlace);
		Log.v(Config.TAG, "Setting placeButton to have text " + place.getName());
		placeButton.setText(place.getName());
		placeButton.setTextColor(Color.parseColor("#111111"));
		if (byUser) {
			userHasEditedPlace = true;
		}
		checkShouldEnableButton();
	}

	private void checkShouldEnableButton() {
		String customName = ((TextView) findViewById(R.id.editBookmarkTitle))
				.getText().toString();
		if (customName.length() == 0 || place == null) {
			findViewById(R.id.addBookmarkButton).setEnabled(false);
		} else {
			findViewById(R.id.addBookmarkButton).setEnabled(true);
		}
	}

	public void startPickPlace(View v) {
		Intent intent = new Intent(this, BrowsePlaceActivity.class);
		TextView customNameView = (TextView) findViewById(R.id.editBookmarkTitle);
		String customName = customNameView.getText().toString();
		intent.putExtra(CUSTOM_NAME, customName);
		intent.putExtra(PLACE, place);
		startActivityForResult(intent, BROWSE_REQUEST);
	}

	public void addBookmark(View v) {
		TextView autoComplete = (TextView) findViewById(R.id.editBookmarkTitle);
		String customName = autoComplete.getText().toString();
		if (place == null || customName.length() == 0) {
			return;
		}
		Bookmark.addBookmark(this, customName, place, type);
		setResult(RESULT_OK);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case BROWSE_REQUEST:
			switch (resultCode) {
			case RESULT_OK:
				Place p = (Place) data.getParcelableExtra(PLACE);
				setPlace(p, true);
				break;
			}
			break;
		}
	}

	public void dismissHelp(View v) {
		v.setVisibility(View.GONE);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// db.close();
	}
}
