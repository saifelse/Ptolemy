package edu.mit.pt.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
	public Drawable getMarker(Context context) {
		return context.getResources().getDrawable(R.drawable.green_point);
	}

}
