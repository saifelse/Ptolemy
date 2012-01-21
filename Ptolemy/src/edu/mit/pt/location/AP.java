package edu.mit.pt.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import edu.mit.pt.Config;
import edu.mit.pt.R;
import edu.mit.pt.classes.MITClass;
import edu.mit.pt.data.PlacesTable;

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
			while ((line = reader.readLine()) != null) {
				String[] splitLine = line.split(",");
				if (splitLine.length != 2)
					continue;
				String name = splitLine[0].trim();
				String bssid = splitLine[1].trim();
			}
		} catch (Exception e) {
			System.out.println("EXCEPTION");
		}
		return count;
		
	}
	
	public class APLoader extends AsyncTask<Context, Integer, Integer>{
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
