package edu.mit.pt.maps;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.OverlayItem;

import edu.mit.pt.Config;
import edu.mit.pt.data.Place;

public class PlacesOverlayItem extends OverlayItem {

	Place place;
	Drawable marker;
	Drawable markerSel;
	Drawable above;
	Drawable below;

	PlacesItemizedOverlay overlay;

	public PlacesOverlayItem(Place p, String title, String snippet,
			Drawable marker, Drawable markerSel, Drawable below,
			Drawable above, PlacesItemizedOverlay overlay) {
		super(p.getPoint(), title, snippet);
		this.place = p;
		
		above.setBounds(0, 0, above.getIntrinsicWidth(), above.getIntrinsicHeight());
		below.setBounds(0, 0, below.getIntrinsicWidth(), below.getIntrinsicHeight());
		
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());
		PlacesItemizedOverlay.boundCenterBottom(marker);
		markerSel.setBounds(0, 0, markerSel.getIntrinsicWidth(),
				markerSel.getIntrinsicHeight());
		PlacesItemizedOverlay.boundCenterBottom(markerSel);
		this.marker = marker;
		this.markerSel = markerSel;
		this.above = above;
		this.below = below;
		this.overlay = overlay;
	}

	public Place getPlace() {
		return place;
	}

	@Override
	public Drawable getMarker(int stateBitset) {
		// if (((stateBitset & OverlayItem.ITEM_STATE_SELECTED_MASK) ==
		// OverlayItem.ITEM_STATE_SELECTED_MASK)
		// || (stateBitset & OverlayItem.ITEM_STATE_PRESSED_MASK) ==
		// OverlayItem.ITEM_STATE_PRESSED_MASK) {
		// return this.markerSel;
		// }
		if (place.getFloor() == overlay.getFloor() - 1) {
			Log.v(Config.TAG, "Floor below! "+place.getName());
			return below;
		} else if (place.getFloor() == overlay.getFloor() + 1) {
			Log.v(Config.TAG, "Floor above! "+place.getName());
			return above;
		}
		if (getTitle() != null && getTitle().equals(overlay.getFocusedTitle())) {
			return this.markerSel;
		}
		return marker;
	}

}
