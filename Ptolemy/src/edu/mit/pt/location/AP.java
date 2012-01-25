package edu.mit.pt.location;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.google.android.maps.GeoPoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import android.util.Log;
import edu.mit.pt.Config;
import edu.mit.pt.R;
import edu.mit.pt.data.PlacesTable;

public class AP {

	public static GeoPoint getAPLocation(String bssid, SQLiteDatabase database) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		queryBuilder.setTables(APTable.AP_TABLE_NAME);
		
		queryBuilder.appendWhere(APTable.COLUMN_BSSID + " = '" + bssid + "'");
		
		//SQLiteDatabase database = db.getReadableDatabase();
		Cursor cursor = queryBuilder.query(database, null, null,
				null, null, null, null);
				
		System.out.println(cursor.getCount() + " ssids found");
		if (cursor.getCount() == 0)
			return null;
		int latIndex = cursor.getColumnIndex(APTable.COLUMN_LAT);
		int lonIndex = cursor.getColumnIndex(APTable.COLUMN_LON);
		cursor.moveToFirst();
		int lat = cursor.getInt(latIndex);
		int lon = cursor.getInt(lonIndex);
		return new GeoPoint(lat, lon);
	}
	
	public static Integer loadAPs(Context context, SQLiteDatabase db) {
//		// Delete all rows
//		try {
//			db.delete(APTable.AP_TABLE_NAME, "", new String[] {});
//		} catch (Exception e) {
//			
//		}
		
		// Refetch from file
		int count = 0;

		try {
			InputStream is = context.getResources().openRawResource(R.raw.apslist);
			Reader isr = (Reader)new InputStreamReader(is, "UTF-8");
			BufferedReader reader = new BufferedReader(isr);
			reader.readLine(); //first line is the header
			String line = null;
			db.beginTransaction();
			while ((line = reader.readLine()) != null) {
				String[] splitLine = line.split(",");
				if (splitLine.length != 5)
					continue;
				String bssid = splitLine[0].trim();
				String building = splitLine[1].trim();
				int floor = Integer.parseInt(splitLine[2].trim());
				double lat = Double.parseDouble(splitLine[3].trim());
				double lon = Double.parseDouble(splitLine[4].trim());
				int latE6 = (int)(lat*1e6);
				int lonE6 = (int)(lon*1e6);
				if (addAP(bssid, latE6, lonE6, floor, db))
					count++;
			}
			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("EXCEPTION");
		}
		System.out.println("Inserted " + count + " aps");
		return count;
		
	}
	
	private static boolean addAP(String bssid, int latE6, int lonE6, int floor, SQLiteDatabase db) {
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

	public static class APLoader extends AsyncTask<Context, Integer, Integer>{
		private SQLiteDatabase db;
		public APLoader(SQLiteDatabase db){
			super();
			this.db = db;
		}
		@Override
		protected Integer doInBackground(Context... context) {
			return AP.loadAPs(context[0], db);
		}
		@Override
	     protected void onProgressUpdate(Integer... progress) {
	     }
		@Override
	    protected void onPostExecute(Integer result) {
	         Log.v(Config.TAG,"Downloaded " + result + " APs.");
	         //db.close();
	    }
		
	}
}
