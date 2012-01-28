package edu.mit.pt.location;

import com.google.android.maps.GeoPoint;

public class APGeoPoint extends GeoPoint {
	private int floor;
	
	public APGeoPoint(int latitudeE6, int longitudeE6, int floor) {
		super(latitudeE6, longitudeE6);
		this.floor = floor;
	}
	
	public int getFloor() {
		return floor;
	}

}
