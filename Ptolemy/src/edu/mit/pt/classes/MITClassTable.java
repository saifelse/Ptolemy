package edu.mit.pt.classes;

import java.util.HashMap;

import edu.mit.pt.data.PtolemyOpenHelper;
import android.app.SearchManager;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class MITClassTable {

	public static final String CLASSES_TABLE_NAME = "classes";
	public static final String COLUMN_ID = BaseColumns._ID;
	public static final String COLUMN_MITID = "mitid"; 
	public static final String COLUMN_RESOLVE = "resolve";
	public static final String COLUMN_TERM = "term";
	public static final String COLUMN_NAME = "name";	
	public static final String COLUMN_ROOM = "room";

	public static final String CLASSES_TABLE_CREATE = "CREATE TABLE "
			+ CLASSES_TABLE_NAME + " (" + COLUMN_ID
			+ " integer primary key autoincrement, " 
			+ COLUMN_MITID + " TEXT not null collate nocase, "
			+ COLUMN_RESOLVE + " TEXT not null, "
			+ COLUMN_TERM + " TEXT not null, "
			+ COLUMN_NAME + " TEXT not null, "
			+ COLUMN_ROOM + " TEXT"
			+ ");";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(CLASSES_TABLE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(PtolemyOpenHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + CLASSES_TABLE_NAME);
		onCreate(db);
	}
	
	public static HashMap<String, String> buildColumnMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(SearchManager.SUGGEST_COLUMN_TEXT_1, COLUMN_MITID + " AS "
				+ SearchManager.SUGGEST_COLUMN_TEXT_1);
//		map.put(COLUMN_LAT, COLUMN_LAT);
//		map.put(COLUMN_LON, COLUMN_LON);
//		map.put(COLUMN_FLOOR, COLUMN_FLOOR);
		// Cute SQLite thing: ROWID aliases to whatever primary key you have.
		map.put(BaseColumns._ID, "ROWID AS " + BaseColumns._ID);
		map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "'c' || ROWID AS "
				+ SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		return map;
	}
}