package edu.mit.pt.bookmarks;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import edu.mit.pt.data.Place;
import edu.mit.pt.data.PtolemyDBOpenHelperSingleton;

public class Bookmark {

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
}
