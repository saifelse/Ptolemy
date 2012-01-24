package edu.mit.pt.data;

import java.util.HashMap;

import android.app.SearchManager;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class PlacesTable {
	public static final String PLACES_TABLE_NAME = "places";
	public static final String COLUMN_ID = BaseColumns._ID;
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_LAT = "lat";
	public static final String COLUMN_LON = "lon";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_FLOOR = "floor";

	static HashMap<String, String> buildColumnMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(SearchManager.SUGGEST_COLUMN_TEXT_1, COLUMN_NAME + " AS "
				+ SearchManager.SUGGEST_COLUMN_TEXT_1);
		map.put(COLUMN_LAT, COLUMN_LAT);
		map.put(COLUMN_LON, COLUMN_LON);
		// Cute SQLite thing: ROWID aliases to whatever primary key you have.
		map.put(BaseColumns._ID, "ROWID AS " + BaseColumns._ID);
		map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "ROWID AS "
				+ SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		return map;
	}

	// table creation
	public static final String PLACES_TABLE_CREATE = "CREATE TABLE "
			+ PLACES_TABLE_NAME + " (" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_NAME
			+ " TEXT not null, " + COLUMN_LAT + " REAL, " + COLUMN_LON
			+ " REAL, " + COLUMN_TYPE + " TEXT);";
	

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(PLACES_TABLE_CREATE);

	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(PtolemyOpenHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + PLACES_TABLE_NAME);
		onCreate(db);
	}
}
