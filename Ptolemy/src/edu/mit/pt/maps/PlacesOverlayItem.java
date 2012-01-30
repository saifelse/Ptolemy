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

		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());
		markerSel.setBounds(0, 0, markerSel.getIntrinsicWidth(),
				markerSel.getIntrinsicHeight());
		downBelow.setBounds(0, 0, downBelow.getIntrinsicWidth(),
				downBelow.getIntrinsicHeight());

		switch (this.place.getPlaceType()) {
		case CLASSROOM:
			PlacesItemizedOverlay.boundCenter(marker);
			PlacesItemizedOverlay.boundCenter(markerSel);
			PlacesItemizedOverlay.boundCenter(downBelow);
			break;
		case ATHENA:
		case FOUNTAIN:
		case MTOILET:
		case FTOILET:
			PlacesItemizedOverlay.boundCenterBottom(marker);
			PlacesItemizedOverlay.boundCenterBottom(markerSel);
			PlacesItemizedOverlay.boundCenterBottom(downBelow);
		}

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
		//Attempting to use direct field access for speed instead of getters/setters
		if (place.floor == overlay.floor) {
			// if (((stateBitset & OverlayItem.ITEM_STATE_SELECTED_MASK) ==
			// OverlayItem.ITEM_STATE_SELECTED_MASK)
			// || (stateBitset & OverlayItem.ITEM_STATE_PRESSED_MASK) ==
			// OverlayItem.ITEM_STATE_PRESSED_MASK) {
			// return this.markerSel;
			// }
			String title = getTitle();
			if (title != null && title.equals(overlay.getFocusedTitle())) {
				return this.markerSel;
			}
			return marker;
		} else { // plage.getFloor() << overlay.getFloor() or some other case
					// that shouldn't happen.
			return downBelow;
		}
	}

}
