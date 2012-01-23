package edu.mit.pt.bookmarks;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.TextView;
import edu.mit.pt.R;
import edu.mit.pt.classes.MITClassTable;
import edu.mit.pt.data.Place;

public class TitleAutoCompleteTextView extends AutoCompleteTextView {

	Context ctx;
	SQLiteDatabase db;
	AddBookmarkActivity activity;

	public TitleAutoCompleteTextView(Context context) {
		super(context);
		this.ctx = context;
	}

	public TitleAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.ctx = context;
	}

	public TitleAutoCompleteTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.ctx = context;
	}

	void setup(SQLiteDatabase db, final AddBookmarkActivity activity) {
		CourseAdapter adapter = new CourseAdapter(ctx, null);
		this.setAdapter(adapter);
		this.db = db;
		this.activity = activity;
		this.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				activity.changeType(BookmarkType.LECTURE, false);

				Cursor c = (Cursor) parent.getItemAtPosition(position);
				String room = c.getString(c
						.getColumnIndex(MITClassTable.COLUMN_ROOM));
				Place place = Place.getClassroom(activity, room);
				activity.setPlace(place, false);
				InputMethodManager imm = (InputMethodManager) activity
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindowToken(), 0);
			}
		});
	}

	private class CourseAdapter extends CursorAdapter {

		public CourseAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			String mitId = cursor.getString(cursor
					.getColumnIndex(MITClassTable.COLUMN_MITID));
			
			String resolve = cursor.getString(cursor.getColumnIndex(MITClassTable.COLUMN_RESOLVE));
			resolve = resolve.equals("") ? "" : resolve+" - ";
			
			String fullName = cursor.getString(cursor
					.getColumnIndex(MITClassTable.COLUMN_NAME));
			TextView text = (TextView) view.findViewById(R.id.autocompleteText);
			text.setText(mitId + " (" + resolve + fullName + ")");
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater
					.inflate(R.layout.bookmark_text_autocomplete, null);
			return v;
		}

		@Override
		public CharSequence convertToString(Cursor cursor) {
			return cursor.getString(cursor
					.getColumnIndex(MITClassTable.COLUMN_MITID));
		}

		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			String filter = "";
			if (constraint != null) {
				filter = constraint.toString();
			}

			Cursor c = db.query(MITClassTable.CLASSES_TABLE_NAME, new String[] {
					MITClassTable.COLUMN_ID, MITClassTable.COLUMN_MITID,
					MITClassTable.COLUMN_NAME, MITClassTable.COLUMN_ROOM, MITClassTable.COLUMN_RESOLVE},
					MITClassTable.COLUMN_MITID + " LIKE ? || '%'",
					new String[] { filter }, null, null,
					MITClassTable.COLUMN_MITID, "20");
			return c;
		}

	}

}
