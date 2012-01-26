package edu.mit.pt.maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import edu.mit.pt.Config;
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
		// http://code.google.com/p/android/issues/detail?id=2035
		populate();
	}

	public void addOverlayItem(PlacesOverlayItem overlayItem) {
		overlayItems.add(overlayItem);
		update();
	}

	public void setExtras(List<PlacesOverlayItem> items) {
		extraOverlayItems.clear();
		extraOverlayItems.addAll(items);
		update();
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

	/**
	 * Call this method after any changes are made, to avoid bug:
	 * http://groups.google
	 * .com/group/android-developers/browse_thread/thread/38b11314e34714c3
	 */
	private void update() {
		setLastFocusedIndex(-1);
		populate();
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
		Log.v(Config.TAG, "TAPPED");
		if (tapListener != null) {
			Place p = getOverlayItem(index).getPlace();
			tapListener.onTap(p);
		}
		return true;
	}

	public void clear() {
		overlayItems.clear();
		extraOverlayItems.clear();
		update();
	}

	static public Drawable boundCenterBottom(Drawable drawable) {
		return ItemizedOverlay.boundCenterBottom(drawable);
	}

	static public Drawable boundCenter(Drawable drawable) {
		return ItemizedOverlay.boundCenter(drawable);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// Disable the shadow on markers.
		if (!shadow) {
			super.draw(canvas, mapView, false);
		}
	}

}
