package edu.mit.pt;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import edu.mit.pt.classes.MITClass;

/**
 * 
 * TODO: Make it choose the term.
 * FIXME: fix naming conventions for ids to underscores.
 * FIXME: can't auth again after already auth'ing
 */
public class PrepopulateActivity extends Activity {
	private static String term = "fa11";
	private final int MOIRA_ERROR = 0;
	public final static String CLASSES = "classes";

	private ProgressDialog dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prepopulate);

		ActionBar.setDefaultBackAction(this);
		ActionBar.setTitle(this, "Import Classes");
	}

	public void loginTouchstone(View view) {
		// Fields
		EditText usernameField = (EditText) findViewById(R.id.EditUserName);
		EditText passwordField = (EditText) findViewById(R.id.EditPassword);

		// Get data
		String username = usernameField.getText().toString();
		String password = passwordField.getText().toString();

		dialog = ProgressDialog.show(this, "", "Connecting. Please wait...",
				true);

		// Attempt login.
		new MoiraTask(this).execute(username, password, term);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case MOIRA_ERROR:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setNeutralButton("OK",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							onSearchRequested();
						}
					})
					.setTitle("Hm...")
					.setMessage(
							"An error occurred trying to connect. Please try again!");
			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	private class MoiraTask extends AsyncTask<String, Integer, List<String>> {
		Activity activity;
		
		public MoiraTask(Activity activity) {
			this.activity = activity;
		}
		
		@Override
		protected List<String> doInBackground(String... credential) {
			String username = credential[0];
			String password = credential[1];
			String term = credential[2];
			try {
				List<String> result = Moira
						.getClasses(username, password, term);
				return result;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
		}

		@Override
		protected void onPostExecute(List<String> classes) {
			// Hide progress bar
			dialog.dismiss();
			if (classes == null) {
				// Show content based on error.
				showDialog(MOIRA_ERROR);
			} else {
				List<Long> mitClasses = new ArrayList<Long>();
				for (String dirtyClassName : classes) {
					String className = dirtyClassName.split("-")[1];
					long classId = MITClass.lookupName(activity, className);
					Log.v(Config.TAG, "Looking up class: " + className);
					if (classId != -1) {
						Log.v(Config.TAG, "Matched class: " + classId);
						mitClasses.add(classId);
					}
				}
				Intent intent = new Intent();
				intent.putExtra(CLASSES, mitClasses.toArray(new Long[0]));
				setResult(RESULT_OK, intent);
				finish();
			}
		}
	}
}
