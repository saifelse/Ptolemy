package edu.mit.pt.data;

import android.app.SearchManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PlacesTable {
	public static final String PLACES_TABLE_NAME = "places";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
	public static final String COLUMN_LAT = "lat";
	public static final String COLUMN_LON = "lon";
	public static final String COLUMN_TYPE = "type";
	
	//table creation
	public static final String PLACES_TABLE_CREATE = "CREATE TABLE "
			+ PLACES_TABLE_NAME + " (" + COLUMN_ID + " integer primary key autoincrement, " +
			COLUMN_NAME + " TEXT not null, " +
			COLUMN_LAT + " REAL, " +
			COLUMN_LON + " REAL, " +
			COLUMN_TYPE + " TEXT);";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(PLACES_TABLE_CREATE);

	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(PtolemyOpenHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + PLACES_TABLE_NAME);
		onCreate(db);
	}
}
