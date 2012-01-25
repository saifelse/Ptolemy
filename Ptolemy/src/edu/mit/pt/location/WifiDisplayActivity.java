package edu.mit.pt.location;

import edu.mit.pt.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class WifiDisplayActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_display);
	}
	
	public void updateWifiData(View view) {
		//WifiLocation wifi = new WifiLocation(this);
		EditText e = (EditText) findViewById(R.id.editText1);
		//e.setText(wifi.scanResults());
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
