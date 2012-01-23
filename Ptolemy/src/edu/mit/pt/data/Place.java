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

	// TODO: is this necessary?
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

	public Drawable getMarker(Resources resources) {
		return resources.getDrawable(getMarkerId());
	}

	abstract public int getMarkerId();

	public static Place getPlace(Context context, int id) {
		// TODO: implement this.
		return new Classroom(id, "10-250", 42361113, -71092261);
	}

	public static Place getClassroom(Context context, String room) {
		if (room == null) {
			return null;
		}
		SQLiteDatabase db = new PtolemyOpenHelper(context)
				.getWritableDatabase();
		Cursor c = db.query(PlacesTable.PLACES_TABLE_NAME, new String[] {
				PlacesTable.COLUMN_NAME, PlacesTable.COLUMN_LAT,
				PlacesTable.COLUMN_LON, PlacesTable.COLUMN_TYPE },
				PlacesTable.COLUMN_NAME + "=?", new String[] { room }, null,
				null, null);
		if (c.getCount() == 0) {
			return null;
		}
		c.moveToFirst();
		int id = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_ID));
		String name = c.getString(c.getColumnIndex(PlacesTable.COLUMN_NAME));
		int latE6 = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_LAT));
		int lonE6 = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_LON));
		String typeName = c
				.getString(c.getColumnIndex(PlacesTable.COLUMN_TYPE));
		PlaceType type = PlaceType.valueOf(typeName);
		c.close();
		db.close();
		// This only searches classrooms.
		if (type != PlaceType.CLASSROOM) {
			return null;
		}
		return new Classroom(id, name, latE6, lonE6);
	}

	public static void addPlace(Context context, String name, int latE6,
			int lonE6, PlaceType type) {
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

	public static List<Place> getPlacesExceptClassrooms(Context context) {
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
			case TOILET:
				// Determine gender
				GenderEnum gender;
				Cursor tc = db.query(ToiletMetaTable.TOILET_TABLE_NAME,
						new String[] { ToiletMetaTable.COLUMN_TYPE },
						"PLACE_ID=?", new String[] { Integer.toString(id) },
						null, null, null);
				if (tc.getCount() == 1) {
					tc.moveToFirst();
					gender = GenderEnum.valueOf(tc.getString(tc
							.getColumnIndex(ToiletMetaTable.COLUMN_TYPE)));
				} else {
					Log.v(Config.TAG, tc.getCount()
							+ " entries found for Toilet id " + id
							+ ". Expected 1 entry. Defaulting to BOTH.");
					gender = GenderEnum.BOTH;
				}
				p = new Toilet(id, name, latE6, lonE6, gender);
				break;
			case FOUNTAIN:
				p = new Fountain(id, name, latE6, lonE6);
			case CLUSTER:
				p = new Athena(id, name, latE6, lonE6);
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

	protected Place(Parcel in) {
		id = in.readInt();
		latE6 = in.readInt();
		lonE6 = in.readInt();
		name = in.readString();
	}
}
