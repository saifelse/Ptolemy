package edu.mit.pt.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
	int id;
	int latE6;
	int lonE6;
	String name;

	public Place(int id, String name, int latE6, int lonE6) {
		this.id = id;
		this.name = name;
		this.latE6 = latE6;
		this.lonE6 = lonE6;
	}

	public Place(String name, int latE6, int lonE6) {
		// TODO write this constructor to save to db.
	}

	public int getId() {
		return id;
	}

	public int getLatE6() {
		return latE6;
	}

	public int getLonE6() {
		return lonE6;
	}

	public String getName() {
		return name;
	}

	public static Place getPlace(Context context, int id) {
		// TODO: implement this.
		return new Place(id, "10-250", 42361113, -71092261);
	}

	public static Place getPlace(Context context, String room) {
		// TODO: implement this.
		return new Place(1, "10-250", 42361113, -71092261);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(latE6);
		dest.writeInt(lonE6);
		dest.writeString(name);
	}

	public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
		public Place createFromParcel(Parcel in) {
			return new Place(in);
		}

		public Place[] newArray(int size) {
			return new Place[size];
		}
	};

	private Place(Parcel in) {
		id = in.readInt();
		latE6 = in.readInt();
		lonE6 = in.readInt();
		name = in.readString();
	}
}
