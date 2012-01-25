package edu.mit.pt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import edu.mit.pt.maps.PtolemyMapActivity;

public class PtolemyActivity extends Activity {

	static final boolean DEBUG = true;
	boolean needFirstRun = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		needFirstRun = Config.firstRunCheck(this);
		setContentView(R.layout.first_launch);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (needFirstRun) {
			ProgressBar bar = (ProgressBar) findViewById(R.id.first_launch_progress);
			TextView messageView = (TextView) findViewById(R.id.first_launch_details);
			bar.setVisibility(View.VISIBLE);
			new Config.FirstRunTask(this, bar, messageView, false).execute();
		} else {
			if (DEBUG) {
				setContentView(R.layout.main);
			} else {
				startActivity(new Intent(this, PtolemyMapActivity.class));
				finish();
			}
		}
	}

	public void clearPreferences(View view) {
		SharedPreferences settings = getPreferences(0);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
		finish();
	}

	public void launchFirstRun(View view) {
		new Config.FirstRunTask(this, null, null, true).execute();
	}

	public void launchPtolemyMap(View view) {
		Intent i = new Intent(this, PtolemyMapActivity.class);
		startActivity(i);
	}

}
