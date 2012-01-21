package edu.mit.pt.bookmarks;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookmarksOpenHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "bookmarks.db";
	static final String BOOKMARKS_TABLE_NAME = "bookmarks";
	static final String COLUMN_ID = "_id";
	static final String COLUMN_NAME = "customName";
	static final String COLUMN_PLACE_ID = "place";
	static final String COLUMN_TYPE = "type";

	public BookmarksOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	private static final String PLACES_TABLE_CREATE = "CREATE TABLE "
			+ BOOKMARKS_TABLE_NAME + " (" + COLUMN_ID + " integer primary key autoincrement, " +
			COLUMN_NAME + " TEXT not null, " +
			COLUMN_PLACE_ID + " INTEGER, " +
			COLUMN_TYPE + " TEXT);";

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(PLACES_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
