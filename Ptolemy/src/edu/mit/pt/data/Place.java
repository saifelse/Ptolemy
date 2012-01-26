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
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

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

	public Drawable getMarker(Resources resources, boolean isSel) {
		if (isSel) {
			return resources.getDrawable(getMarkerSelId());
		} else {
			return resources.getDrawable(getMarkerId());
		}
	}

	abstract public int getMarkerId();
	abstract public int getMarkerSelId();

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
			return null;
		}
		c.moveToFirst();
		String name = c.getString(c.getColumnIndex(PlacesTable.COLUMN_NAME));
		int latE6 = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_LAT));
		int lonE6 = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_LON));
		int floor = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_FLOOR));
		String typeName = c
				.getString(c.getColumnIndex(PlacesTable.COLUMN_TYPE));
		Log.v(Config.TAG, "TYPENAME IS " + typeName);
		PlaceType type = PlaceType.valueOf(typeName);
		c.close();
		switch (type) {
		case CLASSROOM:
			return new Classroom(id, name, latE6, lonE6, floor);
		case ATHENA:
			return new Athena(id, name, latE6, lonE6, floor);
		case FOUNTAIN:
			return new Fountain(id, name, latE6, lonE6, floor);
		case MTOILET:
			return new MaleToilet(id, name, latE6, lonE6, floor);
		case FTOILET:
			return new MaleToilet(id, name, latE6, lonE6, floor);
		default:
			return null;
		}
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
		Log.v(Config.TAG, "MAKING: " + type.name());
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
		case ATHENA:
			return new Athena(id, name, latE6, lonE6, floor);
		case FOUNTAIN:
			return new Fountain(id, name, latE6, lonE6, floor);
		case MTOILET:
			return new MaleToilet(id, name, latE6, lonE6, floor);
		case FTOILET:
			return new FemaleToilet(id, name, latE6, lonE6, floor);
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
			case MTOILET:
				p = new MaleToilet(id, name, latE6, lonE6, floor);
				break;
			case FTOILET:
				p = new FemaleToilet(id, name, latE6, lonE6, floor);
				break;
			case FOUNTAIN:
				p = new Fountain(id, name, latE6, lonE6, floor);
				break;
			case ATHENA:
				p = new Athena(id, name, latE6, lonE6, floor);
				break;
			default:
				continue;
			}
			places.add(p);
		}
		// db.close();
		return places;
	}

	// FIXME: Don't use this.
	public static List<Place> getPlaces(Context context) {
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
			case MTOILET:
				p = new MaleToilet(id, name, latE6, lonE6, floor);
				break;
			case FTOILET:
				p = new FemaleToilet(id, name, latE6, lonE6, floor);
				break;
			case FOUNTAIN:
				p = new Fountain(id, name, latE6, lonE6, floor);
				break;
			case ATHENA:
				p = new Athena(id, name, latE6, lonE6, floor);
				break;
			case CLASSROOM:
				p = new Classroom(id, name, latE6, lonE6, floor);
				break;
			default:
				continue;
			}
			places.add(p);
		}
		// db.close();
		return places;
	}

	public static List<Place> getPlaces(Context context, int latMin,
			int latMax, int lonMin, int lonMax) {
		SQLiteDatabase db = PtolemyDBOpenHelperSingleton
				.getPtolemyDBOpenHelper(context).getReadableDatabase();

		String where = PlacesTable.COLUMN_LAT + ">=? AND "
				+ PlacesTable.COLUMN_LAT + "<=? AND " + PlacesTable.COLUMN_LON
				+ ">=? AND " + PlacesTable.COLUMN_LON + "<=?";
		Cursor c = db.query(
				PlacesTable.PLACES_TABLE_NAME,
				new String[] { PlacesTable.COLUMN_ID, PlacesTable.COLUMN_NAME,
						PlacesTable.COLUMN_LAT, PlacesTable.COLUMN_LON,
						PlacesTable.COLUMN_TYPE, PlacesTable.COLUMN_FLOOR },
				where,
				new String[] { Integer.toString(latMin),
						Integer.toString(latMax), Integer.toString(lonMin),
						Integer.toString(lonMax) }, null, null, null);
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
			case MTOILET:
				p = new MaleToilet(id, name, latE6, lonE6, floor);
				break;
			case FTOILET:
				p = new FemaleToilet(id, name, latE6, lonE6, floor);
				break;
			case FOUNTAIN:
				p = new Fountain(id, name, latE6, lonE6, floor);
				break;
			case ATHENA:
				p = new Athena(id, name, latE6, lonE6, floor);
				break;
			case CLASSROOM:
				p = new Classroom(id, name, latE6, lonE6, floor);
				break;
			default:
				continue;
			}
			places.add(p);
		}
		// Log.v(Config.TAG, "Downloaded a tile: "+places.size());
		Log.v(Config.TAG, "lat" + latMin + "-" + latMax + ", lon" + lonMin
				+ "," + lonMax);
		return places;
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
			case MTOILET:
				return new MaleToilet(in);
			case FTOILET:
				return new FemaleToilet(in);
			case ATHENA:
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
