package edu.mit.pt.data;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import edu.mit.pt.maps.PlacesOverlayItem;

abstract public class Place implements Parcelable {
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

	public int getId() {
		return id;
	}

	public int getLatE6() {
		return latE6;
	}

	public int getLonE6() {
		return lonE6;
	}
	
	public GeoPoint getPoint() {
		return new GeoPoint(latE6, lonE6);
	}

	public String getName() {
		return name;
	}

	abstract public PlaceType getPlaceType();
	
	public PlacesOverlayItem getOverlayItem() {
		return new PlacesOverlayItem(this, name, name);
	}
	
	abstract public Drawable getMarker(Context context);

	public static Place getPlace(Context context, int id) {
		// TODO: implement this.
		return new Classroom(id, "10-250", 42361113, -71092261);
	}

	public static Place getPlace(Context context, String room) {
		// TODO: implement this.
		return new Classroom(1, "10-250", 42361113, -71092261);
	}

	public static void addPlace(Context context, String name, int latE6, int lonE6, PlaceType type) {
		SQLiteDatabase db = new PtolemyOpenHelper(context)
				.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(PlacesTable.COLUMN_NAME, name);
		values.put(PlacesTable.COLUMN_LAT, latE6);
		values.put(PlacesTable.COLUMN_LON, lonE6);
		values.put(PlacesTable.COLUMN_TYPE, type.name());
		db.insert(PlacesTable.PLACES_TABLE_NAME, null, values);
		db.close();
	}

	// TODO: modify this to not show classrooms (and change method name)
	public static List<Place> getPlaces(Context context) {
		SQLiteDatabase db = new PtolemyOpenHelper(context)
				.getReadableDatabase();
		Cursor c = db.query(PlacesTable.PLACES_TABLE_NAME, new String[] {
				PlacesTable.COLUMN_ID, PlacesTable.COLUMN_NAME,
				PlacesTable.COLUMN_LAT, PlacesTable.COLUMN_LON,
				PlacesTable.COLUMN_TYPE }, null, null, null, null, null);
		List<Place> places = new ArrayList<Place>();
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			int id = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_ID));
			String name = c
					.getString(c.getColumnIndex(PlacesTable.COLUMN_NAME));
			int latE6 = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_LAT));
			int lonE6 = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_LON));
			String typeName = c.getString(c
					.getColumnIndex(PlacesTable.COLUMN_TYPE));

			PlaceType type = PlaceType.valueOf(typeName);
			Place p;
			switch (type) {
			case CLASSROOM:
				p = new Classroom(id, name, latE6, lonE6);
				break;
			default:
				continue;
			}
			places.add(p);
		}
		db.close();
		return places;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getPlaceType().name());
		dest.writeInt(id);
		dest.writeInt(latE6);
		dest.writeInt(lonE6);
		dest.writeString(name);
	}

	/**
	 * CREATOR is required for Parcelable, so we need to do some thinking first
	 * to return the right child of Place.
	 */
	public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
		public Place createFromParcel(Parcel in) {
			PlaceType type = PlaceType.valueOf(in.readString());
			switch (type) {
			case CLASSROOM:
				return new Classroom(in);
				// TODO implement it for other abstract classes.
			default:
				return new Classroom(in);
			}
		}

		public Place[] newArray(int size) {
			return new Place[size];
		}
	};

	protected Place(Parcel in) {
		id = in.readInt();
		latE6 = in.readInt();
		lonE6 = in.readInt();
		name = in.readString();
	}
}
