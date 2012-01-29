package edu.mit.pt;

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
 * TODO: Make it choose the term.
 */
public class PrepopulateActivity extends Activity {
	private String term;
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
		
		term = Config.getTerm(this);
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
							"An error occurred trying to connect. Please check your username or password and try again!");
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
				Log.v(Config.TAG +"M", "LOOKING UP CLASSES! (" + classes.size() + ")");
				long[] mitClasses = new long[classes.size()];
				for (int i = 0; i < classes.size(); i++) {
					String dirtyClassName = classes.get(i);
					String className = dirtyClassName.split("-")[1];
					Log.v(Config.TAG +"M", "Looking up class: " + className);
					long classId = MITClass.getIdIfValidRoom(activity, className);
					if (classId != -1) {
						Log.v(Config.TAG +"M", "Matched class: " + classId);
						mitClasses[i] = classId;
					}
				}
				Intent intent = new Intent();
				intent.putExtra(CLASSES, mitClasses);
				setResult(RESULT_OK, intent);
				finish();
			}
		}
	}
}
