package edu.mit.pt.maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class PlacesItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private int floor;
	private List<PlacesOverlayItem> overlayItems = Collections
			.synchronizedList(new ArrayList<PlacesOverlayItem>());

	private String focusedTitle;

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

	@Override
	protected OverlayItem createItem(int i) {
		return getOverlayItem(i);
	}

	private PlacesOverlayItem getOverlayItem(int i) {
		return overlayItems.get(i);
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
		return overlayItems.size();
	}

	// public void setOnTapListener(OnTapListener listener) {
	// this.tapListener = listener;
	// }
	//
	// @Override
	// public boolean onTap(int index) {
	// Log.v(Config.TAG, "TAPPED");
	// if (tapListener != null) {
	// Place p = getOverlayItem(index).getPlace();
	// tapListener.onTap(p);
	// }
	// return true;
	// }

	public void clear() {
		overlayItems.clear();
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
	
	public void setFocusedTitle(String title) {
		focusedTitle = title;
	}
	
	public String getFocusedTitle() {
		return focusedTitle;
	}
	public void setFloor(int f){
		floor = f;
	}
	public int getFloor() {
		return floor;
	}

}
