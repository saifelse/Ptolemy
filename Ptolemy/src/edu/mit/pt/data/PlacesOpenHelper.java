package edu.mit.pt.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class PlacesOpenHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "places.db";
	private static final String PLACES_TABLE_NAME = "places";
	private static final String COLUMN_ID = "_id";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_LAT = "lat";
	private static final String COLUMN_LON = "lon";

	public PlacesOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	private static final String PLACES_TABLE_CREATE = "CREATE TABLE "
			+ PLACES_TABLE_NAME + " (" + COLUMN_ID + " integer primary key autoincrement, " +
			COLUMN_NAME + " text not null, " +
			COLUMN_LAT + " REAL, " +
			COLUMN_LON + " REAL);";

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(PLACES_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
