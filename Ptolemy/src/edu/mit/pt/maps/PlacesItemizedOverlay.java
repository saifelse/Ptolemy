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
	
	private List<PlacesOverlayItem> overlayItems = Collections
			.synchronizedList(new ArrayList<PlacesOverlayItem>());

	private List<PlacesOverlayItem> extraOverlayItems = Collections
			.synchronizedList(new ArrayList<PlacesOverlayItem>());

	public PlacesItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		// This needs to be here to fix bug with index out of bounds exception.
		populate();
	}

	public void addOverlayItem(PlacesOverlayItem overlayItem) {
		overlayItems.add(overlayItem);
		populate();
	}
	
	public void setExtras(List<PlacesOverlayItem> items) {
		extraOverlayItems.clear();
		extraOverlayItems.addAll(items);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return getOverlayItem(i);
	}
	
	private PlacesOverlayItem getOverlayItem(int i) {
		int overlayItemsSize = overlayItems.size();
		if (i < overlayItemsSize) {
			return overlayItems.get(i);
		} else {
			return extraOverlayItems.get(i - overlayItemsSize);
		}
	}

	@Override
	public int size() {
		return overlayItems.size() + extraOverlayItems.size();
	}

	public void setOnTapListener(OnTapListener listener) {
		this.tapListener = listener;
	}

	@Override
	public boolean onTap(int index) {
		if (tapListener != null) {
			Place p = getOverlayItem(index).getPlace();
			tapListener.onTap(p);
		}
		return true;
	}
	
	public void clear(){
		overlayItems.clear();
		extraOverlayItems.clear();
	}
	static public Drawable boundCenterBottom(Drawable drawable) {
		return ItemizedOverlay.boundCenterBottom(drawable);
	}
	
	static public Drawable boundCenter(Drawable drawable) {
		return ItemizedOverlay.boundCenter(drawable);
	}

}
