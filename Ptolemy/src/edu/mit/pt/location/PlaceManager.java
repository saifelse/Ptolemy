package edu.mit.pt.location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.android.maps.GeoPoint;
import android.content.Context;
import android.util.Log;
import edu.mit.pt.Config;
import edu.mit.pt.data.Place;
import edu.mit.pt.data.PlaceType;


public class PlaceManager {
	public static int LAT_TILE_SPAN = 400;
	public static int LON_TILE_SPAN = 400;
	public static int CACHE_SIZE = 15;
	private Context context;
	private Map<String, Map<Integer, List<Place>>> cachedTiles;

	public PlaceManager(Context context) {
		this.context = context;
		cachedTiles = new LinkedHashMap<String, Map<Integer, List<Place>>>(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 5942076442426988803L;

			@Override
			protected boolean removeEldestEntry(Entry<String, Map<Integer,List<Place>>> x){
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
	
	

	public MinMax getMinMax(GeoPoint topLeft, GeoPoint bottomRight) {
		
		int tileYMax = latToTileY(topLeft.getLatitudeE6());
		int tileYMin = latToTileY(bottomRight.getLatitudeE6());
		int tileXMin = lonToTileX(topLeft.getLongitudeE6());
		int tileXMax = lonToTileX(bottomRight.getLongitudeE6());
		
		int min = 0;
		int max = 0;
		for (int x = tileXMin; x <= tileXMax; x++) {
			for (int y = tileYMin; y <= tileYMax; y++) {
				Map<Integer, List<Place>> indivTile = getPlaces(x,y);
				for(Integer k : indivTile.keySet()){
					if(k < min) min = k;
					if(k > max) max = k;
				}
			}
		}
		return new MinMax(min, max);
	}
	public static class MinMax {
		public int min, max;
		public MinMax(int min, int max){
			this.min = min;
			this.max = max;
		}
	}
	
	public Map<Integer,List<Place>> getPlaces(GeoPoint topLeft, GeoPoint bottomRight) {
		
		int tileYMax = latToTileY(topLeft.getLatitudeE6());
		int tileYMin = latToTileY(bottomRight.getLatitudeE6());
		int tileXMin = lonToTileX(topLeft.getLongitudeE6());
		int tileXMax = lonToTileX(bottomRight.getLongitudeE6());
		
		Map<Integer, List<Place>> results = new HashMap<Integer, List<Place>>();
		for (int x = tileXMin; x <= tileXMax; x++) {
			for (int y = tileYMin; y <= tileYMax; y++) {
				Map<Integer, List<Place>> indivTile = getPlaces(x,y);
				for(Entry<Integer, List<Place>> k : indivTile.entrySet()){
					int floor = k.getKey();
					List<Place> places = k.getValue();
					if(!results.containsKey(floor)){
						results.put(floor, new ArrayList<Place>());
					}
					results.get(floor).addAll(places);
				}
			}
		}
		return results;
	}

	private Map<Integer, List<Place>> getPlaces(int x, int y) {
		String h = hash(x, y, 0);
		if (!cachedTiles.containsKey(h)) {
			int latMin = tileYToLat(y);
			int lonMin = tileXToLon(x);
			Map<Integer, List<Place>> computed = Place.getPlaces(context, latMin, latMin+LAT_TILE_SPAN, lonMin, lonMin+LON_TILE_SPAN);
			cachedTiles.put(h, computed);
		}
		return cachedTiles.get(h);
	}
	// TODO use a linkedlist to optimize addAll.
	
	private List<Place> getPlaces(int x, int y, int f) {
		List<Place> result = new ArrayList<Place>();
		
		Map<Integer, List<Place>> unfiltered = getPlaces(x,y);
		
		if(unfiltered.containsKey(f))
			result.addAll(unfiltered.get(f));
		
		if(unfiltered.containsKey(f-1)){
			for(Place p : unfiltered.get(f-1)){
				if(p.getPlaceType() != PlaceType.CLASSROOM){
					result.add(p);
				}
			}
		}
		if(unfiltered.containsKey(f+1)){
			for(Place p : unfiltered.get(f+1)){
				if(p.getPlaceType() != PlaceType.CLASSROOM){
					result.add(p);
				}
			}
		}
		
		/*
		for(Place p : getPlaces(x,y)){
			if(p.getFloor() == f || (p.getFloor() == f-1 || p.getFloor() == f+1) && p.getPlaceType() != PlaceType.CLASSROOM){
				result.add(p);
			}
		}
		*/
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
