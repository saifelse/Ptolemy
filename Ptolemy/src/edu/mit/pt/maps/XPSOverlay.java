package edu.mit.pt.maps;

import edu.mit.pt.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class XPSOverlay extends Overlay {
	private GeoPoint point;
	private float dir;

	public void setLocation(GeoPoint p, float direction) {
		point = p;
		dir = direction;
	}

	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (point == null)
			return;

		Point screenPoint = mapView.getProjection().toPixels(point, null);
		Bitmap arrow = BitmapFactory.decodeResource(mapView.getResources(),
				R.drawable.arrow_up_blue);

		Matrix placementMatrix = new Matrix();
		placementMatrix.setRotate(dir);
		placementMatrix.setTranslate(screenPoint.x - arrow.getWidth() / 2,
				screenPoint.y - arrow.getHeight() / 2);

		canvas.drawBitmap(arrow, placementMatrix, null);
	}
}
