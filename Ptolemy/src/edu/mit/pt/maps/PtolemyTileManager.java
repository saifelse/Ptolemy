package edu.mit.pt.maps;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import edu.mit.pt.Config;

public class PtolemyTileManager {

	private Context ctx;
	private SoftBitmapCache softBitmapCache;
	private StrongBitmapCache strongBitmapCache;
	private Map<String, Boolean> notMapped;

	public PtolemyTileManager(Context context) {
		ctx = context;
		softBitmapCache = new SoftBitmapCache();
		strongBitmapCache = StrongBitmapCache.getInstance();
		notMapped = new HashMap<String, Boolean>();
	}

	public boolean isNotMapped(int tileCol, int tileRow) {
		return notMapped.containsKey(hashKey(tileCol, tileRow));
	}

	public Bitmap getBitmap(int tileCol, int tileRow) {
		String bitmapKey = hashKey(tileCol, tileRow);
		Bitmap bitmap = strongBitmapCache.get(bitmapKey);

		// Try the strong cache.
		if (bitmap != null) {
			// Refreshes "last-used" order of bitmap.
			strongBitmapCache.remove(bitmapKey);
			strongBitmapCache.put(bitmapKey, bitmap);
			return bitmap;
		}

		// Try the soft cache.
		SoftReference<Bitmap> bitmapRef = softBitmapCache.get(bitmapKey);
		if (bitmapRef != null) {
			bitmap = bitmapRef.get();
			if (bitmap != null) {
				strongBitmapCache.put(bitmapKey, bitmap);
			}
			return bitmap;
		}

		// Load from a file.
		Log.v(Config.TAG, "Missed cache, loading bitmap file.");
		Resources resources = ctx.getResources();
		int bmId = resources
				.getIdentifier(bitmapKey, "drawable", "edu.mit.pt");
		Log.v(Config.TAG, "Trying to load: " + bitmapKey);
		if (bmId == 0) {
			Log.v(Config.TAG, bitmapKey + " not found.");
			notMapped.put(bitmapKey, true);
			return null;
		}

		Log.v(Config.TAG, bitmapKey + " found!");
		Bitmap bm = BitmapFactory.decodeResource(resources, bmId);
		softBitmapCache.put(bitmapKey, new SoftReference<Bitmap>(bm));
		strongBitmapCache.put(bitmapKey, bm);
		return bm;
	}

	private String hashKey(int tileCol, int tileRow) {
		return "x" + tileCol + "y" + tileRow;
	}

	private static class SoftBitmapCache extends
			LinkedHashMap<String, SoftReference<Bitmap>> {
		private static final long serialVersionUID = 1L;
		protected static final int MAX_ENTRIES = 30;

		protected boolean removeEldestEntry(
				Map.Entry<String, SoftReference<Bitmap>> eldest) {
			return (size() > MAX_ENTRIES);
		}
	}

	public static class StrongBitmapCache extends LinkedHashMap<String, Bitmap> {
		private static final long serialVersionUID = 1L;
		protected static final int MAX_ENTRIES = 15;

		private static int sReferenceCount = 0;
		private static StrongBitmapCache sInstance = null;

		// Need reference count so that calling stop() on activity releases it.
		// In contrast, SoftBitmapCache has SoftReferences so that's okay.
		public static StrongBitmapCache getInstance() {
			sReferenceCount++;
			if (sInstance != null) {
				return sInstance;
			} else {
				sInstance = new StrongBitmapCache();
				return sInstance;
			}
		}

		public static void releaseInstance() {
			sReferenceCount--;
			if (sReferenceCount == 0) {
				sInstance = null;
			}
		}

		protected boolean removeEldestEntry(Map.Entry<String, Bitmap> eldest) {
			return (size() > MAX_ENTRIES);
		}
	}

}
