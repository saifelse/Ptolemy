package edu.mit.pt.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class RoomLoader extends AsyncTask {
	public void getRooms() {
		String roomJSON = readRoomJSON();
		try {
			JSONObject rooms = new JSONObject(roomJSON);
			Log.i(RoomLoader.class.getName(),
					"Number of rooms " + rooms.length());
			
			JSONArray roomList = rooms.names();
			for (int i = 0; i < roomList.length(); i++) {
				Log.i(RoomLoader.class.getName(), roomList.getString(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String readRoomJSON() {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(
				"http://mit.edu/~georgiou/pt/rooms.json");
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

	@Override
	protected Object doInBackground(Object... params) {
		getRooms();
		return null;
	}
}
