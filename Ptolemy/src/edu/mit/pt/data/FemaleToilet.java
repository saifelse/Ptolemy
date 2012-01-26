package edu.mit.pt.data;

import android.os.Parcel;
import edu.mit.pt.R;

public class FemaleToilet extends Place {
	public FemaleToilet(long id, String name, int latE6, int lonE6, int floor) {
		super(id, name, latE6, lonE6, floor);
	}
	
	public FemaleToilet(Parcel in) {
		super(in);
	}

	@Override
	public String getName() {
		return "Women's Bathroom (" + name + ")";
	}

	// TODO: Make different icons for different gender types
	@Override
	public int getMarkerId() {
		return R.drawable.icon_br_female;
	}
	
	@Override
	public int getMarkerSelId() {
		return R.drawable.icon_br_female_sel;
	}

	@Override
	public PlaceType getPlaceType() {
		return PlaceType.FTOILET;
	}
}
