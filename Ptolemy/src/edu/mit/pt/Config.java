package edu.mit.pt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import edu.mit.pt.bookmarks.BookmarksTable;
import edu.mit.pt.classes.MITClass;
import edu.mit.pt.classes.MITClassTable;
import edu.mit.pt.data.Place;
import edu.mit.pt.data.PlaceType;
import edu.mit.pt.data.PlacesTable;
import edu.mit.pt.data.PtolemyDBOpenHelperSingleton;
import edu.mit.pt.data.RoomLoader;
import edu.mit.pt.location.AP;
import edu.mit.pt.location.APTable;
import edu.mit.pt.maps.PtolemyMapActivity;

public class Config {

	static public final String TAG = "PTOLEMY";
	static public final String FIRST_RUN = "firstRun";
	static public final String TERM = "term";
	static public final String DEFAULT_TERM = "fa11";

	/**
	 * Converts from DP to pixels.
	 */
	static public int getPixelsFromDp(Activity a, float dp) {
		float scale = a.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	static public String getTerm(Activity activity) {
		SharedPreferences settings = activity.getPreferences(0);
		String term = settings.getString(TERM, DEFAULT_TERM);
		return term;
	}

	static public boolean firstRunCheck(Activity activity) {
		SharedPreferences settings = activity.getPreferences(0);
		boolean needFirstRun = settings.getBoolean(FIRST_RUN, true);
		if (needFirstRun) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(FIRST_RUN, false);
			editor.commit();
			return true;
		}
		return false;
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
			AsyncTask<Void, ProgressUpdate, Void> {

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
		protected Void doInBackground(Void... params) {

			SQLiteDatabase db = PtolemyDBOpenHelperSingleton
					.getPtolemyDBOpenHelper(activity).getWritableDatabase();

			if (debug) {
				// Recreate tables.
				String[] tables = new String[] { PlacesTable.PLACES_TABLE_NAME,
						BookmarksTable.BOOKMARKS_TABLE_NAME,
						MITClassTable.CLASSES_TABLE_NAME, APTable.AP_TABLE_NAME };
				for (String table : tables) {
					db.execSQL("DROP TABLE IF EXISTS " + table);
				}
				String[] create = new String[] {
						PlacesTable.PLACES_TABLE_CREATE,
						BookmarksTable.BOOKMARKS_TABLE_CREATE,
						MITClassTable.CLASSES_TABLE_CREATE,
						APTable.AP_TABLE_CREATE };
				for (String stmt : create) {
					db.execSQL(stmt);
				}
			}

			publishProgress(new ProgressUpdate("Constructing rooms...", 10));

			int numRooms = new RoomLoader(activity).loadRooms();
			Log.v(TAG, "Loaded " + numRooms + " rooms.");
			publishProgress(new ProgressUpdate("Zapping Wifi data...", 40));

			AP.loadAPs(activity, db);
			publishProgress(new ProgressUpdate("Ptolemizing classes...", 20));

			// TODO: remove debug code.
			Place.addPlace(activity, "testbathroom", 42359101, -71090869, 2,
					PlaceType.TOILET);

			String rawTerm = MITClass.loadClasses(activity, db);
			String term = standardizeTerm(rawTerm);
			SharedPreferences settings = activity.getPreferences(0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(TERM, term);
			editor.commit();

			Log.v(TAG, "Loaded classes for " + term + ".");
			publishProgress(new ProgressUpdate("Done!", 30));

			return null;
		}

		/**
		 * Converts 2012FA to fa12.
		 */
		private String standardizeTerm(String rawTerm) {
			Log.v(Config.TAG, rawTerm);
			Matcher matcher = Pattern.compile("([0-9]{4})([A-Z]{2})").matcher(
					rawTerm);
			if (matcher.matches()) {
				String year = matcher.group(1);
				String semester = matcher.group(2);
				return year.substring(2) + semester.toLowerCase();
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

		protected void onPostExecute(Void result) {
			activity.startActivity(new Intent(activity,
					PtolemyMapActivity.class));
			activity.finish();
		}

	}

}
