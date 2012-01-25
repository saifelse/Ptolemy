package edu.mit.pt;

import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
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
import edu.mit.pt.location.WifiDisplayActivity;
import edu.mit.pt.maps.PtolemyMapActivity;
import edu.mit.pt.widgets.SeekBarTestActivity;

public class PtolemyActivity extends Activity {

	boolean needFirstRun = false;
	final boolean DEBUG = true;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		needFirstRun = Config.firstRunCheck(this);
		setContentView(R.layout.first_launch);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (DEBUG) {
			setContentView(R.layout.main);
		} else {
			if (needFirstRun) {
				Config.doFirstRun(this);
			} else {
				startActivity(new Intent(this, PtolemyMapActivity.class));
				finish();
			}
		}
	}

	/*
	 * public void onPause(){ LocationSetter.pause(); } public void onResume(){
	 * LocationSetter.resume(); }
	 */

	public void loadClasses(View view) {
		SQLiteDatabase db = PtolemyDBOpenHelperSingleton
				.getPtolemyDBOpenHelper(this).getWritableDatabase();
		new MITClass.MITClassLoader(db, this).execute();
	}

	public void launchTouchstoneLogin(View view) {
		Intent i = new Intent(this, PrepopulateActivity.class);
		startActivity(i);
	}

	public void launchPtolemyMap(View view) {
		Intent i = new Intent(this, PtolemyMapActivity.class);
		startActivity(i);
	}

	public void launchWifiDisplay(View view) {
		Intent i = new Intent(this, WifiDisplayActivity.class);
		startActivity(i);
	}

	public void resetData(View view) {
		SQLiteDatabase db = PtolemyDBOpenHelperSingleton
				.getPtolemyDBOpenHelper(view.getContext())
				.getWritableDatabase();
		// Recreate tables.
		String[] tables = new String[] { PlacesTable.PLACES_TABLE_NAME,
				BookmarksTable.BOOKMARKS_TABLE_NAME,
				MITClassTable.CLASSES_TABLE_NAME, APTable.AP_TABLE_NAME };
		for (String table : tables) {
			db.execSQL("DROP TABLE IF EXISTS " + table);
		}
		String[] create = new String[] { PlacesTable.PLACES_TABLE_CREATE,
				BookmarksTable.BOOKMARKS_TABLE_CREATE,
				MITClassTable.CLASSES_TABLE_CREATE, APTable.AP_TABLE_CREATE };
		for (String stmt : create) {
			db.execSQL(stmt);
		}

		// Load rooms.
		RoomLoader roomLoader = new RoomLoader(this);
		roomLoader.execute();

		Place.addPlace(this, "testbathroom", 42359101, -71090869, 2,
				PlaceType.TOILET);

		// SQLiteDatabase db = new
		// PtolemyOpenHelper(this).getWritableDatabase();
		new AP.APLoader(db).execute(this);

		// db.close();
		Toast toast = Toast
				.makeText(
						view.getContext(),
						"Reset tables: "
								+ Arrays.toString(tables)
								+ ". Please wait several seconds while room data is downloaded...",
						1000);
		toast.show();
	}
}
