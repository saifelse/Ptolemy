package edu.mit.pt.data;

import android.os.Parcel;
import android.util.Log;
import edu.mit.pt.Config;
import edu.mit.pt.R;

public class Toilet extends Place {
	private GenderEnum gender;
	public Toilet(long id, String name, int latE6, int lonE6, GenderEnum gender) {
		super(id, name, latE6, lonE6);
		this.gender = gender;
	}

	public Toilet(Parcel in) {
		super(in);
		Log.v(Config.TAG, "TOILET CONSTRUCTOR");
		gender = GenderEnum.valueOf(in.readString());
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		Log.v(Config.TAG, "TOILET WRITETOO");
		dest.writeString(this.gender.name());
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
	public PlaceType getPlaceType() {
		return PlaceType.TOILET;
	}

	// TODO: Make different icons for different gender types
	@Override
	public int getMarkerId() {
		int resourceId;
		switch(gender){
		case MALE:
			resourceId = R.drawable.blue_point;
			break;
		case FEMALE:
			resourceId = R.drawable.blue_point;
			break;
		case BOTH: default:
			resourceId = R.drawable.blue_point;
			break;
		}
		return resourceId;
	}
}
