package edu.mit.pt.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.google.android.maps.GeoPoint;

import edu.mit.pt.Config;
import edu.mit.pt.R;

public class AP {

	public static APGeoPoint getAPLocation(String bssid, SQLiteDatabase database) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		queryBuilder.setTables(APTable.AP_TABLE_NAME);

		queryBuilder.appendWhere(APTable.COLUMN_BSSID + " = '" + bssid + "'");

		// SQLiteDatabase database = db.getReadableDatabase();
		Cursor cursor = queryBuilder.query(database, null, null, null, null,
				null, null);

		Log.v(Config.TAG, cursor.getCount() + " ssids found");
		if (cursor.getCount() == 0) {
			System.out.println("Could not find " + bssid);
			return null;
		}
		int latIndex = cursor.getColumnIndex(APTable.COLUMN_LAT);
		int lonIndex = cursor.getColumnIndex(APTable.COLUMN_LON);
		int flrIndex = cursor.getColumnIndex(APTable.COLUMN_FLOOR);
		cursor.moveToFirst();
		int lat = cursor.getInt(latIndex);
		int lon = cursor.getInt(lonIndex);
		int flr = cursor.getInt(flrIndex);
		System.out.println("FLOOR: " + flr + " BSSID: " + bssid);
		return new APGeoPoint(lat, lon, flr);
	}

	public static Integer loadAPs(Context context, SQLiteDatabase db)
			throws IOException {

		// Refetch from file
		int count = 0;

		InputStream is = context.getResources().openRawResource(R.raw.apslist);
		Reader isr = (Reader) new InputStreamReader(is, "UTF-8");
		BufferedReader reader = new BufferedReader(isr);
		reader.readLine(); // first line is the header
		String line = null;
		db.beginTransaction();
		while ((line = reader.readLine()) != null) {
			String[] splitLine = line.split(",");
			if (splitLine.length != 5)
				continue;
			String bssid = splitLine[0].trim();
			String building = splitLine[1].trim();
			int floor = 0;
			try {
				floor = Integer.parseInt(splitLine[2].trim());
			} catch (NumberFormatException e) {
				continue;
			}
			double lat = Double.parseDouble(splitLine[3].trim());
			double lon = Double.parseDouble(splitLine[4].trim());
			int latE6 = (int) (lat * 1e6);
			int lonE6 = (int) (lon * 1e6);
			if (addAP(bssid, latE6, lonE6, floor, db))
				count++;
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		Log.v(Config.TAG, "Inserted " + count + " aps");
		return count;

	}

	private static boolean addAP(String bssid, int latE6, int lonE6, int floor,
			SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put(APTable.COLUMN_BSSID, bssid);
		cv.put(APTable.COLUMN_LAT, latE6);
		cv.put(APTable.COLUMN_LON, lonE6);
		cv.put(APTable.COLUMN_FLOOR, floor);
		try {
			db.insertOrThrow(APTable.AP_TABLE_NAME, null, cv);
			return true;
		} catch (SQLException e) {
			Log.v(Config.TAG, "Couldn't insert. " + e.getMessage());
		}
		return false;
	}
}
