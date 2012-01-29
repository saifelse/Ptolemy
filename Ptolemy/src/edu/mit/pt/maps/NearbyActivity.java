package edu.mit.pt.maps;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.mit.pt.ActionBar;
import edu.mit.pt.Config;
import edu.mit.pt.R;
import edu.mit.pt.data.Place;
import edu.mit.pt.data.PlaceType;
import edu.mit.pt.data.PlacesTable;
import edu.mit.pt.data.PtolemyDBOpenHelperSingleton;

public class NearbyActivity extends ListActivity {

	static public final String LAT = "latitude";
	static public final String LON = "longitude";
	static public final String FLOOR = "floor";
	static public final String PLACE = "place";
	static private final int FLOOR_PENALTY = 50; // "meters"
	static private final double RADIUS = 6378100;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(Config.TAG, "STARTING NEARBYACTIVITY");
		Intent intent = getIntent();
		int lat = intent.getIntExtra(LAT, -1);
		int lon = intent.getIntExtra(LON, -1);
		int floor = intent.getIntExtra(FLOOR, -1);
		if (lat == -1 || lon == -1 || floor == -1) {
			finish();
		}
		setContentView(R.layout.nearest);
		List<PlaceDistance> places = findClosestPlaces(lat, lon, floor);
		
		ActionBar.setTitle(this, "Nearby Places");
		ActionBar.setDefaultBackAction(this);

		ListView lv = getListView();
		final ArrayAdapter<PlaceDistance> adapter = new ArrayAdapter<PlaceDistance>(
				this, R.layout.nearest_item) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = getLayoutInflater().inflate(
							R.layout.nearest_item, null);
				}
				PlaceDistance p = getItem(position);
				Resources res = getResources();
				((ImageView) convertView.findViewById(R.id.icon))
						.setBackgroundDrawable(p.place.getMarker(res, false));
				((TextView) convertView.findViewById(R.id.name))
						.setText(p.place.getName());
				return convertView;
			}
		};
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				PlaceDistance np = adapter.getItem(position);
				Intent intent = new Intent();
				intent.putExtra(PLACE, np.place);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		for (PlaceDistance p : places) {
			adapter.add(p);
		}
	}

	private List<PlaceDistance> findClosestPlaces(int myLat, int myLon,
			int myFloor) {
		PriorityQueue<PlaceDistance> athenaQueue = new PriorityQueue<PlaceDistance>(
				3);
		PriorityQueue<PlaceDistance> maleToiletQueue = new PriorityQueue<PlaceDistance>(
				3);
		PriorityQueue<PlaceDistance> femaleToiletQueue = new PriorityQueue<PlaceDistance>(
				3);

		SQLiteDatabase db = PtolemyDBOpenHelperSingleton
				.getPtolemyDBOpenHelper(this).getReadableDatabase();
		Cursor c = db.query(PlacesTable.PLACES_TABLE_NAME, new String[] {
				PlacesTable.COLUMN_ID, PlacesTable.COLUMN_FLOOR,
				PlacesTable.COLUMN_LAT, PlacesTable.COLUMN_LON,
				PlacesTable.COLUMN_TYPE }, PlacesTable.COLUMN_TYPE + "!=?",
				new String[] { PlaceType.CLASSROOM.name() }, null, null, null);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			long id = c.getLong(c.getColumnIndex(PlacesTable.COLUMN_ID));
			int floor = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_FLOOR));
			int lat = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_LAT));
			int lon = c.getInt(c.getColumnIndex(PlacesTable.COLUMN_LON));
			double distance = getDistance(lat, lon, myLat, myLon, floor,
					myFloor);
			PlaceType type = PlaceType.valueOf(c.getString(c
					.getColumnIndex(PlacesTable.COLUMN_TYPE)));
			switch (type) {
			case ATHENA:
				athenaQueue.add(new PlaceDistance(this, id, distance));
				break;
			case MTOILET:
				maleToiletQueue.add(new PlaceDistance(this, id, distance));
				break;
			case FTOILET:
				femaleToiletQueue.add(new PlaceDistance(this, id, distance));
				break;
			case CLASSROOM:
			case FOUNTAIN:
				break;
			}
		}
		c.close();

		PlaceDistance maleToiletTop = null;
		PlaceDistance femaleToiletTop = null;
		PlaceDistance athenaTop = null;

		PriorityQueue<PlaceDistance> finalQueue = new PriorityQueue<PlaceDistance>(
				6);

		Log.v(Config.TAG, "MALE BR: " + maleToiletQueue.size());
		Log.v(Config.TAG, "FEMALE BR: " + femaleToiletQueue.size());
		Log.v(Config.TAG, "ATHENA: " + athenaQueue.size());

		for (int i = 0; i < 3; i++) {
			if ((maleToiletTop = maleToiletQueue.poll()) != null) {
				finalQueue.add(maleToiletTop);
			}
			if ((femaleToiletTop = femaleToiletQueue.poll()) != null) {
				finalQueue.add(femaleToiletTop);
			}
			if ((athenaTop = athenaQueue.poll()) != null) {
				finalQueue.add(athenaTop);
			}
		}

		List<PlaceDistance> out = new ArrayList<PlaceDistance>();
		while (finalQueue.size() > 0) {
			out.add(finalQueue.remove());
		}

		return out;
	}

	private double getDistance(int lat1, int lon1, int lat2, int lon2,
			int floor1, int floor2) {
		double lat1rad = lat1 * Math.PI / (180 * 1000000);
		double lon1rad = lon1 * Math.PI / (180 * 1000000);
		double lat2rad = lat2 * Math.PI / (180 * 1000000);
		double lon2rad = lon2 * Math.PI / (180 * 1000000);
		double x = (lon2rad - lon1rad) * Math.cos((lat1rad + lat2rad) / 2);
		double y = (lat2rad - lat1rad);
		double flatDistance = RADIUS * Math.hypot(x, y);
		return flatDistance + FLOOR_PENALTY * Math.abs(floor2 - floor1);
	}

	private class PlaceDistance implements Comparable<PlaceDistance> {
		long id;
		double distance;
		Place place;

		public PlaceDistance(Context context, long id, double distance) {
			this.id = id;
			this.distance = distance;
			this.place = Place.getPlace(context, id);
		}

		@Override
		public String toString() {
			return "PD<" + this.id + "," + this.distance + ">";
		}

		public int compareTo(PlaceDistance y) {
			if (this.distance < y.distance) {
				return -1;
			} else if (this.distance > y.distance) {
				return 1;
			}
			return (int) (this.id - y.id);
		}

	}

}
