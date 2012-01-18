package edu.mit.pt.data;

public class Place {
	long latE6;
	long lonE6;
	String name;
	
	public Place(String name, long lat, long lon) {
		this.name = name;
		this.latE6 = lat;
		this.lonE6 = lon;
	}
	public long getLatE6() {
		return latE6;
	}
	public long getLonE6() {
		return lonE6;
	}
	public String getName() {
		return name;
	}
}
