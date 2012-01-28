package edu.mit.pt.tutorial;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import edu.mit.pt.R;

public class TourMapActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tour_map);
		
		Typeface tf = Typeface.createFromAsset(getAssets(),
                "coolvetica.ttf");
		TextView tv = (TextView) findViewById(R.id.map_title);
        tv.setTypeface(tf);
	}
	
	public void continueTour(View v) {
		setResult(RESULT_OK);
		finish();
	}

}
