package edu.mit.pt.tutorial;

import edu.mit.pt.R;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class TourItemActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tour_item);
		
		Typeface tf = Typeface.createFromAsset(getAssets(),
                "coolvetica.ttf");
		TextView tv = (TextView) findViewById(R.id.item);
        tv.setTypeface(tf);
	}

}
