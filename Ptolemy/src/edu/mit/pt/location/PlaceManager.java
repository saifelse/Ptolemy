package edu.mit.pt.location;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.android.maps.GeoPoint;
import android.content.Context;
import edu.mit.pt.data.Place;


public class PlaceManager {
	public static int LAT_TILE_SPAN = 400;
	public static int LON_TILE_SPAN = 400;
	public static int CACHE_SIZE = 15;
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
	public List<Place> getPlaces(GeoPoint topLeft, GeoPoint bottomRight,
			int floor) {
		
		int tileYMax = latToTileY(topLeft.getLatitudeE6());
		int tileYMin = latToTileY(bottomRight.getLatitudeE6());
		int tileXMin = lonToTileX(topLeft.getLongitudeE6());
		int tileXMax = lonToTileX(bottomRight.getLongitudeE6());

		List<Place> result = new ArrayList<Place>();
		for (int x = tileXMin; x <= tileXMax; x++) {
			for (int y = tileYMin; y <= tileYMax; y++) {
				result.addAll(getPlaces(x, y, floor));
			}
		}
		return result;
	}
	
	
	public List<Place> getPlaces(GeoPoint topLeft, GeoPoint bottomRight) {
		
		int tileYMax = latToTileY(topLeft.getLatitudeE6());
		int tileYMin = latToTileY(bottomRight.getLatitudeE6());
		int tileXMin = lonToTileX(topLeft.getLongitudeE6());
		int tileXMax = lonToTileX(bottomRight.getLongitudeE6());
		
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
		return (int)Math.floor((double)lat / LAT_TILE_SPAN);
	}

	public static int lonToTileX(int lon) {
		return (int)Math.floor((double)lon / LON_TILE_SPAN);
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
