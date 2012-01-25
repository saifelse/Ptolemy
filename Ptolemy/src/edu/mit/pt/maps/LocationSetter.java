package edu.mit.pt.maps;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.skyhookwireless.wps.RegistrationCallback;
import com.skyhookwireless.wps.WPSAuthentication;
import com.skyhookwireless.wps.WPSContinuation;
import com.skyhookwireless.wps.WPSLocation;
import com.skyhookwireless.wps.WPSPeriodicLocationCallback;
import com.skyhookwireless.wps.WPSReturnCode;
import com.skyhookwireless.wps.XPS;

import edu.mit.pt.Config;
import edu.mit.pt.location.WifiLocation;

public class LocationSetter {
	// Available data
	private static double latitude;
	private static double longitude;
	private static double altitude;
	private static double bearing;
	
	private static XPSOverlay overlay;
	
	// Bearings
	private static SensorManager sman;
	private static SensorEventListener compassListener;
	private static Sensor accelerometerSensor;
	private static Sensor magneticFieldSensor;
	
	// Location
	private final static int MAX_XPS_DELAY = 10;
	private static XPS xps;
	private static WPSAuthentication auth;
	private static Handler updateLocationHandler;
	private static boolean isStopped;
	
	public static double getLatitude(){
		return latitude;
	}
	public static double getLongitude(){
		return longitude;
	}
	public static double getAltitude(){
		return altitude;
	}
	public static GeoPoint getPoint(Context context){
		WifiLocation wifiLocation = WifiLocation.getInstance(context);
		return wifiLocation.getLocation();
		//return new GeoPoint((int)(latitude*1e6),(int)(longitude*1e6));
	}
	public static void init(Context context, String username, String realm, XPSOverlay o){
		overlay = o;
		updateLocationHandler = new Handler();
		initLocation(context, username, realm);
		initBearing(context);
	}
	public static void pause(){
		//pauseLocation();
		pauseBearing();
	}
	public static void resume(){
		//resumeLocation();
		resumeBearing();
	}
	public static void stop(){
		pause();
		xps.abort();
	}

	private static void initLocation(Context context, String username,
			String realm) {
		
		isStopped = true;
		// Authenticate
		auth = new WPSAuthentication(username, realm);
		xps = new XPS(context);
		xps.registerUser(auth, null, new RegistrationCallback() {
			public void done() {
				Log.v(Config.TAG + "_xps", "Done with registration.");
			}

			public WPSContinuation handleError(WPSReturnCode error) {
				// Retry if auth failure
				Log.v(Config.TAG + "_xps", "Auth fail, trying again.");
				return isStopped ? WPSContinuation.WPS_STOP
						: WPSContinuation.WPS_CONTINUE;
			}

			public void handleSuccess() {
				Log.v(Config.TAG + "_xps", "Auth success.");
			}
		});
	}
	
	private static void pauseLocation() {
		isStopped = true;
	}
	private static void resumeLocation() {
		Log.v(Config.TAG+"_xps","Resuming location...");
		//if(!isStopped) return;
		//isStopped = false;
		Log.v(Config.TAG+"_xps","Registering callback");
		xps.getXPSLocation(auth, MAX_XPS_DELAY, XPS.EXACT_ACCURACY,
				new WPSPeriodicLocationCallback() {
					public void done() {
						Log.v(Config.TAG + "_xps", "Done with location?");
					}

					public WPSContinuation handleError(WPSReturnCode error) {
						Log.v(Config.TAG + "_xps",
								"Location error... trying again.");
						
						return WPSContinuation.WPS_CONTINUE;
						//return isStopped ? WPSContinuation.WPS_STOP
						//		: WPSContinuation.WPS_CONTINUE;
					}

					public WPSContinuation handleWPSPeriodicLocation(
							final WPSLocation loc) {
						//Log.v(Config.TAG + "_xps", loc.getLatitude()+", "+loc.getLongitude());
						handleLocation(loc.getLatitude(),
								loc.getLongitude(), loc.getAltitude());
						/*
						updateLocationHandler.post(new Runnable() {
							public void run() {
								handleLocation(loc.getLatitude(),
										loc.getLongitude(), loc.getAltitude());
							}
						});*/
						return WPSContinuation.WPS_CONTINUE;
					}

				});
	}
	
	private static void initBearing(Context context){
		sman = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		accelerometerSensor = sman.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magneticFieldSensor = sman.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		compassListener = new SensorEventListener() {
			private float[] accData;
			private float[] magData;

			public void onSensorChanged(SensorEvent event) {
				switch (event.sensor.getType()) {
				case Sensor.TYPE_ACCELEROMETER:
					accData = event.values;
					break;
				case Sensor.TYPE_MAGNETIC_FIELD:
					magData = event.values;
					break;
				}
				if (accData != null && magData != null) {
					float R[] = new float[9];
					float I[] = new float[9];
					boolean success = SensorManager.getRotationMatrix(R, I,
							accData, magData);
					if (success) {
						float orientation[] = new float[3];
						SensorManager.getOrientation(R, orientation);
						handleBearing(orientation[0] * 180.0 / Math.PI);
					}
				}
			}
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
	}
	
	
	private static void pauseBearing() {
		// Unregister listeners
		sman.unregisterListener(compassListener, magneticFieldSensor);
		sman.unregisterListener(compassListener, accelerometerSensor);
	}
	
	private static void resumeBearing(){
		// Register listeners
		sman.registerListener(compassListener, magneticFieldSensor,
				SensorManager.SENSOR_DELAY_FASTEST);
		sman.registerListener(compassListener, accelerometerSensor,
				SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	public static void setLocation(GeoPoint p) {
		overlay.setLocation(p);
	}
	
	protected static void handleLocation(double lat, double lng, double alt){
		latitude = lat;
		longitude = lng;
		altitude = alt;
		
		GeoPoint p = new GeoPoint((int)(lat*1e6),(int)(lng*1e6));
		overlay.setLocation(p);
	}
	protected static void handleBearing(double bng){
		bearing = bng;
		overlay.setBearing(bearing);
	}
}