package edu.mit.pt.classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MITClassesOpenHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "mitclasses.db";
	
	static final String CLASSES_TABLE_NAME = "classes";
	static final String COLUMN_ID = "_id";
	static final String COLUMN_MITID = "mitid";
	static final String COLUMN_TERM = "term";
	static final String COLUMN_NAME = "name";
	static final String COLUMN_PLACEID = "place_id";

	public MITClassesOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	private static final String CLASSES_TABLE_CREATE = "CREATE TABLE "
			+ CLASSES_TABLE_NAME + " (" + COLUMN_ID + " integer primary key autoincrement, " +
			COLUMN_MITID + " TEXT not null, " +
			COLUMN_TERM + " TEXT not null, " +
			COLUMN_NAME + " TEXT not null, " +
			COLUMN_PLACEID + " INTEGER);";

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CLASSES_TABLE_CREATE);
		// TODO: Query server for data 
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
