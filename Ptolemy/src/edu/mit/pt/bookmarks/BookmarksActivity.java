package edu.mit.pt.bookmarks;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import edu.mit.pt.ActionBar;
import edu.mit.pt.R;

public class BookmarksActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmarks);
		
		ActionBar.setTitle(this, "Bookmarks");
		final Activity that = this;
		ActionBar.setBackAction(this, new Runnable() {
			@Override
			public void run() {
				that.finish();
			}
		});
		
		// Add nav button.
		ImageButton addButton = (ImageButton) getLayoutInflater().inflate(
				R.layout.menu_nav_button, null);
		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		layout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		ActionBar.setButtons(this, new View[] { addButton });

		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
			}
		});

		ArrayAdapter<Bookmark> adapter = new ArrayAdapter<Bookmark>(this,
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
		for (Bookmark b : Bookmark.getBookmarks(this)) {
			adapter.add(b);
		}
	}

}
