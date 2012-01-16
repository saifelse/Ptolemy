package edu.mit.pt.maps;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import edu.mit.pt.Config;

public class PtolemyMapView extends MapView {

	Context ctx;
	private final int MIN_ZOOM_LEVEL = 20;
	private final int MAX_ZOOM_LEVEL = 21;

	public PtolemyMapView(Context context, String key) {
		super(context, key);
		ctx = context;
		setup();
	}

	public PtolemyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		ctx = context;
		setup();
	}

	public PtolemyMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		ctx = context;
		setup();
	}

	private void setup() {
		List<Overlay> overlays = getOverlays();
		overlays.add(new TileOverlay());
	}

	class TileOverlay extends Overlay {

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {

			super.draw(canvas, mapView, shadow);
			// draw() is called twice, once with shadow=true for shadow layer
			// and once with shadow=false for the real layer.
			if (shadow) {
				return;
			}
			int zoomLevel = mapView.getZoomLevel();
			if (zoomLevel < MIN_ZOOM_LEVEL || zoomLevel > MAX_ZOOM_LEVEL) {
				return;
			}
			Log.v(Config.TAG,
					"Drawing! MapZoomLevel is " + mapView.getZoomLevel());

		}
	}

	static double computeGoogleX(int longitudeE6, int zoomLevel) {
		return (180. + ((double) longitudeE6 / 1000000.)) / (360.)
				* Math.pow(2.0, zoomLevel);
	}

	static double computeGoogleY(int latitudeE6, int zoomLevel) {
		// Convert to radians.
		double phi = (double) latitudeE6 / 1000000. * Math.PI / 180.;

		// Calculate Mercator coordinate.
		double mercatorY = Math.log(Math.tan(phi) + 1. / Math.cos(phi));

		// Rescale to Google coordinate.
		return (Math.PI - mercatorY) / (2. * Math.PI)
				* Math.pow(2.0, zoomLevel);
	}

}
