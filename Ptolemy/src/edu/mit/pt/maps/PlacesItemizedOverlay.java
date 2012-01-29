package edu.mit.pt.maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import edu.mit.pt.Config;
import edu.mit.pt.data.Place;

public class PlacesItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private int floor;
	private List<PlacesOverlayItem> overlayItems = Collections
			.synchronizedList(new ArrayList<PlacesOverlayItem>());

	private Map<String, PlacesOverlayItem> overlayItemMap = new HashMap<String, PlacesOverlayItem>();

	public PlacesItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		// This needs to be here to fix bug with index out of bounds exception.
		// http://code.google.com/p/android/issues/detail?id=2035
		populate();
	}

	public void addOverlayItem(PlacesOverlayItem overlayItem) {
		overlayItems.add(overlayItem);
		overlayItemMap.put(overlayItem.getPlace().getName(), overlayItem);
		update();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return getOverlayItem(i);
	}

	private PlacesOverlayItem getOverlayItem(int i) {
		return overlayItems.get(i);
	}

	public void setFocusByPlace(Place p) {
		String key = p.getName();
		if (overlayItemMap.containsKey(key)) {
			Log.v(Config.TAG, "TRYING TO FOCUS ON " + p.getName());
			setFocus(overlayItemMap.get(key));
		} else {
			for (String name : overlayItemMap.keySet()) {
				Log.v(Config.TAG, p.getName() + ": NOPE ITS NOT: " + name);
			}
		}
	}

	/**
	 * Call this method after any changes are made, to avoid bug:
	 * http://groups.google
	 * .com/group/android-developers/browse_thread/thread/38b11314e34714c3
	 */
	public void update() {
		setLastFocusedIndex(-1);
		populate();
	}

	@Override
	public int size() {
		return overlayItems.size();
	}

	public void clear() {
		overlayItems.clear();
		overlayItemMap.clear();
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

	public void setFloor(int f) {
		floor = f;
	}

	public int getFloor() {
		return floor;
	}

}
