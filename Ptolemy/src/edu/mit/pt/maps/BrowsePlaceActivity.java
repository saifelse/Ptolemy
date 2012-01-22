package edu.mit.pt.maps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.maps.MapActivity;

import edu.mit.pt.ActionBar;
import edu.mit.pt.R;
import edu.mit.pt.bookmarks.AddBookmarkActivity;
import edu.mit.pt.data.Classroom;
import edu.mit.pt.data.Place;

public class BrowsePlaceActivity extends MapActivity {

	private final String ACTIVITY_TITLE = "Pick a location";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_browse);

		ActionBar.setTitle(this, ACTIVITY_TITLE);
		ActionBar.setDefaultBackAction(this);

		ImageButton searchButton = (ImageButton) getLayoutInflater().inflate(
				R.layout.menu_nav_button, null);
		searchButton.setImageResource(R.drawable.ic_menu_search);
		searchButton.setContentDescription(getString(R.string.search));
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onSearchRequested();
			}
		});
		
		searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Thomas how did you do this lol.
				onSearchRequested();
			}
		});

		ActionBar.setButton(this, searchButton);
		
		String customName = getIntent().getStringExtra(AddBookmarkActivity.CUSTOM_NAME);
		if (customName.length() != 0) {
			Button nameButton = (Button) findViewById(R.id.backToAddTitle);
			nameButton.setText(customName);
		}
		
		findViewById(R.id.backToAddTitle).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO: replace with actually getting a place.
				setPlace(new Classroom(0, "6-120", 42000000, -71000000));
				finish();
			}
		});
	}
	
	void setPlace(Place p) {
		Intent data = new Intent();
		data.putExtra(AddBookmarkActivity.PLACE, p);
		setResult(RESULT_OK, data);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
