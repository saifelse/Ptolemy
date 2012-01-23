package edu.mit.pt.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import edu.mit.pt.R;

public class Fountain extends Place {
	public Fountain(int id, String name, int latE6, int lonE6) {
		super(id, name, latE6, lonE6);
	}

	public Fountain(Parcel in) {
		super(in);
	}

	@Override
	public PlaceType getPlaceType() {
		return PlaceType.FOUNTAIN;
	}
	//TODO: Custom icon for fountain
	@Override
	public Drawable getMarker(Context context) {
		return context.getResources().getDrawable(R.drawable.green_point);
	}

}
