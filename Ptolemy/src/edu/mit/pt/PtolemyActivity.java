package edu.mit.pt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import edu.mit.pt.maps.PtolemyMapActivity;

public class PtolemyActivity extends Activity {

	static final boolean DEBUG = false;
	static final int DIALOG_ERROR_NETWORK = 0;
	static final int DIALOG_ERROR_JSON = 1;
	static final int DIALOG_ERROR_OTHER = 2;
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
			new Config.FirstRunTask(this, bar, messageView, DEBUG).execute();
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
		Config.clearPreferences(this);
		finish();
	}

	public void launchPtolemyMap(View view) {
		Intent i = new Intent(this, PtolemyMapActivity.class);
		startActivity(i);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false).setNeutralButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						PtolemyActivity.this.finish();
					}
				}).setTitle("Oops...");
		switch (id) {
		case DIALOG_ERROR_NETWORK:
			builder.setMessage("Could not connect to our servers! A connection is necessary in order to build the database.");
			break;
		case DIALOG_ERROR_JSON:
			builder.setMessage("An error occurred while parsing the data. Make sure you're connected to the Internet!");
			break;
		case DIALOG_ERROR_OTHER:
			builder.setMessage("An unexpected error occurred!");
			break;
		default:
			return null;
		}
		return builder.create();
	}

}
