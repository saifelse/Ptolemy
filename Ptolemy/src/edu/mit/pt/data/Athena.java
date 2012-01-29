package edu.mit.pt.data;

import android.os.Parcel;
import edu.mit.pt.R;

public class Athena extends Place {
	public Athena(long id, String name, int latE6, int lonE6, int floor) {
		super(id, name, latE6, lonE6, floor);
	}

	public Athena(Parcel in) {
		super(in);
	}

	@Override
	public PlaceType getPlaceType() {
		return PlaceType.ATHENA;
	}

	// TODO: Custom icon for athena
	@Override
	public int getMarkerId() {
		return R.drawable.icon_athena;
	}

	@Override
	public int getMarkerSelId() {
		return R.drawable.icon_athena_sel;
	}

	@Override
	public int getMarkerDownBelowId() {
		return R.drawable.icon_athena_down_below;
	}

}
