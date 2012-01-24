package edu.mit.pt.data;

import android.content.Context;

public class PtolemyDBOpenHelperSingleton {
	private static PtolemyOpenHelper DBOpenHelper = null;
	
	public static PtolemyOpenHelper getPtolemyDBOpenHelper(Context context) {
		if (DBOpenHelper == null) {
			DBOpenHelper = new PtolemyOpenHelper(context);
		}
		
		return DBOpenHelper;
	}
}
