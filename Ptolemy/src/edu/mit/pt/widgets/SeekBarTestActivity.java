package edu.mit.pt.widgets;

import com.google.android.maps.MapActivity;

import edu.mit.pt.Config;
import edu.mit.pt.R;
import edu.mit.pt.maps.PtolemyMapView;
import edu.mit.pt.widgets.FloorSeekBar.FloorSeekEvent;
import edu.mit.pt.widgets.FloorSeekBar.OnFloorSelectListener;
import android.os.Bundle;
import android.util.Log;

public class SeekBarTestActivity extends MapActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seekbar_test);
		FloorSeekBar seeker = (FloorSeekBar) findViewById(R.id.FloorTest);
		seeker.addFloorListener(new OnFloorSelectListener(){
			@Override
			public void onFloorSelect(FloorSeekEvent event) {
				Log.v(Config.TAG, "New floor is: "+event.getFloor());
			}
		});
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
