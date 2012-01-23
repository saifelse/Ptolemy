package edu.mit.pt.maps;

import com.google.android.maps.OverlayItem;

import edu.mit.pt.data.Place;

public class PlacesOverlayItem extends OverlayItem {
	
	Place place;

	public PlacesOverlayItem(Place p, String title, String snippet) {
		super(p.getPoint(), title, snippet);
		this.place = p;
	}
	
	public Place getPlace() {
		return place;
	}

}
