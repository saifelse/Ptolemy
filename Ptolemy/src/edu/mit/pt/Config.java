package edu.mit.pt;

import android.app.Activity;

public class Config {
	
	static public final String TAG = "PTOLEMY";
	
	/**
	 * Converts from DP to pixels.
	 */
	static public int getPixelsFromDp(Activity a, float dp) {
		float scale = a.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

}
