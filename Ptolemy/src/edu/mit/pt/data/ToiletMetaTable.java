package edu.mit.pt.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ToiletMetaTable {
	public static final String TOILET_TABLE_NAME = "toilet";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_PLACEID = "place_id";
	public static final String COLUMN_TYPE = "type";


	public static final String TOILET_TABLE_CREATE = "CREATE TABLE "
			+ TOILET_TABLE_NAME + " (" + COLUMN_ID
			+ " integer primary key autoincrement, " 
			+ COLUMN_PLACEID + " INTEGER not null, "
		    + COLUMN_TYPE + " TEXT not null, "
		    + "FOREIGN KEY("+COLUMN_PLACEID+") REFERENCES "+PlacesTable.PLACES_TABLE_NAME+"("+PlacesTable.COLUMN_ID+")"
			+ ");";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(TOILET_TABLE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(PtolemyOpenHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TOILET_TABLE_NAME);
		onCreate(db);
	}
}