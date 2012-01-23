package edu.mit.pt.data;

import android.os.Parcel;
import edu.mit.pt.R;

public class Athena extends Place {
	public Athena(long id, String name, int latE6, int lonE6) {
		super(id, name, latE6, lonE6);
	}

	public Athena(Parcel in) {
		super(in);
	}

	@Override
	public PlaceType getPlaceType() {
		return PlaceType.CLUSTER;
	}
	//TODO: Custom icon for athena
	@Override
	public int getMarkerId() {
		return R.drawable.green_point;
	}

}
