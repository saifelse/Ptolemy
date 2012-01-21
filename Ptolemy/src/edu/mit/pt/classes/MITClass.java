package edu.mit.pt.classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.pt.Config;

import edu.mit.pt.R;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import edu.mit.pt.data.PlacesTable;

public class MITClass {
	private static boolean addClass(String id, String term, String name,
			String placeId, SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put(MITClassTable.COLUMN_MITID, id);
		cv.put(MITClassTable.COLUMN_TERM, term);
		cv.put(MITClassTable.COLUMN_NAME, name);
		cv.put(MITClassTable.COLUMN_PLACEID, placeId);
		try {
			db.insertOrThrow(MITClassTable.CLASSES_TABLE_NAME, null, cv);
			return true;
		} catch (SQLException e) {
			Log.v(Config.TAG, "Couldn't insert. " + e.getMessage());
		}
		return false;
	}
	
	private static String readJSON(Context context, int resource) throws IOException{
		InputStream is = context.getResources().openRawResource(resource);
		Reader isr = (Reader)new InputStreamReader(is, "UTF-8");
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
		    Reader reader = new BufferedReader(isr);
		    int n;
		    while ((n = reader.read(buffer)) != -1) {
		        writer.write(buffer, 0, n);
		    }
		} finally {
		    is.close();
		}
		return writer.toString();
	}

	protected static int loadClasses(Context context, SQLiteDatabase db) {
		// Delete all rows
		db.delete(PlacesTable.PLACES_TABLE_NAME, "", new String[] {});
		
		// Refetch from file
		int count = 0;
		String json;
		
		try {
			json = readJSON(context, R.raw.classes);
		} catch (IOException e2) {
			e2.printStackTrace();
			return 0;
		}
		Log.v(Config.TAG, ""+json.length());
		Log.v(Config.TAG, json);
		JSONArray classes;
		try {
			classes = new JSONObject(json).getJSONArray("classes");
		} catch (JSONException e1) {
			Log.v(Config.TAG, "Expected 'classes' in class.json. "+e1.getMessage());
			return 0;
		}
		for (int i = 0; i < classes.length(); i++) {
			String mitID, term, room, name;
			try {
				JSONObject c = classes.getJSONObject(i);
				mitID = c.getString("id");
				term = c.getString("term");
				room = c.getString("room");
				name = c.getString("name");
			} catch (JSONException e) {
				Log.v(Config.TAG, "Error: Couldn't parse element " + i
						+ " in classes.json");
				continue;
			}
			// Get place ID
			Cursor result = db.query(PlacesTable.PLACES_TABLE_NAME,
					new String[] { PlacesTable.COLUMN_ID },
					PlacesTable.COLUMN_NAME + "=?", new String[] { room },
					null, null, null, "1");
			if (result.getCount() == 1) {
				String placeId = result.getString(result
						.getColumnIndex(PlacesTable.COLUMN_ID));
				if(addClass(mitID, term, name, placeId, db))count++;
			} else if(result.getCount() == 0){
				String placeId = "0";
				if(addClass(mitID, term, name, placeId, db))count++;
			} else {
				Log.v(Config.TAG, "Error: Found " + result.getCount()
						+ " results for " + room + ". Expected 1.");
			}
		}
		return count;
	}
	public static class MITClassLoader extends AsyncTask<Context, Integer, Integer>{
		private SQLiteDatabase db;
		public MITClassLoader(SQLiteDatabase db){
			super();
			this.db = db;
		}
		@Override
		protected Integer doInBackground(Context... context) {
			return MITClass.loadClasses(context[0], db);
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