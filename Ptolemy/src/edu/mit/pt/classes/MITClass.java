package edu.mit.pt.classes;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import edu.mit.pt.data.Place;

public class MITClass {
	private static final String DATABASE_NAME = "mitclasses.db";
	private static final String CLASSES_TABLE_NAME = "classes";
	private static final SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DATABASE_NAME, null);
	
	private String id;
	private String term;
	private String name;
	private Place place;

	private MITClass(String id, String term, String name, Place place) {
		this.id = id;
		this.term = term;
		this.name = name;
		this.place = place;
	}
	
	public String getId() {
		return id;
	}

	public String getTerm() {
		return term;
	}

	public String getName() {
		return name;
	}

	public Place getPlace() {
		return place;
	}

	private static boolean addOrReplaceClass(MITClass mitClass) {
		//db.insertWithOnConflict(CLASSES_TABLE_NAME, nullColumnHack, initialValues, conflictAlgorithm)		
		return false;
	}

	private static boolean removeClass(String id) {
		return false;
	}

	public static MITClass getClass(String search) {
		return null;
	}

	public static List<MITClass> getClasses(String search) {
		return null;
	}
	
	
	
	
	
	
}