package edu.mit.pt.classes;

import edu.mit.pt.data.PtolemyOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MITClassTable {

	static final String CLASSES_TABLE_NAME = "classes";
	static final String COLUMN_ID = "_id";
	static final String COLUMN_MITID = "mitid";
	static final String COLUMN_TERM = "term";
	static final String COLUMN_NAME = "name";
	static final String COLUMN_PLACEID = "place_id";

	private static final String CLASSES_TABLE_CREATE = "CREATE TABLE "
			+ CLASSES_TABLE_NAME + " (" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_MITID
			+ " TEXT not null, " + COLUMN_TERM + " TEXT not null, "
			+ COLUMN_NAME + " TEXT not null, " + COLUMN_PLACEID + " INTEGER);";

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(CLASSES_TABLE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(PtolemyOpenHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + CLASSES_TABLE_NAME);
		onCreate(db);
	}
}