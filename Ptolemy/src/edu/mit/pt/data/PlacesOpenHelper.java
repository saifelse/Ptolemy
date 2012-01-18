package edu.mit.pt.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PlacesOpenHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "places.db";
	

	public PlacesOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		PlacesTable.onCreate(db);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		PlacesTable.onUpgrade(db, oldVersion, newVersion);
	}

}
