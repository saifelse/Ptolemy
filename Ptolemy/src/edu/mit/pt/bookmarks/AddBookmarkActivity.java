package edu.mit.pt.bookmarks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.android.maps.MapActivity;

import edu.mit.pt.ActionBar;
import edu.mit.pt.Config;
import edu.mit.pt.R;
import edu.mit.pt.data.PtolemyOpenHelper;

public class AddBookmarkActivity extends MapActivity {

	private BookmarkType type = BookmarkType.OTHER;
	private SQLiteDatabase db;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(Config.TAG, "ADDBOOKMARKACTIVITY CREATE");
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
		TitleAutoCompleteTextView autoComplete = (TitleAutoCompleteTextView) findViewById(R.id.editBookmarkTitle);
		db = new PtolemyOpenHelper(this).getReadableDatabase();
		autoComplete.setup(db);
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
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();
	}
}
