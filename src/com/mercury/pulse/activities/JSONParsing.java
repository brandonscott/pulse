package com.mercury.pulse.activities;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mercury.pulse.objects.JSONServiceHandler;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class JSONParsing extends ListActivity {

	// URL to get contacts JSON
	private static String url = "http://cadence-bu.cloudapp.net/cadence/servers";

	// JSON Node names
	private static final String JSON_ID = "id";
	private static final String JSON_DESCRIPTION = "description";
	// Hashmap for ListView
	ArrayList<HashMap<String, String>> contactList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);

		contactList = new ArrayList<HashMap<String, String>>();

		// Calling async task to get json
		new GetServers().execute();
	}

	/**
	 * Async task class to get json by making HTTP call
	 * */
	public class GetServers extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			JSONServiceHandler sh = new JSONServiceHandler();

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(url, JSONServiceHandler.GET, "brandon@brandonscott.co.uk", "Cadenc3!");

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {
					JSONArray jsonArr = new JSONArray(jsonStr);

					for(int i=0; i<jsonArr.length(); i++)
					{
					     JSONObject obj=jsonArr.getJSONObject(i);
					     String id = obj.getString(JSON_ID); 
					     String description = obj.getString(JSON_DESCRIPTION);
					     Log.d("PARSEMESOMEMOTHERFUCKINGJSON", "id: " + id + " description: " + description);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

	}

}