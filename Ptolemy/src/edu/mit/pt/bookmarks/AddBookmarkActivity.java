package edu.mit.pt.bookmarks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.MapActivity;

import edu.mit.pt.ActionBar;
import edu.mit.pt.R;

public class AddBookmarkActivity extends MapActivity {

	private BookmarkType type = BookmarkType.OTHER;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_bookmark);
		ActionBar.setTitle(this, "Add Bookmark");
		ActionBar.setDefaultBackAction(this);

		// Configure button to show list of types to choose from.
		Button typeButton = (Button) findViewById(R.id.typeButton);
		final BookmarkType[] types = BookmarkType.values();
		final ArrayAdapter<BookmarkType> adapter = new ArrayAdapter<BookmarkType>(
				this, R.layout.bookmark_type_item, types);

		typeButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				new AlertDialog.Builder(v.getContext())
						.setTitle(
								getResources().getString(
										R.string.bookmark_type_prompt))
						.setAdapter(adapter,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										changeType(types[which]);
										dialog.dismiss();
									}
								}).create().show();
			}
		});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Autocomplete on title.
		TextView titleView = (TextView) findViewById(R.id.editBookmarkTitle);
		titleView.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Toast toast = Toast.makeText(v.getContext(), "" + keyCode, 2000);
				toast.show();
				return false;
			}
		});
	}
	
	

	private void changeType(BookmarkType newType) {
		type = newType;
		Button typeButton = (Button) findViewById(R.id.typeButton);
		typeButton.setText(type.getShortName());
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
