package edu.mit.pt.maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class PlacesItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private List<OverlayItem> pOverlays = Collections
			.synchronizedList(new ArrayList<OverlayItem>());

	public PlacesItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		// This needs to be here to fix bug with index out of bounds exception.
		populate();
	}

	public void addOverlay(OverlayItem overlay) {
		pOverlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		// System.out.println("Size: " + size());
		// System.out.println("i: " + i);
		return pOverlays.get(i);
	}

	@Override
	public int size() {
		return pOverlays.size();
	}

}