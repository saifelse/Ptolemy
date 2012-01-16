package edu.mit.pt;

import android.os.Bundle;

import com.google.android.maps.MapActivity;

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