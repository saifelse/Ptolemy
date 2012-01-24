package edu.mit.pt.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.maps.GeoPoint;

import edu.mit.pt.Config;

abstract public class Place implements Parcelable {
	long id;
	int latE6;
	int lonE6;
	String name;
	int floor;

	public Place(long id, String name, int latE6, int lonE6, int floor) {
		this.id = id;
		this.name = name;
		this.latE6 = latE6;
		this.lonE6 = lonE6;
		this.floor = floor;
	}

	// TODO: is this necessary?
	public long getId() {
		return id;
	}

	public int getLatE6() {
		return latE6;
	}

	public int getLonE6() {
		return lonE6;
	}

	public int getFloor() {
		return floor;
	}

	public GeoPoint getPoint() {
		return new GeoPoint(latE6, lonE6);
	}

	public String getName() {
		return name;
	}

	abstract public PlaceType getPlaceType();

	public Drawable getMarker(Resources resources) {
		return resources.getDrawable(getMarkerId());
	}

	abstract public int getMarkerId();

	public static Place getPlace(Context context, long id) {
		SQLiteDatabase db = PtolemyDBOpenHelperSingleton
				.getPtolemyDBOpenHelper(context).getReadableDatabase();

		Cursor c = db.query(PlacesTable.PLACES_TABLE_NAME, new String[] {
				PlacesTable.COLUMN_ID, PlacesTable.COLUMN_NAME,
				PlacesTable.COLUMN_LAT, PlacesTable.COLUMN_LON,
				PlacesTable.COLUMN_TYPE, PlacesTable.COLUMN_FLOOR },
				PlacesTable.COLUMN_ID + "=?",
				new String[] { Long.toString(id) }, null, null, null);
		if (c.getCount() == 0) {
			c.close();
			// db.close();
			return null;
		}
		c.moveToFirst();
		String name = c.getString(c.getColumnIndex(PlacesTable.COLUMN_NAME));
		int latE6 = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_LAT));
		int lonE6 = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_LON));
		int floor = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_FLOOR));
		String typeName = c
				.getString(c.getColumnIndex(PlacesTable.COLUMN_TYPE));
		PlaceType type = PlaceType.valueOf(typeName);
		c.close();
		Place output = null;
		switch (type) {
		case CLASSROOM:
			output = new Classroom(id, name, latE6, lonE6, floor);
		case CLUSTER:
			output = new Athena(id, name, latE6, lonE6, floor);
		case FOUNTAIN:
			output = new Fountain(id, name, latE6, lonE6, floor);
		case TOILET:
			output = new Toilet(id, name, latE6, lonE6, floor,
					getGender(db, id));
		}
		// db.close();
		return output;
	}

	public static Place getClassroom(Context context, String room) {
		SQLiteDatabase db = PtolemyDBOpenHelperSingleton
				.getPtolemyDBOpenHelper(context).getWritableDatabase();
		Cursor c = db.query(PlacesTable.PLACES_TABLE_NAME, new String[] {
				PlacesTable.COLUMN_ID, PlacesTable.COLUMN_NAME,
				PlacesTable.COLUMN_LAT, PlacesTable.COLUMN_LON,
				PlacesTable.COLUMN_TYPE, PlacesTable.COLUMN_FLOOR },
				PlacesTable.COLUMN_NAME + "=?", new String[] { room }, null,
				null, null);
		if (c.getCount() == 0) {
			c.close();
			// db.close();
			return null;
		}
		c.moveToFirst();
		long id = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_ID));
		String name = c.getString(c.getColumnIndex(PlacesTable.COLUMN_NAME));
		int latE6 = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_LAT));
		int lonE6 = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_LON));
		int floor = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_FLOOR));
		String typeName = c
				.getString(c.getColumnIndex(PlacesTable.COLUMN_TYPE));
		PlaceType type = PlaceType.valueOf(typeName);
		c.close();
		// db.close();
		// This only searches classrooms.
		if (type != PlaceType.CLASSROOM) {
			return null;
		}
		return new Classroom(id, name, latE6, lonE6, floor);
	}

	public static Place addPlace(Context context, String name, int latE6,
			int lonE6, int floor, PlaceType type) {
		return addPlaceHelper(context, name, latE6, lonE6, floor, type, null);
	}

	public static Place addBathroom(Context context, String name, int latE6,
			int lonE6, int floor, PlaceType type, GenderEnum gender) {
		return addPlaceHelper(context, name, latE6, lonE6, floor, type, gender);
	}

	private static Place addPlaceHelper(Context context, String name,
			int latE6, int lonE6, int floor, PlaceType type, GenderEnum gender) {
		SQLiteDatabase db = PtolemyDBOpenHelperSingleton
				.getPtolemyDBOpenHelper(context).getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(PlacesTable.COLUMN_NAME, name);
		values.put(PlacesTable.COLUMN_LAT, latE6);
		values.put(PlacesTable.COLUMN_LON, lonE6);
		values.put(PlacesTable.COLUMN_TYPE, type.name());
		values.put(PlacesTable.COLUMN_FLOOR, floor);
		long id = db.insert(PlacesTable.PLACES_TABLE_NAME, null, values);
		// db.close();
		if (id == -1) {
			return null;
		}
		switch (type) {
		case CLASSROOM:
			return new Classroom(id, name, latE6, lonE6, floor);
		case CLUSTER:
			return new Athena(id, name, latE6, lonE6, floor);
		case FOUNTAIN:
			return new Fountain(id, name, latE6, lonE6, floor);
		case TOILET:
			return new Toilet(id, name, latE6, lonE6, floor, gender);
		default:
			return null;
		}
	}

	public static List<Place> getPlacesExceptClassrooms(Context context) {
		SQLiteDatabase db = PtolemyDBOpenHelperSingleton
				.getPtolemyDBOpenHelper(context).getReadableDatabase();
		Cursor c = db.query(PlacesTable.PLACES_TABLE_NAME, new String[] {
				PlacesTable.COLUMN_ID, PlacesTable.COLUMN_NAME,
				PlacesTable.COLUMN_LAT, PlacesTable.COLUMN_LON,
				PlacesTable.COLUMN_TYPE, PlacesTable.COLUMN_FLOOR }, null,
				null, null, null, null);
		List<Place> places = new ArrayList<Place>();
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			long id = c.getLong(c.getColumnIndex(PlacesTable.COLUMN_ID));
			String name = c
					.getString(c.getColumnIndex(PlacesTable.COLUMN_NAME));
			int latE6 = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_LAT));
			int lonE6 = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_LON));
			int floor = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_FLOOR));
			String typeName = c.getString(c
					.getColumnIndex(PlacesTable.COLUMN_TYPE));

			PlaceType type = PlaceType.valueOf(typeName);
			Place p;
			switch (type) {
			case TOILET:
				p = new Toilet(id, name, latE6, lonE6, floor, getGender(db, id));
				break;
			case FOUNTAIN:
				p = new Fountain(id, name, latE6, lonE6, floor);
			case CLUSTER:
				p = new Athena(id, name, latE6, lonE6, floor);
			default:
				continue;
			}
			places.add(p);
		}
		// db.close();
		return places;
	}

	static private GenderEnum getGender(SQLiteDatabase db, long toiletId) {
		GenderEnum gender;
		Cursor tc = db.query(ToiletMetaTable.TOILET_TABLE_NAME,
				new String[] { ToiletMetaTable.COLUMN_TYPE }, "PLACE_ID=?",
				new String[] { Long.toString(toiletId) }, null, null, null);
		if (tc.getCount() == 1) {
			tc.moveToFirst();
			gender = GenderEnum.valueOf(tc.getString(tc
					.getColumnIndex(ToiletMetaTable.COLUMN_TYPE)));
		} else {
			Log.wtf(Config.TAG, tc.getCount() + " entries found for Toilet id "
					+ toiletId + ". Expected 1 entry. Defaulting to BOTH.");
			gender = GenderEnum.BOTH;
		}
		tc.close();
		return gender;
	}

	public int describeContents() {
		return 0;
	}

	protected Place(Parcel in) {
		id = in.readLong();
		Log.v(Config.TAG, "ID IS " + id);
		latE6 = in.readInt();
		lonE6 = in.readInt();
		name = in.readString();
		Log.v(Config.TAG, "PLACE NAME IS " + name);
	}

	public void writeToParcel(Parcel dest, int flags) {
		Log.v(Config.TAG, "PLACE WRITETO");
		dest.writeString(getPlaceType().name());
		dest.writeLong(id);
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
			case TOILET:
				return new Toilet(in);
			case CLUSTER:
				return new Athena(in);
			case FOUNTAIN:
				return new Fountain(in);
			default:
				return new Classroom(in);
			}
		}

		public Place[] newArray(int size) {
			return new Place[size];
		}
	};
}
