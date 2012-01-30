package edu.mit.pt.maps;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import edu.mit.pt.ActionBar;
import edu.mit.pt.Config;
import edu.mit.pt.R;
import edu.mit.pt.bookmarks.AddBookmarkActivity;
import edu.mit.pt.bookmarks.Bookmark;
import edu.mit.pt.bookmarks.BookmarksActivity;
import edu.mit.pt.bookmarks.EditBookmarkActivity;
import edu.mit.pt.data.Place;
import edu.mit.pt.data.PlaceType;
import edu.mit.pt.location.APGeoPoint;
import edu.mit.pt.tutorial.TourActivity;
import edu.mit.pt.tutorial.TourItemActivity;
import edu.mit.pt.tutorial.TourMapActivity;
import edu.mit.pt.tutorial.TourToolbarActivity;

public class PtolemyMapActivity extends PtolemyBaseMapActivity {
	protected PlacesItemizedOverlay placesItemizedOverlay;

	private final String ACTIVITY_TITLE = "Ptolemy";
	private final int TUTORIAL_INTRO_RESULT = 0;
	private final int TUTORIAL_MAP_RESULT = 1;
	private final int TUTORIAL_TOOLBAR_RESULT = 2;
	private final int TUTORIAL_ITEM_RESULT = 3;
	private final int ADD_EDIT_BOOKMARK_RESULT = 4;
	private final int BOOKMARKS_RESULT = 5;
	private final int NEAREST_RESULT = 6;
	// MAKE SURE THIS ROOM EXISTS!
	private final int TUTORIAL_FLOOR = 2;
	private final String TUTORIAL_ROOM = "38-370";

	private final int DIALOG_MULTIPLE_BOOKMARKS = 0;
	private final String BOOKMARKS = "bookmarks";

	private PtolemyMapView mapView;
	private XPSOverlay meOverlay;
	private List<Long> focusedBookmarkIds = new ArrayList<Long>();

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

