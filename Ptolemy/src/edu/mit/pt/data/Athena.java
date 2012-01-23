package edu.mit.pt.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import edu.mit.pt.R;

public class Athena extends Place {
	public Athena(int id, String name, int latE6, int lonE6) {
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
	public Drawable getMarker(Context context) {
		return context.getResources().getDrawable(R.drawable.green_point);
	}

}
