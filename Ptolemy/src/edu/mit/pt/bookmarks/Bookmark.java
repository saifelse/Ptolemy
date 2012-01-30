package edu.mit.pt.bookmarks;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import edu.mit.pt.data.Place;
import edu.mit.pt.data.PtolemyDBOpenHelperSingleton;

public class Bookmark implements Parcelable {

	private long id;
	private String customName;
	private Place place;
	private BookmarkType type;

	private Bookmark(long id, String customName, Place place, BookmarkType type) {
		this.id = id;
		this.customName = customName;
		this.place = place;
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public String getCustomName() {
		return customName;
	}

	public Place getPlace() {
		return place;
	}

	public BookmarkType getType() {
		return type;
	}

	static public Bookmark getBookmark(Context context, long id) {
		SQLiteDatabase db = PtolemyDBOpenHelperSingleton
				.getPtolemyDBOpenHelper(context).getReadableDatabase();
		Cursor cursor = db.query(BookmarksTable.BOOKMARKS_TABLE_NAME,
				new String[] { BookmarksTable.COLUMN_NAME,
						BookmarksTable.COLUMN_PLACE_ID,
						BookmarksTable.COLUMN_TYPE }, BookmarksTable.COLUMN_ID
						+ "=?", new String[] { Long.toString(id) }, null, null,
				null);
		if (cursor.getCount() == 0) {
			return null;
		}
		cursor.moveToFirst();

		String customName = cursor.getString(cursor
				.getColumnIndex(BookmarksTable.COLUMN_NAME));
		long placeId = cursor.getLong(cursor
				.getColumnIndex(BookmarksTable.COLUMN_PLACE_ID));
		Place place = Place.getPlace(context, placeId);
		String typeName = cursor.getString(cursor
				.getColumnIndex(BookmarksTable.COLUMN_TYPE));
		BookmarkType type = BookmarkType.valueOf(typeName);

		return new Bookmark(id, customName, place, type);
	}

	static public void addBookmark(Context context, String customName,
			Place place, BookmarkType type) {
		SQLiteDatabase db = PtolemyDBOpenHelperSingleton
				.getPtolemyDBOpenHelper(context).getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put(BookmarksTable.COLUMN_NAME, customName);
		values.put(BookmarksTable.COLUMN_TYPE, type.name());
		values.put(BookmarksTable.COLUMN_PLACE_ID, place.getId());
		db.insert(BookmarksTable.BOOKMARKS_TABLE_NAME, null, values);
	}

	static public void deleteBookmark(Context context, long id) {
		SQLiteDatabase db = PtolemyDBOpenHelperSingleton
				.getPtolemyDBOpenHelper(context).getReadableDatabase();
		db.delete(BookmarksTable.BOOKMARKS_TABLE_NAME, BookmarksTable.COLUMN_ID
				+ "=?", new String[] { Long.toString(id) });
	}

	static public void updateBookmark(Context context, long id,
			String customName, Place place, BookmarkType type) {
		SQLiteDatabase db = PtolemyDBOpenHelperSingleton
				.getPtolemyDBOpenHelper(context).getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put(BookmarksTable.COLUMN_NAME, customName);
		values.put(BookmarksTable.COLUMN_TYPE, type.name());
		values.put(BookmarksTable.COLUMN_PLACE_ID, place.getId());
		db.update(BookmarksTable.BOOKMARKS_TABLE_NAME, values,
				BookmarksTable.COLUMN_ID + "=?",
				new String[] { Long.toString(id) });
	}

	static public List<Long> findInBookmarks(Context context, Place p) {
		SQLiteDatabase db = PtolemyDBOpenHelperSingleton
				.getPtolemyDBOpenHelper(context).getReadableDatabase();
		Cursor c = db.query(BookmarksTable.BOOKMARKS_TABLE_NAME,
				new String[] { BookmarksTable.COLUMN_ID, BookmarksTable.COLUMN_PLACE_ID }, null, null,
				null, null, null);
		List<Long> longs = new ArrayList<Long>();
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			long placeId = c.getLong(c.getColumnIndex(BookmarksTable.COLUMN_PLACE_ID));
			if (placeId == p.getId()) {
				longs.add(c.getLong(c.getColumnIndex(BookmarksTable.COLUMN_ID)));
			}
		}
		return longs;
	}
	
	@Override
	public int describeContents() {
        return 0;
    }

	@Override
    public void writeToParcel(Parcel out, int flags) {
		out.writeLong(id);
		out.writeString(customName);
		out.writeParcelable(place, 0);
		out.writeString(type.name());
    }

    public static final Parcelable.Creator<Bookmark> CREATOR
            = new Parcelable.Creator<Bookmark>() {
        public Bookmark createFromParcel(Parcel in) {
            return new Bookmark(in);
        }

        public Bookmark[] newArray(int size) {
            return new Bookmark[size];
        }
    };

    private Bookmark(Parcel in) {
    	id = in.readLong();
    	customName = in.readString();
    	place = in.readParcelable(Place.class.getClassLoader());
    	type = BookmarkType.valueOf(in.readString());
    }
}
