package edu.mit.pt.maps;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import edu.mit.pt.Config;

public class PtolemyMapView extends MapView {

	Context ctx;
	private final int SUPPORTED_ZOOM_LEVEL = 19;
	private final int IMAGE_TILE_SIZE = 512;

	private static final int WEST_LONGITUDE_E6 = -71132032;
	private static final int EAST_LONGITUDE_E6 = -71004543;
	private static final int NORTH_LATITUDE_E6 = 42385049;
	private static final int SOUTH_LATITUDE_E6 = 42339688;

	private int pNumRows = 3;
	private int pNumColumns = 3;
	private boolean pinchZoom = false;
	private PtolemyTileManager tm;

	public PtolemyMapView(Context context, String key) {
		super(context, key);
		ctx = context;
		setup();
	}

	public PtolemyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		ctx = context;
		setup();
	}

	public PtolemyMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		ctx = context;
		setup();
	}

	private void setup() {
		List<Overlay> overlays = getOverlays();
		overlays.add(new TileOverlay());

		getController().setZoom(21);

		setRowsCols();
		getController().setCenter(new GeoPoint(42361568, -71091825));
		
		tm = new PtolemyTileManager(ctx);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			pinchZoom = (ev.getPointerCount() > 1);
			break;
		}

		return super.onTouchEvent(ev);
	}

	private void setRowsCols() {
		Display display = ((WindowManager) ctx
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		int w = display.getWidth();
		int h = display.getHeight();

		pNumRows = h / IMAGE_TILE_SIZE;
		pNumColumns = w / IMAGE_TILE_SIZE;

		if (h % IMAGE_TILE_SIZE != 0) {
			pNumRows++;
		}

		if (w % IMAGE_TILE_SIZE != 0) {
			pNumColumns++;
		}
	}

	/**
	 * PtolemyMapView uses one giant TileOverlay to draw its maps on top of
	 * Google Maps.
	 * 
	 */
	class TileOverlay extends Overlay {
		
		Bitmap bm;

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {

			super.draw(canvas, mapView, shadow);
			// draw() is called twice, once with shadow=true for shadow layer
			// and once with shadow=false for the real layer.
			if (shadow) {
				return;
			}
			// zoomLevel on web maps are 2 less than equivalent on mobile
			// Calculations refer to web map.
			int zoomLevel = mapView.getZoomLevel() - 2;
			if (zoomLevel != SUPPORTED_ZOOM_LEVEL) {
				return;
			}

			int tileSize = GoogleTileCalculator.computeTileSize(mapView, zoomLevel);

			GeoPoint topleftGeoPoint = mapView.getProjection().fromPixels(0, 0);
			// googleX and googleY correspond to the ints that google maps uses
			// to ID tiles
			double googleX = GoogleTileCalculator.computeGoogleX(topleftGeoPoint.getLongitudeE6(),
					zoomLevel);
			double googleY = GoogleTileCalculator.computeGoogleY(topleftGeoPoint.getLatitudeE6(),
					zoomLevel);

			//Log.v(Config.TAG, "Drawing " + googleX + ", " + googleY + "@" + zoomLevel + " (" + topleftGeoPoint.toString() + ")");

			// Tile[X/Y] is integer part of google[X/Y].
			int tileX = (int) googleX;
			int tileY = (int) googleY;
			// Offset is the remaining part.
			int offsetX = -(int) Math.round((googleX - tileX) * tileSize);
			int offsetY = -(int) Math.round((googleY - tileY) * tileSize);

			if (!pinchZoom) {
				drawTiles(canvas, tileX, tileY, offsetX, offsetY, zoomLevel,
						tileSize, true);
			}

		}

		private void drawTiles(Canvas canvas, int tileX, int tileY,
				int offsetX, int offsetY, int zoomLevel, int tileSize,
				boolean fillScreen) {

			Rect src = new Rect();
			Rect dest = new Rect();

			int tileRow, tileCol;

			int numRows;
			int numColumns;
			// TODO(josh) is this necessary
			if (fillScreen && tileSize < IMAGE_TILE_SIZE - 2) {
				// this a crude way to do the calculation
				// but anything else seems to be noticeably slow
				numRows = pNumRows + 1;
				numColumns = pNumColumns + 2;
			} else {
				numRows = pNumRows;
				numColumns = pNumColumns;
			}

			for (int row = 0; row < numRows + 1; row++) {
				for (int col = 0; col < numColumns + 1; col++) {

					tileRow = row + tileY;
					tileCol = col + tileX;

					if (!isTileOnMap(tileCol, tileRow, zoomLevel)) {
						continue;
					}
					
					if (tm.isNotMapped(tileCol, tileRow)) {
						continue;
					}

					int tileOriginX = col * tileSize + offsetX;
					int tileOriginY = row * tileSize + offsetY;
					
					bm = tm.getBitmap(tileCol, tileRow);

					if (bm != null) {
						src.bottom = IMAGE_TILE_SIZE;
						src.left = 0;
						src.right = IMAGE_TILE_SIZE;
						src.top = 0;

						dest.bottom = tileOriginY + tileSize;
						dest.left = tileOriginX;
						dest.right = tileOriginX + tileSize;
						dest.top = tileOriginY;

						canvas.drawBitmap(bm, src, dest, null);

					}
				}

			}
		}
	}

	// TODO(josh) If only one zoom level arrays aren't necessary.
	// Tile boundaries are calculated per zoom level to avoid doing calculations
	// when no map tiles are available.
	private int[] pWestX = new int[22];
	private int[] pEastX = new int[22];
	private int[] pNorthY = new int[22];
	private int[] pSouthY = new int[22];

	private boolean isTileOnMap(int tileX, int tileY, int zoomLevel) {
		initZoomLevel(zoomLevel);

		if (tileX < pWestX[zoomLevel] || tileX > pEastX[zoomLevel]
				|| tileY < pNorthY[zoomLevel] || tileY > pSouthY[zoomLevel]) {
			return false;
		}

		return true;
	}

	private void initZoomLevel(int zoomLevel) {
		if (pWestX[zoomLevel] == 0) {
			pWestX[zoomLevel] = (int) Math.floor(GoogleTileCalculator.computeGoogleX(
					WEST_LONGITUDE_E6, zoomLevel));
		}

		if (pEastX[zoomLevel] == 0) {
			pEastX[zoomLevel] = (int) Math.ceil(GoogleTileCalculator.computeGoogleX(
					EAST_LONGITUDE_E6, zoomLevel));
		}

		if (pNorthY[zoomLevel] == 0) {
			pNorthY[zoomLevel] = (int) Math.floor(GoogleTileCalculator.computeGoogleY(
					NORTH_LATITUDE_E6, zoomLevel));
		}

		if (pSouthY[zoomLevel] == 0) {
			pSouthY[zoomLevel] = (int) Math.ceil(GoogleTileCalculator.computeGoogleY(
					SOUTH_LATITUDE_E6, zoomLevel));
		}
	}
	
	public void stop() {
		PtolemyTileManager.StrongBitmapCache.releaseInstance();
	}

}
