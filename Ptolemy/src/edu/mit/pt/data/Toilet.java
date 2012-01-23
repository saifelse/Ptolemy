package edu.mit.pt.data;

import android.os.Parcel;
import edu.mit.pt.R;

public class Toilet extends Place {

	public Toilet(int id, String name, int latE6, int lonE6) {
		super(id, name, latE6, lonE6);
	}

	public Toilet(Parcel in) {
		super(in);
	}

	@Override
	public PlaceType getPlaceType() {
		return PlaceType.TOILET;
	}

	@Override
	public int getMarkerId() {
		return R.drawable.blue_point;
	}

}
