package edu.mit.pt.classes;

import edu.mit.pt.data.PtolemyOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MITClassTable {

	public static final String CLASSES_TABLE_NAME = "classes";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_MITID = "mitid"; 
	public static final String COLUMN_TIME = "time";
	
	public static final String COLUMN_TERM = "term";
	public static final String COLUMN_NAME = "name";	
	public static final String COLUMN_ROOM = "room";


	private static final String CLASSES_TABLE_CREATE = "CREATE TABLE "
			+ CLASSES_TABLE_NAME + " (" + COLUMN_ID
			+ " integer primary key autoincrement, " 
			+ COLUMN_MITID + " TEXT not null, "
			+ COLUMN_TIME + " TEXT not null, "
			+ COLUMN_TERM + " TEXT not null, "
			+ COLUMN_NAME + " TEXT not null, "
			+ COLUMN_ROOM + " TEXT, "
			+ "CONSTRAINT idtime UNIQUE ("+COLUMN_MITID+", "+COLUMN_TIME+")"
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
}