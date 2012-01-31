package edu.mit.pt.maps;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import edu.mit.pt.R;
import edu.mit.pt.location.APGeoPoint;

//Positioning system overlay
public class XPSOverlay extends Overlay {
	private GeoPoint point;
	private double dir;
	private MapView mapView;
	private int floor;
	private int viewFloor;
	public XPSOverlay(MapView mapView) {
		super();
		this.mapView = mapView;
		dir = 0;
	}
	public void setBearing(double bearing) {
		//Log.v(Config.TAG, "bearing: "+bearing);
		dir = bearing;
		mapView.postInvalidate();
	}
	public void setLocation(APGeoPoint p) {
		point = (GeoPoint)p;
		floor = p.getFloor();
		mapView.postInvalidate();
	}
	public void setViewFloor(int floor){
		viewFloor = floor;
		mapView.postInvalidate();
	}
	// TODO: draw differently depending what floor is being examined.
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (point == null) return;

		Point screenPoint = mapView.getProjection().toPixels(point, null);
		int resource;
		if(floor != viewFloor){
			resource = R.drawable.location_marker_other_floor;
		}else{
			resource = R.drawable.location_marker;
		}
		
		Paint aliasPaint = new Paint();
		aliasPaint.setAntiAlias(true);
		aliasPaint.setFilterBitmap(true);
		aliasPaint.setDither(true);
		
		Bitmap arrow = BitmapFactory.decodeResource(mapView.getResources(),
				resource);
		
		Matrix placementMatrix = new Matrix();
		placementMatrix.setTranslate(-arrow.getWidth() / 2,
				-arrow.getHeight() / 2);
		placementMatrix.postRotate((float) dir);
		placementMatrix.postTranslate(screenPoint.x, screenPoint.y);
		
		canvas.drawBitmap(arrow, placementMatrix, aliasPaint);
	}
}
