package edu.mit.pt.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import edu.mit.pt.R;

public class Toilet extends Place {
	private GenderEnum gender;
	public Toilet(int id, String name, int latE6, int lonE6, GenderEnum gender) {
		super(id, name, latE6, lonE6);
		this.gender = gender;
	}

	public Toilet(Parcel in) {
		super(in);
		gender = GenderEnum.valueOf(in.readString());
	}
	@Override
	public String getName(){
		String prefix;
		switch(gender){
		case MALE:
			prefix = "Men's "; break;
		case FEMALE:
			prefix = "Women's "; break;
		case BOTH: default:
			prefix = "";
		}
		return prefix+"Bathroom ("+name+")";
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(this.gender.name());
	}
	@Override
	public PlaceType getPlaceType() {
		return PlaceType.TOILET;
	}

	// TODO: Make different icons for different gender types
	@Override
	public Drawable getMarker(Context context) {
		int resourceId;
		switch(gender){
		case MALE:
			resourceId = R.drawable.green_point; break;
		case FEMALE:
			resourceId = R.drawable.green_point; break;
		case BOTH: default:
			resourceId = R.drawable.green_point; break;
		}
		return context.getResources().getDrawable(resourceId);
	}
}