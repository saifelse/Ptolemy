package edu.mit.pt;

import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * TODO: Make it look pretty. (add a cancel button)
 * TODO: Make it choose the term.
 * FIXME: fix naming conventions for ids to underscores.
 * FIXME: can't auth again after already auth'ing
 */
public class PrepopulateActivity extends Activity {
	private static String term = "fa11";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.touchstone_login);
	}

	public void loginTouchstone(View view) {
		// Fields
		ProgressBar moiraProgressBar = (ProgressBar) findViewById(R.id.moiraProgressBar);
		TextView statusField = (TextView) findViewById(R.id.PrepopulateStatus);
		EditText usernameField = (EditText) findViewById(R.id.EditUserName);
		EditText passwordField = (EditText) findViewById(R.id.EditPassword);
		Button loginButton = (Button) findViewById(R.id.ButtonLogin);

		// Get data
		String username = usernameField.getText().toString();
		String password = passwordField.getText().toString();

		// Disable fields
		usernameField.setEnabled(false);
		passwordField.setEnabled(false);
		loginButton.setEnabled(false);

		// Show progress bar
		moiraProgressBar.setVisibility(ProgressBar.VISIBLE);

		// Attempt login.
		statusField.setText("Logging in as " + username
				+ "@athena.dialup.mit.edu");
		new MoiraTask().execute(username, password, term);
	}

	private class MoiraTask extends AsyncTask<String, Integer, List<String>> {
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
			// Get UI components
			ProgressBar moiraProgressBar = (ProgressBar) findViewById(R.id.moiraProgressBar);
			TextView statusField = (TextView) findViewById(R.id.PrepopulateStatus);
			EditText usernameField = (EditText) findViewById(R.id.EditUserName);
			EditText passwordField = (EditText) findViewById(R.id.EditPassword);
			Button loginButton = (Button) findViewById(R.id.ButtonLogin);

			// Hide progress bar
			moiraProgressBar.setVisibility(ProgressBar.INVISIBLE);
			if (classes == null) {
				// Show content based on error.
				statusField.setText("An error arose. Try logging in again.");
				usernameField.setEnabled(true);
				passwordField.setEnabled(true);
				loginButton.setEnabled(true);
			} else {
				setResult(RESULT_OK, new ClassDataIntent(classes));
				finish();
			}
		}
	}
}
