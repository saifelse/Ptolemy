package edu.mit.pt.classes;

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

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import edu.mit.pt.Config;
import edu.mit.pt.R;

public class MITClass {
	private static void addClass(String id, String term, String name,
			String room, String resolve, SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put(MITClassTable.COLUMN_MITID, id);
		cv.put(MITClassTable.COLUMN_TERM, term);
		cv.put(MITClassTable.COLUMN_NAME, name);
		cv.put(MITClassTable.COLUMN_ROOM, room);
		cv.put(MITClassTable.COLUMN_RESOLVE, resolve);
		db.insertOrThrow(MITClassTable.CLASSES_TABLE_NAME, null, cv);
	}

	private static String readJSON(Context context, int resource)
			throws IOException {
		InputStream is = context.getResources().openRawResource(resource);
		Reader isr = (Reader) new InputStreamReader(is, "UTF-8");
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
		db.delete(MITClassTable.CLASSES_TABLE_NAME, "", new String[] {});

		// Refetch from file
		int count = 0;
		String json;

		try {
			json = readJSON(context, R.raw.classes);
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		JSONArray classes;
		try {
			classes = new JSONObject(json).getJSONArray("classes");
		} catch (JSONException e1) {
			Log.v(Config.TAG,
					"Expected 'classes' in classes.json. " + e1.getMessage());
			return 0;
		}
		db.beginTransaction();
		try {
			for (int i = 0; i < classes.length(); i++) {
				try {
					JSONObject c = classes.getJSONObject(i);
					String mitID = c.getString("id");
					String term = c.getString("term");
					String room = c.getString("room");
					String name = c.getString("name");
					String resolve = c.has("resolve") ? c.getString("resolve") : "";
					addClass(mitID, term, name, room, resolve, db);
				} catch (JSONException e) {
					Log.v(Config.TAG, "Error: Couldn't parse element " + i
							+ " in classes.json");
				}
			}
			db.setTransactionSuccessful();
		} finally {
			// TODO: expose to UI if error occurred. quit?
			db.endTransaction();
		}
		return count;
	}

	public static class MITClassLoader extends
			AsyncTask<Context, Integer, Integer> {
		private SQLiteDatabase db;

		public MITClassLoader(SQLiteDatabase db) {
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
			Log.v(Config.TAG, "Downloaded " + result + " classes.");
			db.close();
		}

	}
}