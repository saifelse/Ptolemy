package edu.mit.pt.maps;

import com.google.android.maps.MapView;

public class GoogleTileCalculator {
	private GoogleTileCalculator(){
	}
	static public int computeTileSize(MapView mapView, int zoomLevel) {
		return 512;
	}

	/*
	 * private static int computeLongitudeE6(double googleX, int zoomLevel) {
	 * double longitude = -180. + (360. * googleX) / Math.pow(2.0, zoomLevel);
	 * return (int) Math.round(longitude * 1000000.); }
	 * 
	 * private static int computeLatitudeE6(double googleY, int zoomLevel) {
	 * double mercatorY = Math.PI (1 - 2 * (googleY / Math.pow(2.0,
	 * zoomLevel))); double phi = Math.atan(Math.sinh(mercatorY));
	 * 
	 * // Convert from radians to microdegrees. return (int) Math.round(phi *
	 * 180. / Math.PI * 1000000.); }
	 */
	static public double computeGoogleX(int longitudeE6, int zoomLevel) {
		double mappedLng = 180. + ((double) longitudeE6 / 1000000.);
		double longTileSize = 360 / Math.pow(2.0, zoomLevel);
		return mappedLng / longTileSize;
	}

	static public double computeGoogleY(int latitudeE6, int zoomLevel) {
		// Convert to radians.
		double phi = (double) latitudeE6 / 1000000. * Math.PI / 180.;
		// Calculate Mercator coordinate.
		double mercatorY = Math.log(Math.tan(phi) + 1. / Math.cos(phi));
		// Rescale to Google coordinate.
		return (Math.PI - mercatorY) / (2. * Math.PI)
				* Math.pow(2.0, zoomLevel);
	}
}
