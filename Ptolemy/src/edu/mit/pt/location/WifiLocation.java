package edu.mit.pt.location;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.mit.pt.data.PtolemyOpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class WifiLocation {
	WifiManager wifi;
	Context context;
	
	public WifiLocation(Context context) {
		this.context = context;
		this.wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
	}
	
	@SuppressWarnings("unchecked")
	public String scanResults() {
		String output = "...\n";
		System.out.println("SCANNING WIFI");
		SQLiteDatabase db = new PtolemyOpenHelper(this.context).getReadableDatabase();
		List<ScanResult> results = wifi.getScanResults();
		Collections.sort(results, new Comparator() {

			public int compare(Object lhs, Object rhs) {
				ScanResult a = (ScanResult) lhs;
				ScanResult b = (ScanResult) rhs;
				return b.level - a.level;
			}
			
		});
		for (ScanResult r: results) {
			//System.out.println(r.BSSID);
			//System.out.println(r.level);
			System.out.println(r.BSSID.substring(0, r.BSSID.length() - 1));
			String bssid0 = r.BSSID.substring(0, r.BSSID.length() - 1) + '0';
			System.out.println(bssid0);
			String location = AP.getAPLocation(bssid0, db);
			output = output + location + "\n";
		}
		db.close();
		wifi.startScan();
		return output;
	}
	
	
}
