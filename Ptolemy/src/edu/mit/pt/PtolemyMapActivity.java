package edu.mit.pt;

import com.google.android.maps.MapActivity;

import android.app.Activity;
import android.os.Bundle;

public class PtolemyMapActivity extends MapActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_main);
    }
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}