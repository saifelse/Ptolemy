package edu.mit.pt.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.pt.WebLogger;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class RoomLoader {

	private Context context;

	public RoomLoader(Context context) {
		this.context = context;
	}

	public String readRoomJSON() {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://mit.edu/~georgiou/pt/rooms.json");
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(RoomLoader.class.toString(), "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	public int loadRooms() throws JSONException {
		List<ContentValues> valuesToInsert = new ArrayList<ContentValues>();
		int count = 0;

		String roomJSON = readRoomJSON();
		JSONObject rooms = new JSONObject(roomJSON);
		Log.i(RoomLoader.class.getName(), "Number of rooms " + rooms.length());

		JSONArray roomList = rooms.names();
		for (int i = 0; i < roomList.length(); i++) {
			String name = roomList.getString(i);
			if (name == null) {
				Log.v(RoomLoader.class.getName(), "ROOM IS NULL!");
				continue;
			}

			JSONObject coords = rooms.getJSONObject(name);
			int lat = coords.getInt("lat");
			int lon = coords.getInt("lon");
			int floor = coords.getInt("floor");
			String type = "classroom";
			try {
				type = coords.getString("type");
			} catch (Exception e) {

			}

			PlaceType ptype = PlaceType.CLASSROOM;
			if (type.equals("mtoilet")) {
				ptype = PlaceType.MTOILET;
			} else if (type.equals("ftoilet")) {
				ptype = PlaceType.FTOILET;
			} else if (type.equals("athena")) {
				ptype = PlaceType.ATHENA;
			}

			ContentValues values = new ContentValues();
			values.put(PlacesTable.COLUMN_NAME, name);
			values.put(PlacesTable.COLUMN_LAT, lat);
			values.put(PlacesTable.COLUMN_LON, lon);
			values.put(PlacesTable.COLUMN_FLOOR, floor);
			values.put(PlacesTable.COLUMN_TYPE, ptype.toString());

			valuesToInsert.add(values);
			count++;
			// Log.i(RoomLoader.class.getName(), roomList.getString(i));
		}

		Uri CONTENT_URI = Uri
				.parse("content://edu.mit.pt.data.placescontentprovider/");

		context.getContentResolver()
				.bulkInsert(
						CONTENT_URI,
						valuesToInsert.toArray(new ContentValues[valuesToInsert
								.size()]));

		WebLogger.Log("Succesfully loaded rooms");
		
		return count;
	}
}