		final ImageButton compassButton = (ImageButton) getLayoutInflater()
				.inflate(R.layout.menu_nav_button, null);
		compassButton.setImageResource(R.drawable.ic_menu_compass);
		compassButton.setContentDescription(getString(R.string.centre));
		final Context c = this;
		compassButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				APGeoPoint gp = LocationSetter.getInstance(
						PtolemyMapActivity.this, null).getPoint(c);
				if (gp != null) {
					mapView.getController().animateTo(gp);
					floorMapView.setFloor(gp.getFloor());
				}
			}
		});

		final ImageButton searchButton = (ImageButton) getLayoutInflater()
				.inflate(R.layout.menu_nav_button, null);
		searchButton.setImageResource(R.drawable.ic_menu_search);
		searchButton.setContentDescription(getString(R.string.search));
		searchButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onSearchRequested();
			}
		});

		final ImageButton bookmarksButton = (ImageButton) getLayoutInflater()
				.inflate(R.layout.menu_nav_button, null);
		bookmarksButton.setImageResource(R.drawable.ic_menu_bookmark);
		bookmarksButton.setContentDescription(getString(R.string.bookmarks));
		bookmarksButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivityForResult(new Intent(v.getContext(),
						BookmarksActivity.class), BOOKMARKS_RESULT);
			}
		});

		final ImageButton nearestButton = (ImageButton) getLayoutInflater()
				.inflate(R.layout.menu_nav_button, null);
		nearestButton.setImageResource(R.drawable.ic_menu_goto);
		nearestButton.setContentDescription(getString(R.string.bookmarks));
		nearestButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(v.getContext(), NearbyActivity.class);
				LocationSetter setter = LocationSetter.getInstance(
						v.getContext(), null);
				APGeoPoint p = setter.getPoint(v.getContext());
				intent.putExtra(NearbyActivity.LAT, p.getLatitudeE6());
				intent.putExtra(NearbyActivity.LON, p.getLongitudeE6());
				intent.putExtra(NearbyActivity.FLOOR, p.getFloor());
				startActivityForResult(intent, NEAREST_RESULT);
			}
		});

		ActionBar.setButtons(this, new View[] { compassButton, searchButton,
				nearestButton, bookmarksButton });

		final PlaceFilterButton athenaFilterButton = (PlaceFilterButton) findViewById(R.id.athena_filter_btn);
		setupFilterButton(athenaFilterButton, PlaceType.ATHENA);

		final PlaceFilterButton classroomFilterButton = (PlaceFilterButton) findViewById(R.id.classroom_filter_btn);
		setupFilterButton(classroomFilterButton, PlaceType.CLASSROOM);

		final PlaceFilterButton brMaleFilterButton = (PlaceFilterButton) findViewById(R.id.br_male_filter_btn);
		setupFilterButton(brMaleFilterButton, PlaceType.MTOILET);

		final PlaceFilterButton brFemaleFilterButton = (PlaceFilterButton) findViewById(R.id.br_female_filter_btn);
		setupFilterButton(brFemaleFilterButton, PlaceType.FTOILET);

		if (!handleIntent(getIntent())) {
			mapView.getController().setCenter(Config.DEFAULT_POINT);
		}

		if (!Config.isTourTaken(this)) {
			// Prevent user from clicking a button until the first
			// tutorial page comes up.
			compassButton.setClickable(false);
			searchButton.setClickable(false);
			bookmarksButton.setClickable(false);
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					PtolemyMapActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							startActivityForResult(
									new Intent(PtolemyMapActivity.this,
											TourActivity.class),
									TUTORIAL_INTRO_RESULT);
							compassButton.setClickable(true);
							searchButton.setClickable(true);
							bookmarksButton.setClickable(true);
						}
					});
				}
			}).start();
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
			Log.v(Config.TAG, "INTENT  : " + intent.getData());
			if (segments.size() == 1) {
				String room = segments.get(0);
				Place place = Place.getPlaceByName(this, room);
				if (place != null) {
					showPlaceOnMap(place);
					return true;
				}
			}
		}
		return false;
	}

	private void setToggleState(PlaceType placeType, boolean isChecked) {
		PlaceFilterButton button = PlaceFilterButton
				.getPlaceFilterButton(placeType);
		if (button != null) {
			button.setChecked(isChecked);
			handleButtonState(button);
		}
	}

	private void handleButtonState(PlaceFilterButton button) {
		if (button.isChecked()) {
			floorMapView.getPlaceManager().addFilter(button.getPlaceType());
		} else {
			floorMapView.getPlaceManager().removeFilter(button.getPlaceType());
		}
		floorMapView.updateMinMax();
	}

	private void setupFilterButton(final PlaceFilterButton button,
			final PlaceType placeType) {
		// Set up listener
		PlaceFilterButton.registerPlaceType(placeType, button);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handleButtonState(button);
			}
		});
		// Initialize
		handleButtonState(button);
	}

	/**
	 * Sets view at bottom to reflect focused place.
	 */
	@Override
	protected void setPlaceMeta(Place place) {
		focusedPlace = place;

		View metaView = findViewById(R.id.meta_view);
		View filterView = findViewById(R.id.filter_view);
		if (place == null) {
			metaView.setVisibility(View.GONE);
			filterView.setVisibility(View.VISIBLE);
			return;
		}
		((TextView) findViewById(R.id.place_confirm_text)).setText(place
				.getName());
		Log.v(Config.TAG, "TYPE: " + place.getPlaceType().name());

		setExtraButton();

		metaView.setVisibility(View.VISIBLE);
		filterView.setVisibility(View.GONE);
	}

	/**
	 * Moves map to place, highlights marker, and sets view at bottom to reflect
	 * selection.
	 */
	@Override
	void showPlaceOnMap(final Place place) {
		setToggleState(place.getPlaceType(), true);
		floorMapView.showPlace(place);
		setPlaceMeta(place);
	}

	private void setExtraButton() {
		focusedBookmarkIds = Bookmark.findInBookmarks(this, focusedPlace);
		ImageButton extraBtn = ((ImageButton) findViewById(R.id.place_extra_button));
		if (focusedBookmarkIds.isEmpty()) {
			extraBtn.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_menu_bookmark_add));
		} else {
			extraBtn.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_menu_edit));
		}
	}

	/**
	 * Called in map_main.xml.
	 */
	public void moveToFocusedPlace(View v) {
		if (focusedPlace == null) {
			return;
		}
		floorMapView.showPlace(focusedPlace);
	}

	public void handleExtraButtonClick(View v) {
		if (focusedPlace == null) {
			return;
		}
		Intent intent;
		if (focusedBookmarkIds.isEmpty()) {
			intent = new Intent(this, AddBookmarkActivity.class);
			intent.putExtra(BookmarksActivity.PLACE_ID, focusedPlace.getId());
		} else {
			if (focusedBookmarkIds.size() == 1) {
				intent = new Intent(this, EditBookmarkActivity.class);
				intent.putExtra(BookmarksActivity.BOOKMARK_ID,
						focusedBookmarkIds.get(0));
			} else {
				Bookmark[] bookmarks = new Bookmark[focusedBookmarkIds.size()];
				for (int i = 0; i < focusedBookmarkIds.size(); i++) {
					bookmarks[i] = Bookmark.getBookmark(this,
							focusedBookmarkIds.get(i));
				}
				Bundle data = new Bundle();
				data.putParcelableArray(BOOKMARKS, bookmarks);
				removeDialog(DIALOG_MULTIPLE_BOOKMARKS);
				showDialog(DIALOG_MULTIPLE_BOOKMARKS, data);
				return;
			}
		}
		startActivityForResult(intent, ADD_EDIT_BOOKMARK_RESULT);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case NEAREST_RESULT:
			switch (resultCode) {
			case RESULT_OK:
				Place place = (Place) data
						.getParcelableExtra(NearbyActivity.PLACE);
				showPlaceOnMap(place);
			}
		case ADD_EDIT_BOOKMARK_RESULT:
		case BOOKMARKS_RESULT:
			if (focusedPlace != null) {
				setExtraButton();
			}
			break;
		case TUTORIAL_INTRO_RESULT:
			Config.setTourTaken(this);
			switch (resultCode) {
			case RESULT_OK:
				startActivityForResult(new Intent(this, TourMapActivity.class),
						TUTORIAL_TOOLBAR_RESULT);
			}
			break;
		case TUTORIAL_TOOLBAR_RESULT:
			switch (resultCode) {
			case RESULT_OK:
				findViewById(R.id.tutorial_toolbar_img).setVisibility(
						View.VISIBLE);
				startActivityForResult(new Intent(PtolemyMapActivity.this,
						TourToolbarActivity.class), TUTORIAL_MAP_RESULT);
			}
			break;
		case TUTORIAL_MAP_RESULT:
			switch (resultCode) {
			case RESULT_OK:
				findViewById(R.id.tutorial_toolbar_img)
						.setVisibility(View.GONE);
				floorMapView.setFloor(TUTORIAL_FLOOR);
				showPlaceOnMap(Place.getPlaceByName(this, TUTORIAL_ROOM));
				new Thread(new Runnable() {
					public void run() {
						try {
							Thread.sleep(1300);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						PtolemyMapActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								findViewById(R.id.tutorial_add_bookmark_img)
										.setVisibility(View.VISIBLE);
								startActivityForResult(new Intent(
										PtolemyMapActivity.this,
										TourItemActivity.class),
										TUTORIAL_ITEM_RESULT);
								Config.setShouldShowBookmarkHelp(
										PtolemyMapActivity.this, true);
							}
						});
					}
				}).start();
			}
			break;
		case TUTORIAL_ITEM_RESULT:
			findViewById(R.id.tutorial_add_bookmark_img).setVisibility(
					View.GONE);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		Dialog dialog;
		switch (id) {
		case DIALOG_MULTIPLE_BOOKMARKS:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Multiple bookmarks found!");
			String[] items = new String[focusedBookmarkIds.size()];
			for (int i = 0; i < focusedBookmarkIds.size(); i++) {
				long bookmarkId = focusedBookmarkIds.get(i);
				items[i] = Bookmark.getBookmark(this, bookmarkId)
						.getCustomName();
			}

			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					Intent intent = new Intent(PtolemyMapActivity.this,
							EditBookmarkActivity.class);
					intent.putExtra(BookmarksActivity.BOOKMARK_ID,
							focusedBookmarkIds.get(item));
					startActivityForResult(intent, ADD_EDIT_BOOKMARK_RESULT);
				}
			});
			dialog = builder.create();
			break;
		default:
			dialog = null;
			break;
		}
		return dialog;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LocationSetter.getInstance(this, null).stop();
		mapView.stop();
	}
	
	public void handleCloseMeta(View v) {
		floorMapView.getPlacesOverlay().setFocus(null);
		floorMapView.getPlacesOverlay().update();
	}
}
