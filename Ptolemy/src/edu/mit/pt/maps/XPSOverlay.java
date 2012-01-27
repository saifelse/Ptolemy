package edu.mit.pt.maps;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import edu.mit.pt.R;

//Positioning system overlay
public class XPSOverlay extends Overlay {
	private GeoPoint point;
	private double dir;
	private MapView mapView;
	
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
	public void setLocation(GeoPoint p) {
		point = p;
		mapView.postInvalidate();
	}
	// TODO: draw differently depending what floor is being examined.
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (point == null) return;

		Point screenPoint = mapView.getProjection().toPixels(point, null);
		Bitmap arrow = BitmapFactory.decodeResource(mapView.getResources(),
				R.drawable.arrow_up_blue);

		Matrix placementMatrix = new Matrix();
		placementMatrix.setTranslate(-arrow.getWidth() / 2,
				-arrow.getHeight() / 2);
		placementMatrix.postRotate((float) dir);
		//System.out.println("Rotate by : " + dir);
		placementMatrix.postTranslate(screenPoint.x, screenPoint.y);

		canvas.drawBitmap(arrow, placementMatrix, null);
	}
}
