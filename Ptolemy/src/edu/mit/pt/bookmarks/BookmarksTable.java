package edu.mit.pt.bookmarks;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;
import edu.mit.pt.data.PtolemyOpenHelper;

public class BookmarksTable {
	public static final String BOOKMARKS_TABLE_NAME = "bookmarks";
	static final String COLUMN_ID = BaseColumns._ID;
	static final String COLUMN_NAME = "customName";
	static final String COLUMN_PLACE_ID = "place";
	static final String COLUMN_TYPE = "type";

	public static final String BOOKMARKS_TABLE_CREATE = "CREATE TABLE "
			+ BOOKMARKS_TABLE_NAME + " (" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_NAME
			+ " TEXT not null, " + COLUMN_PLACE_ID + " INTEGER, " + COLUMN_TYPE
			+ " TEXT);";

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(BOOKMARKS_TABLE_CREATE);

	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(PtolemyOpenHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + BOOKMARKS_TABLE_NAME);
		onCreate(db);
	}

}
