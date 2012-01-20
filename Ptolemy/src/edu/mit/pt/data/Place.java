package edu.mit.pt.data;

import android.content.Context;

public class Place {
	int latE6;
	int lonE6;
	String name;
	
	public Place(String name, int latE6, int lonE6) {
		this.name = name;
		this.latE6 = latE6;
		this.lonE6 = lonE6;
	}
	public int getLatE6() {
		return latE6;
	}
	public int getLonE6() {
		return lonE6;
	}
	public String getName() {
		return name;
	}
	
	public static Place getPlace(Context context, int id) {
		// TODO: implement this once OpenHelpers are fixed - fields need to be public.
		return new Place("10-250", 42361113, -71092261);
	}
}
