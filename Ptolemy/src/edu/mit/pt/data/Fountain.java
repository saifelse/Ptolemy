package edu.mit.pt.data;

import android.os.Parcel;
import edu.mit.pt.R;

public class Fountain extends Place {
	public Fountain(long id, String name, int latE6, int lonE6, int floor) {
		super(id, name, latE6, lonE6, floor);
	}

	public Fountain(Parcel in) {
		super(in);
	}

	@Override
	public PlaceType getPlaceType() {
		return PlaceType.FOUNTAIN;
	}

	// TODO: Custom icon for fountain
	@Override
	public int getMarkerId() {
		return R.drawable.green_point;
	}

	@Override
	public int getMarkerSelId() {
		return R.drawable.green_point;
	}

}
