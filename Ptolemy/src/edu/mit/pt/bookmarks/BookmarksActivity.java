package edu.mit.pt.bookmarks;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import edu.mit.pt.ActionBar;
import edu.mit.pt.R;

public class BookmarksActivity extends ListActivity {
	
	ArrayAdapter<Bookmark> adapter;
	private final String ACTIVITY_TILE = "Bookmarks";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmarks);

		ActionBar.setTitle(this, ACTIVITY_TILE);
		ActionBar.setDefaultBackAction(this);
		
		final Activity that = this;

		// Add nav button.
		ImageButton addButton = (ImageButton) getLayoutInflater().inflate(
				R.layout.menu_nav_button, null);
		addButton.setImageResource(R.drawable.ic_menu_bookmark_add);
		addButton.setContentDescription(getString(R.string.add_bookmark));
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(that, AddBookmarkActivity.class);
				startActivity(intent);
			}
		});
		ActionBar.setButton(this, addButton);

		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
			}
		});

		adapter = new ArrayAdapter<Bookmark>(this,
				R.layout.bookmark_list_item) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = getLayoutInflater().inflate(
							R.layout.bookmark_list_item, null);
				}
				TextView nameText = (TextView) convertView
						.findViewById(R.id.name);
				nameText.setText(getItem(position).getCustomName());

				TextView locationText = (TextView) convertView
						.findViewById(R.id.location);
				locationText.setText(getItem(position).getPlace().getName());
				return convertView;
			}
		};

		setListAdapter(adapter);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		adapter.clear();
		for (Bookmark b : Bookmark.getBookmarks(this)) {
			adapter.add(b);
		}
	}

}
