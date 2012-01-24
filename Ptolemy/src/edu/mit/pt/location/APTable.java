package edu.mit.pt.location;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.mit.pt.data.PtolemyOpenHelper;

public class APTable {
	public static final String AP_TABLE_NAME = "ap";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_LOCATION = "customName";
	public static final String COLUMN_BSSID = "place";

	private static final String AP_TABLE_CREATE = "CREATE TABLE "
			+ AP_TABLE_NAME + " (" + COLUMN_ID + " integer primary key autoincrement, " +
			COLUMN_LOCATION + " TEXT not null, " +
			COLUMN_BSSID + " TEXT not null);";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(AP_TABLE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(PtolemyOpenHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + AP_TABLE_NAME);
		onCreate(db);
	}

}
