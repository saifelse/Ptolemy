package edu.mit.pt.data;

import android.os.Parcel;
import edu.mit.pt.R;

public class MaleToilet extends Place {

	public MaleToilet(long id, String name, int latE6, int lonE6, int floor) {
		super(id, name, latE6, lonE6, floor);
	}
	
	public MaleToilet(Parcel in) {
		super(in);
	}

	@Override
	public String getName() {
		return "Men's Bathroom (" + name + ")";
	}

	// TODO: Make different icons for different gender types
	@Override
	public int getMarkerId() {
		return R.drawable.icon_br_male;
	}
	
	@Override
	public int getMarkerSelId() {
		return R.drawable.icon_br_male_sel;
	}


	@Override
	public PlaceType getPlaceType() {
		return PlaceType.MTOILET;
	}

}
