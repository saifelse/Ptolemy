package edu.mit.pt.location;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

import edu.mit.pt.Config;
import edu.mit.pt.R;
import edu.mit.pt.data.PtolemyDBOpenHelperSingleton;
import edu.mit.pt.maps.LocationSetter;

// FIXME: THIS BREAKS IF WIFI IS OFF.

public class WifiLocation {
	WifiManager wifi = null;
	Context context;
	private boolean shownWifiNag = false;
	private boolean paused = false;
	private boolean rle_nag_shown = false;

	// singleton
	static WifiLocation wifiLocation = null;

	public static WifiLocation getInstance(Context context) {
		if (wifiLocation == null)
			wifiLocation = new WifiLocation(context);
		return wifiLocation;

	}

	private WifiLocation(Context context) {
		this.context = context;
		this.wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction()))
					update();
//				if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
//					Log.v(Config.TAG, "Wifi state changed");
//					if (wifi.getWifiState() == WifiManager.WIFI_STATE_DISABLED)
//						shownWifiNag = false;
//				}

			}
		};
		context.registerReceiver(broadcastReceiver, intentFilter);
	}

	public void pause() {
		paused = true;
	}

	public void resume() {
		paused = false;
	}

	public void update() {
		// TODO: FIXME passing null
		if (!paused)
			LocationSetter.getInstance(context, null)
					.setLocation(getLocation(false));
	}

	private GeoPoint midGeoPoint(GeoPoint a, GeoPoint b) {
		return new GeoPoint((a.getLatitudeE6() + b.getLatitudeE6()) / 2,
				(a.getLongitudeE6() + b.getLongitudeE6()) / 2);
	}

	private GeoPoint weightedMidGeoPoint(GeoPoint a, int strengtha, GeoPoint b,
			int strengthb) {
		int diff = strengtha - strengthb;
		double powersOfTwoDiff = (double) diff / 6.02;
		double ratio = Math.pow(2.0, -powersOfTwoDiff);
		double amountA = Math.pow(2.0, -ratio);
		double amountB = 1 - amountA;
		return new GeoPoint((int) (amountA * a.getLatitudeE6() + amountB
				* b.getLatitudeE6()),
				(int) (amountA * a.getLongitudeE6() + amountB
						* b.getLongitudeE6()));
	}

	private double calcErrorHelper(double A, double a, double B, double b) {
		return A * A * b * b - 2 * A * b * B * a + B * B * a * a;
	}

	private double calcError(double lat, double lon, GeoPoint a,
			double sigMagA, GeoPoint b, double sigMagB, GeoPoint c,
			double sigMagC) {
		double scalefactor = 1 / 1.35;
		double A = Math.sqrt((a.getLatitudeE6() - lat)
				* (a.getLatitudeE6() - lat) + (a.getLongitudeE6() - lon)
				* scalefactor * scalefactor * (a.getLongitudeE6() - lon));
		double B = Math.sqrt((b.getLatitudeE6() - lat)
				* (b.getLatitudeE6() - lat) + (b.getLongitudeE6() - lon)
				* scalefactor * scalefactor * (b.getLongitudeE6() - lon));
		double C = Math.sqrt((c.getLatitudeE6() - lat)
				* (c.getLatitudeE6() - lat) + (c.getLongitudeE6() - lon)
				* scalefactor * scalefactor * (c.getLongitudeE6() - lon));
		return calcErrorHelper(A, sigMagA, B, sigMagB)
				+ calcErrorHelper(A, sigMagA, C, sigMagC)
				+ calcErrorHelper(B, sigMagB, C, sigMagC);

	}

	private GeoPoint trilaterateGeoPoints(GeoPoint a, int strengtha,
			GeoPoint b, int strengthb, GeoPoint c, int strengthc) {
		double scaleConst = 6.02;
		double sigMagA = Math.pow(2.0, -strengtha / scaleConst);
		double sigMagB = Math.pow(2.0, -strengthb / scaleConst);
		double sigMagC = Math.pow(2.0, -strengthc / scaleConst);
		double guessLat = (a.getLatitudeE6() * sigMagA + b.getLatitudeE6() * sigMagB + c
				.getLatitudeE6() * sigMagC) / (sigMagA + sigMagB + sigMagC);
//		double guessLat = (a.getLatitudeE6() + b.getLatitudeE6() + c
//				.getLatitudeE6()) / 3.0;
		double guessLon = (a.getLongitudeE6() * sigMagA + b.getLongitudeE6() * sigMagB + c
				.getLongitudeE6() * sigMagC) / (sigMagA + sigMagB + sigMagC);
//		double guessLon = (a.getLongitudeE6() + b.getLongitudeE6() + c
//				.getLongitudeE6()) / 3.0;
		Log.v(WifiLocation.class.getName(), "sigMagA: " + sigMagA);
		double dLat = 7;
		double dLon = 7;
		for (int i = 0; i < 5; i++) {
			Log.v(WifiLocation.class.getName(), "" + guessLat + ", " + guessLon);
			double error = calcError(guessLat, guessLon, a, sigMagA, b,
					sigMagB, c, sigMagC);
			Log.v(WifiLocation.class.getName(), "Error: " + error);
			double dErrordLatE7 = calcError(guessLat + dLat, guessLon, a,
					sigMagA, b, sigMagB, c, sigMagC) - error;
			Log.v(WifiLocation.class.getName(), "dErrordLatE7: " + dErrordLatE7);
			double dErrordLonE7 = calcError(guessLat, guessLon + dLon, a,
					sigMagA, b, sigMagB, c, sigMagC) - error;
			Log.v(WifiLocation.class.getName(), "dErrordLonE7: " + dErrordLonE7);
			double dErrordLatLonE7 = calcError(guessLat + dLat,
					guessLon + dLon, a, sigMagA, b, sigMagB, c, sigMagC)
					- error;
			Log.v(WifiLocation.class.getName(), "dErrordLatLonE7: "
					+ dErrordLatLonE7);
			guessLat = guessLat - error / dErrordLatE7 * 2e-1;
			guessLon = guessLon - error / dErrordLonE7 * 2e-1;
		}
		return new GeoPoint((int) guessLat, (int) guessLon);
	}

	private String maskBSSID(String bssid) {
		return bssid.substring(0, bssid.length() - 1) + '0';
	}

	private void showWifiNag() {
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(R.string.wifi_nag_title);
		ad.setMessage(R.string.wifi_nag_text);

		ad.setPositiveButton(R.string.wifi_nag_positive, new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				wifi.setWifiEnabled(true);

			}
		});
		ad.setNegativeButton(R.string.wifi_nag_negative, new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(context, "Please turn WiFi on!",
						Toast.LENGTH_LONG).show();

			}
		});
		ad.create().show();
		shownWifiNag = true;
	}

	public APGeoPoint getLocation(boolean clicked) {
		SQLiteDatabase db = PtolemyDBOpenHelperSingleton
				.getPtolemyDBOpenHelper(this.context).getReadableDatabase();
		List<ScanResult> results = wifi.getScanResults();
		if (results == null) {
			if (!shownWifiNag && clicked)
				showWifiNag();
			return null;
		}
		Collections.sort(results, new Comparator<ScanResult>() {

			public int compare(ScanResult a, ScanResult b) {
				return b.level - a.level;
			}

		});
		
		for (ScanResult r: results)
			Log.v(Config.TAG, "Found ap bssid " + r.BSSID);
		
		if (results.size() < 1) // no aps found
			return null;

		ScanResult closestAP1 = null;
		String bssid1 = null;
		APGeoPoint closestAP1Location = null;
		int j = 0;
		for (; j < results.size(); j++) {
			String bssid = maskBSSID(results.get(j).BSSID);
			Log.v(Config.TAG, "Searching for " + bssid);
			APGeoPoint location = AP.getAPLocation(bssid, db);
			if (location != null) { // found good ap
				Log.v(Config.TAG, "Found location for " + bssid);
				closestAP1 = results.get(j);
				closestAP1Location = location;
				bssid1 = bssid;
				j++;
				break;
			}
		}

		if (closestAP1 == null)
			return null;


		ScanResult closestAP2 = null;
		String bssid2 = null;
		APGeoPoint closestAP2Location = null;
		for (; j < results.size(); j++) {
			String bssid = maskBSSID(results.get(j).BSSID);
			APGeoPoint location = AP.getAPLocation(bssid, db);
			if (!bssid.equals(bssid1) && location != null) {
				closestAP2 = results.get(j);
				closestAP2Location = location;
				bssid2 = bssid;
				j++;
				break;
			}
		}
		
		int floor = closestAP1Location.getFloor();
		
		Log.v(Config.TAG, "bssid: " + bssid1);
		if (results.get(0).SSID.equals("EECS-MTL-RLE")) {
			Log.v(Config.TAG, "RLE level: " + closestAP1.level);
			if (closestAP1.level < -44 && !rle_nag_shown && clicked) {
				Toast.makeText(context, "I've detected you are in RLE and because of that, my location sources may be inaccurate", Toast.LENGTH_LONG).show();
				rle_nag_shown = true;
			}
			//RLE
			if (closestAP1.level < -44 && closestAP2 != null) {
				floor = closestAP2Location.getFloor();
			}
		}
		
		Log.v(WifiLocation.class.getName(),"CURRENT FLOOR: " + floor);


		ScanResult closestAP3 = null;
		String bssid3 = null;
		APGeoPoint closestAP3Location = null;
		for (; j < results.size(); j++) {
			String bssid = maskBSSID(results.get(j).BSSID);
			APGeoPoint location = AP.getAPLocation(bssid, db);
			if (!bssid.equals(bssid1) && !bssid.equals(bssid2)
					&& location != null) {
				closestAP3 = results.get(j);
				closestAP3Location = location;
				bssid3 = bssid;
				j++;
				break;
			}
		}
		wifi.startScan();
		// GeoPoint W20d100 = new GeoPoint(42358824, -71094653);
		// GeoPoint W20d101 = new GeoPoint(42358832, -71095082);
		// GeoPoint W20d102 = new GeoPoint(42358974, -71094955);
		// GeoPoint W20d106 = new GeoPoint(42359079, -71094361);
		//
		// return trilaterateGeoPoints(W20d100, -60, W20d101, -50, W20d102,
		// -50);

		if (closestAP2 == null) { // only 1 AP found
			return closestAP1Location;
		} else if (closestAP3 == null) { // 2 APs found
			// 2 results found
			return new APGeoPoint(weightedMidGeoPoint(closestAP1Location,
					closestAP1.level, closestAP2Location, closestAP2.level),
					floor);
		} else {
			// at least 3 APs found
			assert (bssid1 != null) : "BSSID 1 is null";
			assert (bssid2 != null) : "BSSID 2 is null";
			assert (bssid3 != null) : "BSSID 3 is null";
			return new APGeoPoint(trilaterateGeoPoints(closestAP1Location,
					closestAP1.level, closestAP2Location, closestAP2.level,
					closestAP3Location, closestAP3.level), floor);
		}

	}
}
