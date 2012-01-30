package edu.mit.pt;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class WebLogger extends AsyncTask<String, Void, Void> {

	@Override
	protected Void doInBackground(String... params) {
		StringBuilder sb = new StringBuilder();
		if (params.length < 1) {
			return null;
		} else {
			for (String s: params)
				sb.append(s);
		}
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://23.21.193.26");
        try {
            httppost.setEntity(new StringEntity(sb.toString()));
            HttpResponse response = httpclient.execute(httppost);
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return null;
	}
	
	public static void Log(String... params) {
		new WebLogger().execute(params);
	}
}
