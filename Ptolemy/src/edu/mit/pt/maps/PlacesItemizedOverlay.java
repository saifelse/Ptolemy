package edu.mit.pt.maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import edu.mit.pt.data.Place;

public class PlacesItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	
	OnTapListener tapListener = null;
	
	private List<PlacesOverlayItem> pOverlayItems = Collections
			.synchronizedList(new ArrayList<PlacesOverlayItem>());

	public PlacesItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		// This needs to be here to fix bug with index out of bounds exception.
		populate();
	}

	public void addOverlayItem(PlacesOverlayItem overlayItem) {
		pOverlayItems.add(overlayItem);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		// System.out.println("Size: " + size());
		// System.out.println("i: " + i);
		return pOverlayItems.get(i);
	}

	@Override
	public int size() {
		return pOverlayItems.size();
	}
	
	public void setOnTapListener(OnTapListener listener) {
		this.tapListener = listener;
	}
	
	@Override
	public boolean onTap(int index) {
		if (tapListener != null) {
			Place p = pOverlayItems.get(index).getPlace();
			tapListener.onTap(p);
		}
		return true;
	}

}
