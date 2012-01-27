package edu.mit.pt.maps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.OverlayItem;

import edu.mit.pt.Config;
import edu.mit.pt.R;
import edu.mit.pt.data.Place;

abstract public class PtolemyBaseMapActivity extends MapActivity {

	private final int DIALOG_INVALID_ROOM = 0;
	private String roomQuery = null;
	protected Place focusedPlace;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onNewIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// Try autocomplete ID first.
			Uri dataUri = intent.getData();
			Place p;
			if (dataUri != null) {
				int classroomId = Integer.valueOf(dataUri.getLastPathSegment());
				p = Place.getPlace(this, classroomId);
				roomQuery = p.getName();
			} else {
				roomQuery = intent.getStringExtra(SearchManager.QUERY);
				p = Place.getClassroom(this, roomQuery);
				if (p == null) {
					showDialog(DIALOG_INVALID_ROOM);
					return;
				}
			}
			showClassroom(p);
		}
	}

	@Override
	public boolean onSearchRequested() {
		Log.v(Config.TAG, "SEARCH REQUESTED.");
		startSearch(roomQuery, true, null, false);
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case DIALOG_INVALID_ROOM:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setNeutralButton("OK",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							onSearchRequested();
						}
					}).setTitle("Hm...").setMessage("");
			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		Resources res = getResources();
		((AlertDialog) dialog).setMessage(String.format(
				res.getString(R.string.room_not_found), roomQuery));
	}

	/**
	 * Sets behavior to deselect place when nothing is selected.
	 */
	protected void configureFloorMapView(FloorMapView floorMapView) {
		floorMapView.getPlacesOverlay().setOnFocusChangeListener(
				new ItemizedOverlay.OnFocusChangeListener() {

					@SuppressWarnings("rawtypes")
					public void onFocusChanged(ItemizedOverlay overlay,
							OverlayItem newFocus) {
						if (newFocus == null) {
							setPlace(null);
							((PlacesItemizedOverlay) overlay).setFocusedTitle(null);
							return;
						}
						PlacesOverlayItem pItem = (PlacesOverlayItem) newFocus;
						//((PlacesItemizedOverlay) overlay).setFocusedTitle(pItem.getTitle());
						setPlace(pItem.getPlace());
					}
				});
	}

	abstract void setPlace(Place p);

	abstract void showClassroom(Place p);

}
