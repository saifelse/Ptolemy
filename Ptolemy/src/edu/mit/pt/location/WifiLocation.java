package edu.mit.pt.location;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class WifiLocation {
	WifiManager wifi;
	public WifiLocation(Context context) {
		wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
	}
	
	public void scanResults() {
		List<ScanResult> results = wifi.getScanResults();
		for (ScanResult r: results) {
			System.out.println(r.BSSID);
			System.out.println(r.level);
		}
	}
	
	
}
