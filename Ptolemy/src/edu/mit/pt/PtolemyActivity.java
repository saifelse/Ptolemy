package edu.mit.pt;

import com.google.android.maps.MapView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import edu.mit.pt.maps.LocationSetter;
import edu.mit.pt.maps.PtolemyMapActivity;
import edu.mit.pt.maps.XPSOverlay;

public class PtolemyActivity extends Activity {
	final static int REQUEST_MOIRA = 1;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		// Show user
		MapView mapView = (MapView) findViewById(R.id.mapview);
		XPSOverlay meOverlay = new XPSOverlay(mapView);
		mapView.getOverlays().add(meOverlay);
        
		// Start Location data
        String skyhookUsername = getString(R.string.skyhook_username);
        String skyhookRealm = getString(R.string.skyhook_realm);
        LocationSetter.init(this, skyhookUsername, skyhookRealm, meOverlay);
        LocationSetter.resume();
        
    }
    
    public void onPause(){
    	LocationSetter.pause();
    }
    public void onResume(){
    	LocationSetter.resume();	
    }
    
    
    public void launchTouchstoneLogin(View view){
    	Intent i = new Intent(this, PrepopulateActivity.class);
    	startActivityForResult(i, REQUEST_MOIRA);
    }
    public void launchSkyhook(View view){
    }
    public void launchPtolemyMap(View view){
    	Intent i = new Intent(this, PtolemyMapActivity.class);
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