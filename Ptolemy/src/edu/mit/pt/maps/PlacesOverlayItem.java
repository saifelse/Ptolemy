package edu.mit.pt.maps;

import android.graphics.drawable.Drawable;

import com.google.android.maps.OverlayItem;

import edu.mit.pt.data.Place;

public class PlacesOverlayItem extends OverlayItem {
	
	Place place;
	Drawable marker;

	public PlacesOverlayItem(Place p, String title, String snippet, Drawable marker) {
		super(p.getPoint(), title, snippet);
		this.place = p;
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		PlacesItemizedOverlay.boundCenter(marker);
		this.marker = marker;
	}
	
	public Place getPlace() {
		return place;
	}
	
	@Override
	public Drawable getMarker(int stateBitset) {
		return marker;
	}

}
