package edu.mit.pt.location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import edu.mit.pt.Config;
import edu.mit.pt.data.Place;
import edu.mit.pt.data.PlacesTable;
import edu.mit.pt.data.PtolemyDBOpenHelperSingleton;

public class PlaceManager {
	public static int LAT_TILE_SPAN = 800;
	public static int LON_TILE_SPAN = 800;
	public static int CACHE_SIZE = 20;
	private Context context;
	private Map<String, List<Place>> cachedTiles;

	public PlaceManager(Context context) {
		this.context = context;
		cachedTiles = new LinkedHashMap<String, List<Place>>(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 5942076442426988803L;

			@Override
			protected boolean removeEldestEntry(Entry<String, List<Place>> x){
				return size() > CACHE_SIZE;
			}
		};
	}

	public List<Place> getPlaces(int top, int left, int latSpan, int lonSpan,
			int floor) {
		if(latSpan == 0) return new ArrayList<Place>();
		// Determine relevant tiles

		int tileYMin = latToTileY(top - latSpan);
		int tileYMax = latToTileY(top);
		int tileXMin = lonToTileX(left);
		int tileXMax = lonToTileX(left + lonSpan);

		Log.v(Config.TAG, "y: "+tileYMin+"-"+tileYMax+", x: "+tileXMin+","+tileXMax);
		
		List<Place> result = new ArrayList<Place>();
		for (int x = tileXMin; x <= tileXMax; x++) {
			for (int y = tileYMin; y <= tileYMax; y++) {
				result.addAll(getPlaces(x, y, floor));
			}
		}
		return result;
	}
	public List<Place> getPlaces(int top, int left, int latSpan, int lonSpan) {
		if(latSpan == 0) return new ArrayList<Place>();
		
		Log.v(Config.TAG, "Span: "+latSpan+", "+lonSpan);
		// Determine relevant tiles

		int tileYMin = latToTileY(top - latSpan);
		int tileYMax = latToTileY(top);
		int tileXMin = lonToTileX(left);
		int tileXMax = lonToTileX(left + lonSpan);

		List<Place> result = new ArrayList<Place>();
		for (int x = tileXMin; x <= tileXMax; x++) {
			for (int y = tileYMin; y <= tileYMax; y++) {
				result.addAll(getPlaces(x, y));
			}
		}
		return result;
	}

	private List<Place> getPlaces(int x, int y) {
		String h = hash(x, y, 0);
		if (!cachedTiles.containsKey(h)) {
			int latMin = tileYToLat(y);
			int lonMin = tileXToLon(x);
			List<Place> computed = Place.getPlaces(context, latMin, latMin+LAT_TILE_SPAN, lonMin, lonMin+LON_TILE_SPAN);
			cachedTiles.put(h, computed);
		}
		return cachedTiles.get(h);
	}
	private List<Place> getPlaces(int x, int y, int f) {
		List<Place> result = new ArrayList<Place>();
		for(Place p : getPlaces(x,y)){
			if(p.getFloor() == f){
				result.add(p);
			}
		}
		return result;
	}

	public static int latToTileY(int lat) {
		return lat / LAT_TILE_SPAN;
	}

	public static int lonToTileX(int lon) {
		return lon / LON_TILE_SPAN;
	}

	public static int tileYToLat(int y) {
		return y * LAT_TILE_SPAN;
	}

	public static int tileXToLon(int x) {
		return x * LON_TILE_SPAN;
	}

	public static String hash(int x, int y, int f) {
		return "x" + x + "y" + y;
	}
}
