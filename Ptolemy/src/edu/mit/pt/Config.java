package edu.mit.pt;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;

import edu.mit.pt.bookmarks.BookmarksTable;
import edu.mit.pt.classes.MITClass;
import edu.mit.pt.classes.MITClassTable;
import edu.mit.pt.data.PlaceType;
import edu.mit.pt.data.PlacesTable;
import edu.mit.pt.data.PtolemyDBOpenHelperSingleton;
import edu.mit.pt.data.RoomLoader;
import edu.mit.pt.location.AP;
import edu.mit.pt.location.APGeoPoint;
import edu.mit.pt.location.APTable;
import edu.mit.pt.maps.PtolemyMapActivity;

public class Config {

	static public final String TAG = "PTOLEMY";
	static public final String FIRST_RUN = "firstRun";
	static public final String TERM = "term";
	static public final String DEFAULT_TERM = "sp11";
	static public final String TOUR_TAKEN = "tourTaken";
	static public final String FILTER = "filter";
	static public final String LAST_LAT = "lastLat";
	static public final String LAST_LON = "lastLon";
	static public final String LAST_FLOOR = "lastFloor";
	static public final String SHOW_ADD_BOOKMARK_HELP = "addBookmarkHelp";
	static public final GeoPoint DEFAULT_POINT = new GeoPoint(42361283,
			-71092025);
	static public final int DEFAULT_FLOOR = 1;
	static private final String SHARED_PREF = "PtolemyPrefsFile";

