package edu.mit.pt.maps;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.OverlayItem;

import edu.mit.pt.data.Place;

public class PlacesOverlayItem extends OverlayItem {
	
	Place place;
	Context context;

	public PlacesOverlayItem(Context context, Place p, String title, String snippet) {
		super(p.getPoint(), title, snippet);
		this.place = p;
		this.context = context;
	}
	
	public Place getPlace() {
		return place;
	}
	
	@Override
	public Drawable getMarker(int stateBitset) {
		return place.getMarker(context);
	}

}
