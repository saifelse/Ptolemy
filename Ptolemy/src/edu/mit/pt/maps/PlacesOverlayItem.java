package edu.mit.pt.maps;

import android.graphics.drawable.Drawable;

import com.google.android.maps.OverlayItem;

import edu.mit.pt.data.Place;

public class PlacesOverlayItem extends OverlayItem {

	Place place;
	Drawable marker;
	Drawable markerSel;
	Drawable downBelow;

	PlacesItemizedOverlay overlay;

	public PlacesOverlayItem(Place p, String title, String snippet,
			Drawable marker, Drawable markerSel, Drawable downBelow,
			PlacesItemizedOverlay overlay) {
		super(p.getPoint(), title, snippet);
		this.place = p;

		downBelow.setBounds(0, 0, downBelow.getIntrinsicWidth(),
				downBelow.getIntrinsicHeight());

		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());
		PlacesItemizedOverlay.boundCenterBottom(marker);
		markerSel.setBounds(0, 0, markerSel.getIntrinsicWidth(),
				markerSel.getIntrinsicHeight());
		PlacesItemizedOverlay.boundCenterBottom(markerSel);
		this.marker = marker;
		this.markerSel = markerSel;
		this.downBelow = downBelow;
		this.overlay = overlay;
	}

	public Place getPlace() {
		return place;
	}

	@Override
	public Drawable getMarker(int stateBitset) {
		if (place.getFloor() == overlay.getFloor()) {
			if(((stateBitset & OverlayItem.ITEM_STATE_SELECTED_MASK) == OverlayItem.ITEM_STATE_SELECTED_MASK)
					|| (stateBitset & OverlayItem.ITEM_STATE_PRESSED_MASK) == OverlayItem.ITEM_STATE_PRESSED_MASK) {
				return this.markerSel;
			}
			OverlayItem item = overlay.getFocus();
			if (item != null) {
				PlacesOverlayItem pItem = (PlacesOverlayItem) item;
				if (pItem.getPlace().getName()
						.equals(place.getName())) {
					return this.markerSel;
				}
			}
			return marker;
		} else { // plage.getFloor() << overlay.getFloor() or some other case
					// that shouldn't happen.
			return downBelow;
		}
	}

}
