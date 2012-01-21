package edu.mit.pt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import edu.mit.pt.location.AP;
import edu.mit.pt.location.WifiDisplayActivity;
import edu.mit.pt.location.WifiLocation;
import edu.mit.pt.classes.MITClass;
import edu.mit.pt.data.PtolemyOpenHelper;
import edu.mit.pt.maps.PtolemyMapActivity;

public class PtolemyActivity extends Activity {
	final static int REQUEST_MOIRA = 1;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        WifiLocation w = new WifiLocation(this);
        w.scanResults();
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
    	new MITClass.MITClassLoader(db).execute(new Context[]{this});
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
    public void onActivityResult(int requestCode, int resultCode, Intent data){
    	switch(requestCode){
    	case REQUEST_MOIRA:
    		if(resultCode == RESULT_OK){
    			TextView classText = (TextView) findViewById(R.id.SelectedClasses);
    			classText.setText("");
    			String[] classes = (String[])data.getExtras().get(ClassDataIntent.CLASSES);
    			for(String classname : classes){
    				classText.append(classname+"\n");
    			}
    		}
    	}
    }
}
