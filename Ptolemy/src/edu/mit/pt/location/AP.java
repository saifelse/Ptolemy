package edu.mit.pt.location;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import edu.mit.pt.Config;
import edu.mit.pt.R;

public class AP {

	public static Integer loadAPs(Context context, SQLiteDatabase db) {
		// Delete all rows
		db.delete(APTable.AP_TABLE_NAME, "", new String[] {});
		
		// Refetch from file
		int count = 0;

		try {
			InputStream is = context.getResources().openRawResource(R.raw.aps);
			Reader isr = (Reader)new InputStreamReader(is, "UTF-8");
			BufferedReader reader = new BufferedReader(isr);
			reader.readLine(); //first line
			String line = null;
			db.beginTransaction();
			while ((line = reader.readLine()) != null) {
				String[] splitLine = line.split(",");
				if (splitLine.length != 2)
					continue;
				String location = splitLine[0].trim();
				String bssid = splitLine[1].trim();
				if (addAP(location, bssid, db))
					count++;
			}
			db.endTransaction();
		} catch (Exception e) {
			System.out.println("EXCEPTION");
		}
		return count;
		
	}
	
	private static boolean addAP(String location, String bssid, SQLiteDatabase db) {
		ContentValues cv = new ContentValues();;
		cv.put(APTable.COLUMN_LOCATION, location);
		cv.put(APTable.COLUMN_BSSID, bssid);
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
	         Log.v(Config.TAG,"Downloaded " + result + " classes.");
	         db.close();
	    }
		
	}
}
