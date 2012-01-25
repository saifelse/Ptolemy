package edu.mit.pt;

import android.app.Activity;
import android.content.SharedPreferences;

public class Config {

	static public final String TAG = "PTOLEMY";
	static private final String FIRST_RUN = "firstRun";

	/**
	 * Converts from DP to pixels.
	 */
	static public int getPixelsFromDp(Activity a, float dp) {
		float scale = a.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	static public boolean firstRunCheck(Activity activity) {
		SharedPreferences settings = activity.getPreferences(0);
		boolean needFirstRun = settings.getBoolean(FIRST_RUN, true);
		if (needFirstRun) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(FIRST_RUN, false);
			editor.commit();
			return true;
		}
		return false;
	}

	static public void doFirstRun(Activity activity) {
		activity.setContentView(R.layout.first_launch);
	}

}
