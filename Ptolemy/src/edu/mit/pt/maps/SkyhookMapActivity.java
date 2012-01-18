package edu.mit.pt.maps;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.skyhookwireless.wps.RegistrationCallback;
import com.skyhookwireless.wps.WPSAuthentication;
import com.skyhookwireless.wps.WPSContinuation;
import com.skyhookwireless.wps.WPSLocation;
import com.skyhookwireless.wps.WPSPeriodicLocationCallback;
import com.skyhookwireless.wps.WPSReturnCode;
import com.skyhookwireless.wps.XPS;

import edu.mit.pt.Config;
import edu.mit.pt.R;

public class SkyhookMapActivity extends MapActivity {
	private final static int MAX_XPS_DELAY = 10;
	private XPS xps;
	private WPSAuthentication auth;
	private Handler updateLocationHandler;
	private XPSOverlay meOverlay;

	/** Called when the activity is first created. */
	// FIXME: onCreate is triggered by screen rotations. xps/auth needs to only
	// happen once.
	// See:
	// http://stackoverflow.com/questions/456211/activity-restart-on-rotation-android/
	// http://developer.android.com/guide/topics/resources/runtime-changes.html#HandlingTheChange
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_skyhook);

		// Handler
		updateLocationHandler = new Handler();

		authenticateSkyhook();
		startLocation();

		// Show user
		MapView stdMapView = (MapView) findViewById(R.id.stdMapView);
		meOverlay = new XPSOverlay(stdMapView);
		stdMapView.getOverlays().add(meOverlay);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void authenticateSkyhook() {
		// Create the authentication object
		// myAndroidContext must be a Context instance

		// Authenticate using personal username/realm.
		// FIXME why aren't these Overrides?
		Log.v(Config.TAG + "_xps", "Trying to authenticate!");
		auth = new WPSAuthentication(getString(R.string.skyhook_username),
				getString(R.string.skyhook_realm));
		xps = new XPS(this);
		xps.registerUser(auth, null, new RegistrationCallback() {
			public void done() {
				Log.v(Config.TAG + "_xps", "Done with registration.");
			}

			public WPSContinuation handleError(WPSReturnCode error) {
				// Retry if auth failure
				Log.v(Config.TAG + "_xps", "Auth fail, trying again.");
				return WPSContinuation.WPS_CONTINUE;
			}

			public void handleSuccess() {
				// Yay we're authed!
				Log.v(Config.TAG + "_xps", "Auth success.");
			}

		});
	}

	public void startLocation() {
		xps.getXPSLocation(auth, MAX_XPS_DELAY, XPS.EXACT_ACCURACY,
				new WPSPeriodicLocationCallback() {
					public void done() {
						Log.v(Config.TAG + "_xps", "Done with location?");
					}

					public WPSContinuation handleError(WPSReturnCode error) {
						Log.v(Config.TAG + "_xps",
								"Location error... trying again.");
						return WPSContinuation.WPS_CONTINUE;
					}

					public WPSContinuation handleWPSPeriodicLocation(
							final WPSLocation loc) {
						updateLocationHandler.post(new Runnable() {
							
							public void run() {
								printLocation(loc.getLatitude(),
										loc.getLongitude(), loc.getAltitude());
							}
						});
						return WPSContinuation.WPS_CONTINUE;
					}

				});
	}

	void printLocation(double latitude, double longitude, double altitude) {
		GeoPoint pt = new GeoPoint((int) (latitude * 1e6),
				(int) (longitude * 1e6));
		meOverlay.setLocation(pt);

		// Animate to point
		MapView stdMapView = (MapView) findViewById(R.id.stdMapView);
		MapController mc = stdMapView.getController();
		mc.animateTo(pt);

		Log.v(Config.TAG + "_xps", "LOCATION: " + latitude + ", " + longitude
				+ ", " + altitude);
	}

}
