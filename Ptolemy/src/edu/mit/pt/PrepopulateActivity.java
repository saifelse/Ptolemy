package edu.mit.pt;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * TODO: Needs to be threaded.
 * TODO: Add a loading bar.
 * TODO: Make it look pretty.
 *
 */
public class PrepopulateActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touchstone_login);
    }
    public void loginTouchstone(View view){
    	// Fields
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
    	
    	// Attempt login.
    	statusField.setText("Logging in as "+username+"@athena.dialup.mit.edu");
    	try {
    	    List<String> classes = Moira.getClasses(username, password, "fa11");
    	    setResult(RESULT_OK, new ClassDataIntent(classes));
    	    finish();
    	}catch(Exception e){
    		e.printStackTrace();
    		statusField.setText("An error arose. Try logging in again.");
    		
    		// Allow user to resubmit data.
        	usernameField.setEnabled(true);
        	passwordField.setEnabled(true);
        	loginButton.setEnabled(true);
    	}
    }
}