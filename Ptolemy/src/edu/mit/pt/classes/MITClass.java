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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.mit.pt.Config;
import edu.mit.pt.R;
import edu.mit.pt.data.Place;
import edu.mit.pt.data.PtolemyDBOpenHelperSingleton;

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

	public static String loadClasses(Context context, SQLiteDatabase db)
			throws JSONException, IOException {
		// Delete all rows
		db.delete(MITClassTable.CLASSES_TABLE_NAME, "", new String[] {});

		// Refetch from file
		String json;

		json = readJSON(context, R.raw.classes);
		JSONObject obj = new JSONObject(json);

		String term = obj.getString("term");

		JSONArray classes = obj.getJSONArray("classes");
		db.beginTransaction();
		for (int i = 0; i < classes.length(); i++) {
			JSONObject c = classes.getJSONObject(i);
			String mitID = c.getString("id");
			String room = c.getString("room");
			String name = c.getString("name");
			String resolve = c.has("resolve") ? c.getString("resolve") : "";
			addClass(mitID, term, name, room, resolve, db);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		return term;
	}

	public static long getIdIfValidRoom(Context context, String name) {
		SQLiteDatabase db = PtolemyDBOpenHelperSingleton
				.getPtolemyDBOpenHelper(context).getReadableDatabase();
		Cursor c = db.query(MITClassTable.CLASSES_TABLE_NAME, new String[] {
				MITClassTable.COLUMN_ID, MITClassTable.COLUMN_ROOM },
				MITClassTable.COLUMN_MITID + "=?", new String[] { name }, null,
				null, null);
		if (c.getCount() == 0) {
			return -1;
		}
		c.moveToFirst();
		String roomName = c.getString(c
				.getColumnIndex(MITClassTable.COLUMN_ROOM));
		Log.v(Config.TAG, "Search for " + name + " found a room: " + roomName);
		Place room = Place.getClassroom(context, roomName);
		if (room == null) {
			Log.v(Config.TAG, "Couldn't find " + roomName);
			return -1;
		}
		return c.getLong(c.getColumnIndex(MITClassTable.COLUMN_ID));
	}

}