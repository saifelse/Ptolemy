package edu.mit.pt.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import edu.mit.pt.bookmarks.BookmarksTable;
import edu.mit.pt.classes.MITClassTable;

public class PtolemyOpenHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "ptolemy.db";
	

	public PtolemyOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		PlacesTable.onCreate(db);
		BookmarksTable.onCreate(db);
		MITClassTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		PlacesTable.onUpgrade(db, oldVersion, newVersion);
		BookmarksTable.onUpgrade(db, oldVersion, newVersion);
		MITClassTable.onUpgrade(db, oldVersion, newVersion);
	}

}

