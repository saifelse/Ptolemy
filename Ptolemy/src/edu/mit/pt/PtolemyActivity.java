package edu.mit.pt;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import edu.mit.pt.location.AP;
import edu.mit.pt.location.APTable;
import edu.mit.pt.location.WifiDisplayActivity;
import edu.mit.pt.location.WifiLocation;
import android.widget.Toast;
import edu.mit.pt.bookmarks.BookmarksTable;
import edu.mit.pt.classes.MITClass;
import edu.mit.pt.classes.MITClassTable;
import edu.mit.pt.data.PlacesTable;
import edu.mit.pt.data.PtolemyOpenHelper;
import edu.mit.pt.data.RoomLoader;
import edu.mit.pt.maps.PtolemyMapActivity;

public class PtolemyActivity extends Activity {
	final static int REQUEST_MOIRA = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SQLiteDatabase db = new PtolemyOpenHelper(this).getWritableDatabase();
        new AP.APLoader(db).execute(this);
    }
    /*
    public void onPause(){
    	LocationSetter.pause();
    }
    public void onResume(){
    	LocationSetter.resume();	
    }*/
    
    public void loadClasses(View view){
    	SQLiteDatabase db = new PtolemyOpenHelper(this).getWritableDatabase();
    	new MITClass.MITClassLoader(db, this).execute();
    }
    public void launchTouchstoneLogin(View view){
    	Intent i = new Intent(this, PrepopulateActivity.class);
    	startActivityForResult(i, REQUEST_MOIRA);
    }
    public void launchPtolemyMap(View view){
    	Intent i = new Intent(this, PtolemyMapActivity.class);
    	startActivity(i);
    }
    public void launchWifiDisplay(View view){
    	Intent i = new Intent(this, WifiDisplayActivity.class);
    	startActivity(i);
    }
    
	public void resetData(View view) {
		SQLiteDatabase db = new PtolemyOpenHelper(view.getContext())
				.getWritableDatabase();
		// Recreate tables.
		String[] tables = new String[] { PlacesTable.PLACES_TABLE_NAME,
				BookmarksTable.BOOKMARKS_TABLE_NAME,
				MITClassTable.CLASSES_TABLE_NAME,
				APTable.AP_TABLE_NAME
				};
		for (String table : tables) {
			db.execSQL("DROP TABLE IF EXISTS " + table);
		}
		String[] create = new String[] {
				PlacesTable.PLACES_TABLE_CREATE,
				BookmarksTable.BOOKMARKS_TABLE_CREATE,
				MITClassTable.CLASSES_TABLE_CREATE
		};
		for (String stmt : create) {
			db.execSQL(stmt);
		}
		
		// Load rooms.
		RoomLoader roomLoader = new RoomLoader(this);
		roomLoader.execute();
		
		
		db.close();
		Toast toast = Toast.makeText(view.getContext(), "Reset tables: "
				+ Arrays.toString(tables)+". Please wait several seconds while room data is downloaded...", 1000);
		toast.show();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_MOIRA:
			if (resultCode == RESULT_OK) {
				
				//TextView classText = (TextView) findViewById(R.id.SelectedClasses);
				//classText.setText("");
				StringBuffer classText = new StringBuffer("We found these classes: \n");
				String[] classes = (String[]) data.getExtras().get(
						ClassDataIntent.CLASSES);
				for (String classname : classes) {
					classText.append(classname + "\n");
				}
				Toast toast = Toast.makeText(this, classText, 1000);
				toast.show();
			}
		}
	}
}
