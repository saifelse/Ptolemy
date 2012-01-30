package edu.mit.pt.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;

import com.google.android.maps.GeoPoint;

public class PlaceManager {
	public static int LAT_TILE_SPAN = 300;
	public static int LON_TILE_SPAN = 400;
	public static int CACHE_SIZE = 60;
	public static int CACHEMINMAX_SIZE = 200;

	private Context context;
	private Set<PlaceType> placeTypeFilter;

	private Map<String, Map<Integer, List<Place>>> cachedTiles;
	private Map<String, MinMax> cachedTilesMinMax;

	public void addFilter(PlaceType p) {
		placeTypeFilter.add(p);
	}

	public void removeFilter(PlaceType p) {
		placeTypeFilter.remove(p);
	}

	public boolean hasFilter(PlaceType p) {
		return placeTypeFilter.contains(p);
	}

	public PlaceManager(Context context) {
		this.context = context;
		placeTypeFilter = new HashSet<PlaceType>();

		cachedTiles = new LinkedHashMap<String, Map<Integer, List<Place>>>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 5942076442426988803L;

			@Override
			protected boolean removeEldestEntry(
					Entry<String, Map<Integer, List<Place>>> x) {
				return size() > CACHE_SIZE;
			}
		};

		cachedTilesMinMax = new LinkedHashMap<String, MinMax>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 5942076442426988803L;

			@Override
			protected boolean removeEldestEntry(Entry<String, MinMax> x) {
				return size() > CACHEMINMAX_SIZE;
			}
		};
	}

	public List<Place> getPlaces(GeoPoint topLeft, GeoPoint bottomRight,
			int floor) {

		int tileYMax = latToTileY(topLeft.getLatitudeE6());
		int tileYMin = latToTileY(bottomRight.getLatitudeE6());
		int tileXMin = lonToTileX(topLeft.getLongitudeE6());
		int tileXMax = lonToTileX(bottomRight.getLongitudeE6());

		// List<Place> result = new LinkedList<Place>();
		List<Place> result = new ArrayList<Place>();
		for (int x = tileXMin; x <= tileXMax; x++) {
			for (int y = tileYMin; y <= tileYMax; y++) {
				for (Place p : getPlaces(x, y, floor)) {
					if (hasFilter(p.getPlaceType())) {
						result.add(p);
					}
				}
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
				MinMax indivTileMinMax = getPlacesMinMax(x, y);
				if (indivTileMinMax.min < min)
					min = indivTileMinMax.min;
				if (indivTileMinMax.max > max)
					max = indivTileMinMax.max;
			}
		}
		return new MinMax(min, max);
	}

	public static class MinMax {
		public int min, max;

		public MinMax(int min, int max) {
			this.min = min;
			this.max = max;
		}
	}

	public Map<Integer, List<Place>> getPlaces(GeoPoint topLeft,
			GeoPoint bottomRight) {

		int tileYMax = latToTileY(topLeft.getLatitudeE6());
		int tileYMin = latToTileY(bottomRight.getLatitudeE6());
		int tileXMin = lonToTileX(topLeft.getLongitudeE6());
		int tileXMax = lonToTileX(bottomRight.getLongitudeE6());

		Map<Integer, List<Place>> results = new HashMap<Integer, List<Place>>();
		for (int x = tileXMin; x <= tileXMax; x++) {
			for (int y = tileYMin; y <= tileYMax; y++) {
				Map<Integer, List<Place>> indivTile = getPlaces(x, y);
				for (Entry<Integer, List<Place>> k : indivTile.entrySet()) {
					int floor = k.getKey();
					List<Place> places = k.getValue();
					if (!results.containsKey(floor)) {
						// results.put(floor, new LinkedList<Place>());
						results.put(floor, new ArrayList<Place>());
					}
					// ((LinkedList<Place>)results.get(floor)).addAll((LinkedList<Place>)places);
					results.get(floor).addAll(places);

				}
			}
		}
		return results;
	}

	private synchronized MinMax getPlacesMinMax(int x, int y) {
		String h = hash(x, y, 0);
		if (!cachedTilesMinMax.containsKey(h)) {
			MinMax minMax = new MinMax(1, 1);
			Map<Integer, List<Place>> indivTile = getPlaces(x, y);
			for (Integer k : indivTile.keySet()) {
				if (k < minMax.min)
					minMax.min = k;
				if (k > minMax.max)
					minMax.max = k;
			}
			cachedTilesMinMax.put(h, minMax);
			return minMax;
		} else {
			return cachedTilesMinMax.get(h);
		}
	}

	private synchronized Map<Integer, List<Place>> getPlaces(int x, int y) {
		String h = hash(x, y, 0);
		if (!cachedTiles.containsKey(h)) {
			int latMin = tileYToLat(y);
			int lonMin = tileXToLon(x);
			Map<Integer, List<Place>> computed = Place.getPlaces(context,
					latMin, latMin + LAT_TILE_SPAN, lonMin, lonMin
							+ LON_TILE_SPAN);
			cachedTiles.put(h, computed);
		}
		return cachedTiles.get(h);
	}
	
	private List<Place> getPlaces(int x, int y, int f) {
		// LinkedList<Place> result = new LinkedList<Place>();
		ArrayList<Place> result = new ArrayList<Place>();
		
		Map<Integer, List<Place>> unfiltered = getPlaces(x, y);

		Integer max = null;
		for (Integer k : unfiltered.keySet()) {
			if (max == null || k > max) {
				max = k;
			}
		}
		if (max != null && max < f){
			// result.addAll((LinkedList<Place>)unfiltered.get(max));
			result.addAll(unfiltered.get(max));
		}

		if (unfiltered.containsKey(f)){
			// result.addAll((LinkedList<Place>)unfiltered.get(f));
			result.addAll(unfiltered.get(f));
		}
		/*
		 * for(Place p : getPlaces(x,y)){ if(p.getFloor() == f || (p.getFloor()
		 * == f-1 || p.getFloor() == f+1) && p.getPlaceType() !=
		 * PlaceType.CLASSROOM){ result.add(p); } }
		 */
		return result;
	}

	public static int latToTileY(int lat) {
		return (int) Math.floor((double) lat / LAT_TILE_SPAN);
	}

	public static int lonToTileX(int lon) {
		return (int) Math.floor((double) lon / LON_TILE_SPAN);
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
