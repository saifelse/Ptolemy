package edu.mit.pt.location;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.android.maps.GeoPoint;

import edu.mit.pt.data.PtolemyDBOpenHelperSingleton;
import edu.mit.pt.data.PtolemyOpenHelper;
import edu.mit.pt.maps.LocationSetter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class WifiLocation {
	WifiManager wifi = null;
	Context context;

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
		BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				update();

			}
		};
		context.registerReceiver(broadcastReceiver, intentFilter);
	}

	public void update() {
		LocationSetter.setLocation(getLocation());
	}

	private GeoPoint midGeoPoint(GeoPoint a, GeoPoint b) {
		return new GeoPoint((a.getLatitudeE6() + b.getLatitudeE6()) / 2,
				(a.getLongitudeE6() + b.getLongitudeE6()) / 2);
	}

	private GeoPoint weightedMidGeoPoint(GeoPoint a, int strengtha, GeoPoint b,
			int strengthb) {
		System.out.println(strengtha);
		System.out.println(strengthb);
		int diff = strengtha - strengthb;
		double powersOfTwoDiff = (double) diff / 6.02;
		double ratio = Math.pow(2.0, -powersOfTwoDiff);
		double amountA = Math.pow(2.0, -ratio);
		System.out.println(amountA);
		double amountB = 1 - amountA;
		return new GeoPoint((int) (amountA * a.getLatitudeE6() + amountB
				* b.getLatitudeE6()),
				(int) (amountA * a.getLongitudeE6() + amountB
						* b.getLongitudeE6()));
	}

	private GeoPoint trilaterateGeoPoints(GeoPoint a, int strengtha,
			GeoPoint b, int strengthb, GeoPoint c, int strengthc) {
		return null;
	}

	@SuppressWarnings("unchecked")
	public GeoPoint getLocation() {
		SQLiteDatabase db = PtolemyDBOpenHelperSingleton
				.getPtolemyDBOpenHelper(this.context).getReadableDatabase();
		List<ScanResult> results = wifi.getScanResults();
		Collections.sort(results, new Comparator() {

			public int compare(Object lhs, Object rhs) {
				ScanResult a = (ScanResult) lhs;
				ScanResult b = (ScanResult) rhs;
				return b.level - a.level;
			}

		});
		if (results.size() < 1)
			return null;
		ScanResult closestAP1 = results.get(0);
		String bssid1 = closestAP1.BSSID.substring(0,
				closestAP1.BSSID.length() - 1) + '0';
		int j = 1;
		String bssid2 = null;
		ScanResult closestAP2 = null;
		for (j = 1; j < results.size(); j++) {
			String bssid = results.get(j).BSSID;
			String bssidtmp = bssid.substring(0, bssid.length() - 1) + '0';
			if (!bssidtmp.equals(bssid1)) {
				bssid2 = bssidtmp;
				closestAP2 = results.get(j);
				j++;
				break;
			}
		}
		String bssid3 = null;
		ScanResult closestAP3 = null;
		for (; j < results.size(); j++) {
			String bssid = results.get(j).BSSID;
			String bssidtmp = bssid.substring(0, bssid.length() - 1) + '0';
			if (!bssidtmp.equals(bssid1) && !bssidtmp.equals(bssid2)) {
				bssid3 = bssidtmp;
				closestAP3 = results.get(j);
				j++;
				break;
			}
		}
		wifi.startScan();
		if (bssid2 == null) { // only 1 AP found
			return AP.getAPLocation(bssid1, db);
		} else if (bssid3 == null) { // 2 APs found
			// 2 results found
			GeoPoint location1 = AP.getAPLocation(bssid1, db);
			GeoPoint location2 = AP.getAPLocation(bssid2, db);
			// return midGeoPoint(location1, location2);
			return weightedMidGeoPoint(location1, closestAP1.level, location2,
					closestAP2.level);
		} else {
			// at least 3 APs found
			assert (bssid1 != null) : "BSSID 1 is null";
			assert (bssid2 != null) : "BSSID 2 is null";
			assert (bssid3 != null) : "BSSID 3 is null";
			GeoPoint location1 = AP.getAPLocation(bssid1, db);
			GeoPoint location2 = AP.getAPLocation(bssid2, db);
			GeoPoint location3 = AP.getAPLocation(bssid3, db);
			return trilaterateGeoPoints(location1, closestAP1.level, location2,
					closestAP2.level, location3, closestAP3.level);
		}
		// for (ScanResult r: results) {
		// //System.out.println(r.BSSID);
		// //System.out.println(r.level);
		// System.out.println(r.BSSID.substring(0, r.BSSID.length() - 1));
		// String bssid0 = r.BSSID.substring(0, r.BSSID.length() - 1) + '0';
		// System.out.println(bssid0);
		// String location = AP.getAPLocation(bssid0, db);
		// output = output + location + "\n";
		// }
		// db.close();

	}

}
