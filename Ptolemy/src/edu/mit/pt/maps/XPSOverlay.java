package edu.mit.pt.maps;

import edu.mit.pt.Config;
import edu.mit.pt.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class XPSOverlay extends Overlay {
	private GeoPoint point;
	private double dir;

	public XPSOverlay(MapView mapView) {
		super();

		// Listen for magnetic compass
		SensorManager sman = (SensorManager) mapView.getContext()
				.getSystemService(Context.SENSOR_SERVICE);
		Sensor accelerometerSensor = sman
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor magneticFieldSensor = sman
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		SensorEventListener compassListener = new SensorEventListener() {
			private float[] accData;
			private float[] magData;

			@Override
			public void onSensorChanged(SensorEvent event) {
				switch (event.sensor.getType()) {
				case Sensor.TYPE_ACCELEROMETER:
					accData = event.values;
					break;
				case Sensor.TYPE_MAGNETIC_FIELD:
					magData = event.values;
					break;
				}
				if (accData != null && magData != null) {
					float R[] = new float[9];
					float I[] = new float[9];
					boolean success = SensorManager.getRotationMatrix(R, I,
							accData, magData);
					if (success) {
						float orientation[] = new float[3];
						SensorManager.getOrientation(R, orientation);
						setBearing(orientation[0] * 180.0 / Math.PI);
					}
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};

		// Register listeners
		sman.registerListener(compassListener, magneticFieldSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		sman.registerListener(compassListener, accelerometerSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	protected void setBearing(double bearing) {
		Log.v(Config.TAG + "_dir", "bearing: " + bearing);
		dir = bearing;
	}

	protected void setLocation(GeoPoint p) {
		point = p;
	}

	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (point == null)
			return;

		Point screenPoint = mapView.getProjection().toPixels(point, null);
		Bitmap arrow = BitmapFactory.decodeResource(mapView.getResources(),
				R.drawable.arrow_up_blue);

		Matrix placementMatrix = new Matrix();
		placementMatrix.setTranslate(-arrow.getWidth() / 2,
				-arrow.getHeight() / 2);
		placementMatrix.postRotate((float) dir);
		placementMatrix.postTranslate(screenPoint.x, screenPoint.y);

		canvas.drawBitmap(arrow, placementMatrix, null);
	}
}
