package edu.mit.pt.bookmarks;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import edu.mit.pt.Config;
import edu.mit.pt.R;
import edu.mit.pt.maps.PtolemyMapActivity;

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
			public void onClick(View v) {
				Intent intent = new Intent(that, AddBookmarkActivity.class);
				startActivity(intent);
			}
		});
		ActionBar.setButton(this, addButton);

		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Bookmark b = (Bookmark) parent.getAdapter().getItem(position);
				Intent intent = new Intent(that, PtolemyMapActivity.class);
				Uri.Builder builder = Uri
						.parse("content://edu.mit.pt.data.placescontentprovider/")
						.buildUpon().path(Long.toString(b.getId()));
				intent.setData(builder.build());
				intent.setAction(Intent.ACTION_SEARCH);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});

		adapter = new ArrayAdapter<Bookmark>(this, R.layout.bookmark_list_item) {
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
				Log.v(Config.TAG, "ITEM: " + getItem(position));
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
