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
	
	//singleton
	static WifiLocation wifiLocation = null;
	public static WifiLocation getInstance(Context context) {
		if (wifiLocation == null)
			wifiLocation = new WifiLocation(context);
		return wifiLocation;
		
	}
	
	private WifiLocation(Context context) {
		this.context = context;
		this.wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
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
		System.out.println("WOOOOO");
		LocationSetter.setLocation(getLocation());
	}

	@SuppressWarnings("unchecked")
	public GeoPoint getLocation() {
		SQLiteDatabase db = PtolemyDBOpenHelperSingleton.getPtolemyDBOpenHelper(this.context).getReadableDatabase();
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
		ScanResult closestAP = results.get(0);
		String bssid0 = closestAP.BSSID.substring(0, closestAP.BSSID.length() - 1) + '0';
		wifi.startScan();
		return AP.getAPLocation(bssid0, db);
//		for (ScanResult r: results) {
//			//System.out.println(r.BSSID);
//			//System.out.println(r.level);
//			System.out.println(r.BSSID.substring(0, r.BSSID.length() - 1));
//			String bssid0 = r.BSSID.substring(0, r.BSSID.length() - 1) + '0';
//			System.out.println(bssid0);
//			String location = AP.getAPLocation(bssid0, db);
//			output = output + location + "\n";
//		}
		//db.close();
		
	}
	
	
}
