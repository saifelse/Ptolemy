package edu.mit.pt.maps;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import edu.mit.pt.Config;
import edu.mit.pt.R;
import edu.mit.pt.data.Place;
import edu.mit.pt.data.PlaceType;
import edu.mit.pt.data.PlacesTable;
import edu.mit.pt.data.PtolemyDBOpenHelperSingleton;

public class NearbyActivity extends Activity {

	static public final String LAT = "latitude";
	static public final String LON = "longitude";
	static public final String FLOOR = "floor";
	static private final int FLOOR_PENALTY = 20; // "meters"
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
		List<Place> places = findClosestPlaces(lat, lon, floor);
		Log.v(Config.TAG, "PLACES");
		for (Place p : places) {
			Log.v(Config.TAG, "  PLACE: " + p.getName());
		}
	}

	private List<Place> findClosestPlaces(int myLat, int myLon, int myFloor) {
		PriorityQueue<PlaceDistance> athenaQueue = new PriorityQueue<PlaceDistance>(
				5);
		PriorityQueue<PlaceDistance> toiletQueue = new PriorityQueue<PlaceDistance>(
				5);

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
			double distance = getDistance(lat, lon, myLat, myLon, floor, myFloor);
			PlaceType type = PlaceType.valueOf(c.getString(c
					.getColumnIndex(PlacesTable.COLUMN_TYPE)));
			switch (type) {
			case ATHENA:
				athenaQueue.add(new PlaceDistance(id, distance));
				break;
			case MTOILET:
			case FTOILET:
				toiletQueue.add(new PlaceDistance(id, distance));
				break;
			case CLASSROOM:
			case FOUNTAIN:
				break;
			}
		}
		c.close();

		List<Place> out = new ArrayList<Place>();
		PlaceDistance athenaTop = null;
		PlaceDistance toiletTop = null;
		while (athenaQueue.size() > 0 && toiletQueue.size() > 0) {
			if (athenaTop == null) {
				athenaTop = athenaQueue.poll();
			}
			if (toiletTop == null) {
				toiletTop = toiletQueue.poll();
			}
			if (athenaTop.distance < toiletTop.distance) {
				out.add(Place.getPlace(this, athenaTop.id));
				athenaTop = null;
			} else {
				out.add(Place.getPlace(this, toiletTop.id));
				toiletTop = null;
			}
		}
		for (PlaceDistance pd : athenaQueue) {
			out.add(Place.getPlace(this, pd.id));
		}
		for (PlaceDistance pd : toiletQueue) {
			out.add(Place.getPlace(this, pd.id));
		}
		return out;
	}

	private double getDistance(int lat1, int lon1, int lat2, int lon2, int floor1, int floor2) {
		double x = (lon2 - lon1) / 1000000 * Math.cos((lat1 + lat2) / 2000000);
		double y = (lat2 - lat1) / 1000000;
		double flatDistance = RADIUS * Math.hypot(x, y);
		return flatDistance + FLOOR_PENALTY*Math.abs(floor2 - floor1);
	}

	private class PlaceDistance implements Comparable<PlaceDistance> {
		long id;
		double distance;

		public PlaceDistance(long id, double distance) {
			this.id = id;
			this.distance = distance;
		}

		@Override
		public int compareTo(PlaceDistance another) {
			if (this.distance < another.distance) {
				return -1;
			} else if (this.distance > another.distance) {
				return 1;
			}
			return 0;
		}

	}

}
