package edu.mit.pt;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PrepopulateActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touchstone_login);
    }
    public void loginTouchstone(View whatView){
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
    	statusField.setText("Logging in "+username+":"+password+"@athena.dialup.mit.edu");
    	try {
    	    List<String> classes = Moira.getClasses(username, password);
    	}catch(Exception e){
    		statusField.setText("An error arose.");
    	}
    	
    }
}