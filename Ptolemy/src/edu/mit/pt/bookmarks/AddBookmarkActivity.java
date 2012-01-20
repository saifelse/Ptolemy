package edu.mit.pt.bookmarks;

import edu.mit.pt.ActionBar;
import edu.mit.pt.R;
import android.app.Activity;
import android.os.Bundle;

public class AddBookmarkActivity extends Activity{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_bookmark);
		ActionBar.setTitle(this, "Add Bookmark");
		ActionBar.setDefaultBackAction(this);
	}
}