	/**
	 * Converts from DP to pixels.
	 */
	static public int getPixelsFromDp(Activity a, float dp) {
		float scale = a.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	static public void clearPreferences(Activity activity) {
		SharedPreferences.Editor editor = activity.getSharedPreferences(
				SHARED_PREF, 0).edit();
		editor.clear();
		editor.commit();
	}

	static public String getTerm(Activity activity) {
		SharedPreferences settings = activity.getSharedPreferences(SHARED_PREF,
				0);
		String term = settings.getString(TERM, DEFAULT_TERM);
		return term;
	}

	static public boolean isTourTaken(Activity activity) {
		SharedPreferences settings = activity.getSharedPreferences(SHARED_PREF,
				0);
		return settings.getBoolean(TOUR_TAKEN, false);
	}

	static public void setTourTaken(Activity activity) {
		SharedPreferences settings = activity.getSharedPreferences(SHARED_PREF,
				0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(TOUR_TAKEN, true);
		editor.commit();
	}

	static public boolean shouldShowBookmarkHelp(Activity activity) {
		SharedPreferences settings = activity.getSharedPreferences(SHARED_PREF,
				0);
		return settings.getBoolean(SHOW_ADD_BOOKMARK_HELP, false);
	}

	static public void setShouldShowBookmarkHelp(Activity activity, boolean val) {
		SharedPreferences settings = activity.getSharedPreferences(SHARED_PREF,
				0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(SHOW_ADD_BOOKMARK_HELP, val);
		editor.commit();
	}

	static public boolean firstRunCheck(Activity activity) {
		SharedPreferences settings = activity.getSharedPreferences(SHARED_PREF,
				0);
		return settings.getBoolean(FIRST_RUN, true);
	}

	static private class ProgressUpdate {
		String message;
		int increment;

		public ProgressUpdate(String message, int increment) {
			this.message = message;
			this.increment = increment;
		}
	}

	static public class FirstRunTask extends
			AsyncTask<Void, ProgressUpdate, Boolean> {

		Activity activity;
		boolean debug;
		ProgressBar bar;
		TextView textView;

		public FirstRunTask(Activity activity, ProgressBar bar,
				TextView textView, boolean debug) {
			this.activity = activity;
			this.debug = debug;
			this.bar = bar;
			this.textView = textView;
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			SQLiteDatabase db = PtolemyDBOpenHelperSingleton
					.getPtolemyDBOpenHelper(activity).getWritableDatabase();

			// Recreate tables.
			String[] tables = new String[] { PlacesTable.PLACES_TABLE_NAME,
					MITClassTable.CLASSES_TABLE_NAME, APTable.AP_TABLE_NAME };
			for (String table : tables) {
				db.execSQL("DROP TABLE IF EXISTS " + table);
			}
			String[] create = new String[] { PlacesTable.PLACES_TABLE_CREATE,
					PlacesTable.PLACES_INDEX_CREATE,
					MITClassTable.CLASSES_TABLE_CREATE, APTable.AP_TABLE_CREATE };
			for (String stmt : create) {
				db.execSQL(stmt);
			}

			if (debug) {
				db.execSQL("DROP TABLE IF EXISTS "
						+ BookmarksTable.BOOKMARKS_TABLE_NAME);
				db.execSQL(BookmarksTable.BOOKMARKS_TABLE_CREATE);
			}

			publishProgress(new ProgressUpdate("Constructing rooms...", 10));

			boolean success = true;

			try {

				int numRooms = new RoomLoader(activity).loadRooms();
				Log.v(TAG, "Loaded " + numRooms + " rooms.");
				publishProgress(new ProgressUpdate("Zapping Wifi data...", 40));

				AP.loadAPs(activity, db);
				publishProgress(new ProgressUpdate("Ptolemizing classes...", 20));

				String rawTerm = MITClass.loadClasses(activity, db);
				String term = standardizeTerm(rawTerm);
				SharedPreferences settings = activity.getSharedPreferences(
						SHARED_PREF, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(TERM, term);
				editor.commit();

				Log.v(TAG, "Loaded classes for " + term + ".");
				publishProgress(new ProgressUpdate("Done!", 30));

			} catch (IOException e) {
				e.printStackTrace();
				success = false;
				activity.runOnUiThread(new Runnable() {
					public void run() {
						activity.showDialog(PtolemyActivity.DIALOG_ERROR_NETWORK);
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
				success = false;
				activity.runOnUiThread(new Runnable() {
					public void run() {
						activity.showDialog(PtolemyActivity.DIALOG_ERROR_JSON);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				success = false;
				activity.runOnUiThread(new Runnable() {
					public void run() {
						activity.showDialog(PtolemyActivity.DIALOG_ERROR_OTHER);
					}
				});
			}

			return success;
		}

		/**
		 * Converts 2012FA to fa12.
		 */
		static private String standardizeTerm(String rawTerm) {
			Log.v(Config.TAG, rawTerm);
			Matcher matcher = Pattern.compile("([0-9]{4})([A-Z]{2})").matcher(
					rawTerm);
			if (matcher.matches()) {
				String year = matcher.group(1);
				String semester = matcher.group(2);
				return semester.toLowerCase() + year.substring(2);
			}
			return DEFAULT_TERM;
		}

		protected void onProgressUpdate(ProgressUpdate... progress) {
			ProgressUpdate pu = progress[0];
			if (bar != null) {
				bar.incrementProgressBy(pu.increment);
			}
			if (textView != null) {
				textView.setText(pu.message);
			}
		}

		protected void onPostExecute(Boolean result) {
			if (!result) {
				return;
			}
			SharedPreferences settings = activity.getSharedPreferences(
					SHARED_PREF, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(FIRST_RUN, false);
			editor.commit();

			activity.startActivity(new Intent(activity,
					PtolemyMapActivity.class));
			activity.finish();
		}

	}
	
	static private String makeFilterKey(PlaceType type) {
		return FILTER + "_" + type.name();
	}

	static public void saveFilter(Activity activity, PlaceType type, boolean isChecked) {
		SharedPreferences settings = activity.getSharedPreferences(SHARED_PREF,
				0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(makeFilterKey(type), isChecked);
		editor.commit();
	}
	
	static public boolean getFilter(Activity activity, PlaceType type) {
		SharedPreferences settings = activity.getSharedPreferences(SHARED_PREF,
				0);
		return settings.getBoolean(makeFilterKey(type), true);
	}
	
	static public void saveLocation(Activity activity, int latE6, int lonE6, int userFloor) {
		SharedPreferences settings = activity.getSharedPreferences(SHARED_PREF,
				0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(LAST_LAT, latE6);
		editor.putInt(LAST_LON, lonE6);
		editor.putInt(LAST_FLOOR, userFloor);
		editor.commit();
	}
	
	static public APGeoPoint getLocation(Activity activity) {
		SharedPreferences settings = activity.getSharedPreferences(SHARED_PREF,
				0);
		int latE6 = settings.getInt(LAST_LAT, DEFAULT_POINT.getLatitudeE6());
		int lonE6 = settings.getInt(LAST_LON, DEFAULT_POINT.getLongitudeE6());
		int floor = settings.getInt(LAST_FLOOR, DEFAULT_FLOOR);
		return new APGeoPoint(latE6, lonE6, floor);
	}

}
