package edu.mit.pt;

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

	/**
	 * Converts from DP to pixels.
	 */
	static public int getPixelsFromDp(Activity a, float dp) {
		float scale = a.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
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

	static public class FirstRunTask extends AsyncTask<Void, ProgressUpdate, Void> {

		Activity activity;
		boolean debug;
		ProgressBar bar;
		TextView textView;

		public FirstRunTask(Activity activity, ProgressBar bar, TextView textView, boolean debug) {
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

			int affected = MITClass.loadClasses(activity, db);
			Log.v(TAG, "Loaded " + affected + " classes.");
			publishProgress(new ProgressUpdate("Done!", 30));

			return null;
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
