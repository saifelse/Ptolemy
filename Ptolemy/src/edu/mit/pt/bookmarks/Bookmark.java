package edu.mit.pt.bookmarks;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import edu.mit.pt.data.Place;

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
		SQLiteDatabase db = new BookmarksOpenHelper(context).getReadableDatabase();
		Cursor cursor = db.query(BookmarksOpenHelper.BOOKMARKS_TABLE_NAME,
				new String[] { BookmarksOpenHelper.COLUMN_ID,
						BookmarksOpenHelper.COLUMN_NAME,
						BookmarksOpenHelper.COLUMN_PLACE,
						BookmarksOpenHelper.COLUMN_TYPE }, null, null, null,
				null, null);
		List<Bookmark> bookmarks = new ArrayList<Bookmark>();
		int idIndex = cursor.getColumnIndex(BookmarksOpenHelper.COLUMN_ID);
		int customNameIndex = cursor.getColumnIndex(BookmarksOpenHelper.COLUMN_NAME);
		int placeIndex = cursor.getColumnIndex(BookmarksOpenHelper.COLUMN_PLACE);
		int typeIndex = cursor.getColumnIndex(BookmarksOpenHelper.COLUMN_TYPE);
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			int id = cursor.getInt(idIndex);
			String customName = cursor.getString(customNameIndex);
			int placeId = cursor.getInt(placeIndex);
			BookmarkType type = BookmarkType.valueOf(cursor.getString(typeIndex));
			Place place = Place.getPlace(context, placeId);
			bookmarks.add(new Bookmark(id, customName, place, type));
		}
		bookmarks.add(new Bookmark(1, "6.034", new Place("10-250", 42361113, -71092261), BookmarkType.LECTURE));
		bookmarks.add(new Bookmark(1, "6.034", new Place("34-123", 42361113, -71092261), BookmarkType.RECITATION));
		cursor.close();
		db.close();
		return bookmarks;
	}
}
