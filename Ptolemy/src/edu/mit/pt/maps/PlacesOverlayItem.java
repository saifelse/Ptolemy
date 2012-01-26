package edu.mit.pt.maps;

import android.graphics.drawable.Drawable;

import com.google.android.maps.OverlayItem;

import edu.mit.pt.data.Place;

public class PlacesOverlayItem extends OverlayItem {

	Place place;
	Drawable marker;
	Drawable markerSel;

	public PlacesOverlayItem(Place p, String title, String snippet,
			Drawable marker, Drawable markerSel) {
		super(p.getPoint(), title, snippet);
		this.place = p;
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());
		PlacesItemizedOverlay.boundCenterBottom(marker);
		markerSel.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());
		PlacesItemizedOverlay.boundCenterBottom(markerSel);
		this.marker = marker;
		this.markerSel = markerSel;
	}

	public Place getPlace() {
		return place;
	}

	@Override
	public Drawable getMarker(int stateBitset) {
		if (((stateBitset & OverlayItem.ITEM_STATE_SELECTED_MASK) == OverlayItem.ITEM_STATE_SELECTED_MASK)
				|| (stateBitset & OverlayItem.ITEM_STATE_PRESSED_MASK) == OverlayItem.ITEM_STATE_PRESSED_MASK) {
			return this.markerSel;
		}
		return marker;
	}

}
