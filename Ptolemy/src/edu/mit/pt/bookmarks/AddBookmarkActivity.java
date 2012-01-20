package edu.mit.pt.bookmarks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

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

		Button typeButton = (Button) findViewById(R.id.typeButton);
		final BookmarkType[] types = BookmarkType.values();
		final ArrayAdapter<BookmarkType> adapter = new ArrayAdapter<BookmarkType>(
				this, R.layout.bookmark_type_item, types);

		typeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(v.getContext())
						.setTitle(
								getResources().getString(
										R.string.bookmark_type_prompt))
						.setAdapter(adapter,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										changeType(types[which]);
										dialog.dismiss();
									}
								}).create().show();
			}
		});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
