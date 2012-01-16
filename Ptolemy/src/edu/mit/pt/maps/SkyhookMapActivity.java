package edu.mit.pt.maps;

import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.MapActivity;
import com.skyhookwireless.wps.WPS;
import com.skyhookwireless.wps.WPSAuthentication;
import com.skyhookwireless.wps.WPSContinuation;
import com.skyhookwireless.wps.WPSLocation;
import com.skyhookwireless.wps.WPSLocationCallback;
import com.skyhookwireless.wps.WPSReturnCode;
import com.skyhookwireless.wps.WPSStreetAddressLookup;

import edu.mit.pt.Config;
import edu.mit.pt.R;

public class SkyhookMapActivity extends MapActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_skyhook);
		
		Log.v(Config.TAG, "SMA: Let's authenticate!");
		authenticateSkyhook();
		Log.v(Config.TAG, "SMA: Ran authenticate");
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	public void authenticateSkyhook(){
		// Create the authentication object
		// myAndroidContext must be a Context instance
		WPS wps = new WPS(this);
		WPSAuthentication auth = new WPSAuthentication(getString(R.string.skyhook_username), getString(R.string.skyhook_realm));
		
		// Callback object
		WPSLocationCallback callback = new WPSLocationCallback()
		{
			// What the application should do after it's done
			public void done()
			{
				Log.v(Config.TAG, "WPS: Donezo.");
				// after done() returns, you can make more WPS calls.
			}

			// What the application should do if an error occurs
			public WPSContinuation handleError(WPSReturnCode error)
			{
				handleWPSError(error); // you'll implement handleWPSError()

				// To retry the location call on error use WPS_CONTINUE,
				// otherwise return WPS_STOP
				return WPSContinuation.WPS_STOP;
			}

			private void handleWPSError(WPSReturnCode error) {
				Log.v(Config.TAG, "WPS ERROR :"+error);
			}
			// Implements the actions using the location object
			public void handleWPSLocation(WPSLocation location)
			{
				// you'll implement printLocation()
				printLocation(location.getLatitude(), location.getLongitude());
			}
		};
		// Call the location function with callback
		wps.getLocation(auth,
		                WPSStreetAddressLookup.WPS_NO_STREET_ADDRESS_LOOKUP,
		                callback);
		
	}
	protected void printLocation(double latitude, double longitude) {
		Log.v(Config.TAG, "WPS: "+latitude+", "+longitude);
	}
	
	
	
}