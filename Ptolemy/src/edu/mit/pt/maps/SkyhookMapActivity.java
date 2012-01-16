package edu.mit.pt.maps;

import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.MapActivity;
import com.skyhookwireless.wps.RegistrationCallback;
import com.skyhookwireless.wps.WPS;
import com.skyhookwireless.wps.WPSAuthentication;
import com.skyhookwireless.wps.WPSContinuation;
import com.skyhookwireless.wps.WPSLocation;
import com.skyhookwireless.wps.WPSLocationCallback;
import com.skyhookwireless.wps.WPSPeriodicLocationCallback;
import com.skyhookwireless.wps.WPSReturnCode;
import com.skyhookwireless.wps.WPSStreetAddressLookup;
import com.skyhookwireless.wps.XPS;

import edu.mit.pt.Config;
import edu.mit.pt.R;

public class SkyhookMapActivity extends MapActivity {
	final static int MAX_XPS_DELAY = 10; 
	XPS xps;
	WPSAuthentication auth;
	/** Called when the activity is first created. */
	// FIXME: onCreate is triggered by screen rotations. xps/auth needs to only happen once.
	// See: http://stackoverflow.com/questions/456211/activity-restart-on-rotation-android/
	//      http://developer.android.com/guide/topics/resources/runtime-changes.html#HandlingTheChange
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_skyhook);
		
		authenticateSkyhook();
		startLocation();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	public void authenticateSkyhook(){
		// Create the authentication object
		// myAndroidContext must be a Context instance
		
		// Authenticate using personal username/realm.
		Log.v(Config.TAG+"_xps", "Trying to authenticate!");
		auth = new WPSAuthentication(getString(R.string.skyhook_username), getString(R.string.skyhook_realm));
		xps = new XPS(this);
		xps.registerUser(auth, null, new RegistrationCallback(){
			@Override
			public void done() {
				Log.v(Config.TAG+"_xps","Done with registration.");
			}
			@Override
			public WPSContinuation handleError(WPSReturnCode error) {
				// Retry if auth failure
				Log.v(Config.TAG+"_xps","Auth fail, trying again.");
				return WPSContinuation.WPS_CONTINUE;
			}
			@Override
			public void handleSuccess() {
				// Yay we're authed!
				Log.v(Config.TAG+"_xps","Auth success.");
			}
			
		});
	}
	public void startLocation(){
		xps.getXPSLocation(auth, MAX_XPS_DELAY, XPS.EXACT_ACCURACY, new WPSPeriodicLocationCallback(){
			@Override
			public void done() {
				Log.v(Config.TAG+"_xps","Done with location?");
			}
			@Override
			public WPSContinuation handleError(WPSReturnCode error) {
				Log.v(Config.TAG+"_xps","Location error... trying again.");
				return WPSContinuation.WPS_CONTINUE;
			}
			@Override
			public WPSContinuation handleWPSPeriodicLocation(WPSLocation loc) {
				printLocation(loc.getLatitude(),loc.getLongitude(),loc.getAltitude());
				return WPSContinuation.WPS_CONTINUE;
			}
			
		});
	}
	protected void printLocation(double latitude, double longitude, double altitude) {
		Log.v(Config.TAG+"_xps","LOCATION: "+latitude+", "+longitude+", "+altitude);
	}
	
	
	
}