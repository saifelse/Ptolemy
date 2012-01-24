package edu.mit.pt.widgets;

import com.google.android.maps.MapActivity;

import edu.mit.pt.R;
import android.os.Bundle;

public class SeekBarTestActivity extends MapActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seekbar_test);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
