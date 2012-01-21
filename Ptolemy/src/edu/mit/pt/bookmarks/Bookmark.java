package edu.mit.pt.bookmarks;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import edu.mit.pt.data.Place;
import edu.mit.pt.data.PtolemyOpenHelper;

public class Bookmark {

	private int id;
	private String customName;
	private Place place;
	private BookmarkType type;

	private Bookmark(int id, String customName, Place place, BookmarkType type) {
		this.id = id;
		this.customName = customName;
		this.place = place;
		this.type = type;
	}

	public int getId() {
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

	static public List<Bookmark> getBookmarks(Context context) {
		SQLiteDatabase db = new PtolemyOpenHelper(context)
				.getReadableDatabase();
		Cursor cursor = db.query(BookmarksTable.BOOKMARKS_TABLE_NAME,
				new String[] { BookmarksTable.COLUMN_ID,
						BookmarksTable.COLUMN_NAME,
						BookmarksTable.COLUMN_PLACE_ID,
						BookmarksTable.COLUMN_TYPE }, null, null, null, null,
				null);
		List<Bookmark> bookmarks = new ArrayList<Bookmark>();
		int idIndex = cursor.getColumnIndex(BookmarksTable.COLUMN_ID);
		int customNameIndex = cursor.getColumnIndex(BookmarksTable.COLUMN_NAME);
		int placeIndex = cursor.getColumnIndex(BookmarksTable.COLUMN_PLACE_ID);
		int typeIndex = cursor.getColumnIndex(BookmarksTable.COLUMN_TYPE);
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			int id = cursor.getInt(idIndex);
			String customName = cursor.getString(customNameIndex);
			int placeId = cursor.getInt(placeIndex);
			BookmarkType type = BookmarkType.valueOf(cursor
					.getString(typeIndex));
			Place place = Place.getPlace(context, placeId);
			bookmarks.add(new Bookmark(id, customName, place, type));
		}
		cursor.close();
		db.close();
		return bookmarks;
	}

	static public void addBookmark(Context context, String customName,
			Place place, BookmarkType type) {
		SQLiteDatabase db = new PtolemyOpenHelper(context)
				.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put(BookmarksTable.COLUMN_NAME, customName);
		values.put(BookmarksTable.COLUMN_TYPE, type.name());
		db.insert(BookmarksTable.BOOKMARKS_TABLE_NAME, null, values);
		db.close();
	}
}
