package edu.mit.pt.bookmarks;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import edu.mit.pt.R;

public class EditBookmarkActivity extends AddBookmarkActivity {
	
	long bookmarkId;

	@Override
	protected String getNavTitle() {
		return "Edit Bookmark";
	}

	@Override
	protected void completeSetup() {
		bookmarkId = getIntent().getLongExtra(
				BookmarksActivity.BOOKMARK_ID, -1);
		if (bookmarkId == -1) {
			return;
		}
		Bookmark bookmark = Bookmark.getBookmark(this, bookmarkId);
		TextView autoComplete = (TextView) findViewById(R.id.editBookmarkTitle);
		autoComplete.setText(bookmark.getCustomName());
		changeType(bookmark.getType(), false);
		setPlace(bookmark.getPlace(), false);
		Button saveButton = (Button) findViewById(R.id.addBookmarkButton);
		saveButton.setText(getResources().getString(R.string.save_bookmark));
	}
	
	@Override
	public void addBookmark(View v) {
		TextView autoComplete = (TextView) findViewById(R.id.editBookmarkTitle);
		String customName = autoComplete.getText().toString();
		if (place == null || customName.length() == 0) {
			return;
		}
		Bookmark.updateBookmark(this, bookmarkId, customName, place, type);
		finish();
	}

}