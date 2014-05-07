package com.mercury.pulse.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.pulse.R;
import com.mercury.pulse.object.Server;
import com.mercury.pulse.objects.JSONServiceHandler;
import com.mercury.pulse.views.PieChartView;
import com.mercury.pulse.views.SmallPieChartView;

public class ServerInfoActivity extends Activity {
	
	//API URL to parse our JSON list of servers from
	private String url;
	//serverid defined from activity bundle
	private static int SERVERID;
	//JSON Node names
	private static final String JSON_ID = "id";
	private static final String JSON_DESCRIPTION = "description";
	//ArrayList for pulse storage
	private ArrayList<Pulse> mPulseList;
	//define views
	private TextView mServerName;
	private PieChartView mPieChart;
	private SmallPieChartView mPieChart2, mPieChart3;
	
	@Override
	/**
	 * override onCreate to provide out layout file and execute init()
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serverinfo); 
		
		init();
		updateData();
	}
	
	private void init() {
		//get server id from the activity bundle
		Bundle bundle = getIntent().getExtras();
	    SERVERID = bundle.getInt("SERVERID");
		
		//API URL to parse our JSON server data from
		url = "http://cadence-bu.cloudapp.net/cadence/servers/" + SERVERID + "/pulses/";
		mServerName = (TextView) findViewById(R.id.serverinfoactivity_windowsversion);
		mPieChart = (PieChartView) findViewById(R.id.stats_piechart);
		mPieChart2 = (SmallPieChartView) findViewById(R.id.stats_piechart2);
		mPieChart3 = (SmallPieChartView) findViewById(R.id.stats_piechart3);
	}

	
	public void updateData(){
		new UpdateData().execute();
	}

	public class UpdateData extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute(){
			mServerName.setVisibility(View.GONE);
			mPieChart.setVisibility(View.GONE);
			mPieChart2.setVisibility(View.GONE);
			mPieChart3.setVisibility(View.GONE);
			
			try {
				// Creating service handler class instance
				JSONServiceHandler jsonHandler = new JSONServiceHandler();

				// Making a request to url and getting response
				String jsonStr = jsonHandler.makeServiceCall(url, JSONServiceHandler.GET);

				Log.d("Response: ", "> " + jsonStr);

				if (jsonStr != null) {
					try {
						JSONArray jsonArr = new JSONArray(jsonStr);

						for(int i=0; i<jsonArr.length(); i++)
						{
							JSONObject obj=jsonArr.getJSONObject(i);
							try {
								Server newServer = new Server(Integer.parseInt(obj.getString(JSON_ID)), obj.getString(JSON_DESCRIPTION));
								mServerList.add(newServer);
							} catch (NumberFormatException e) {
								Log.e("GetServers", "Server ID could not be parsed as an integer...");
							}
							//Log.d("PARSEMESOMEMOTHERFUCKINGJSON", "id: " + id + " description: " + description);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.e("ServiceHandler", "Couldn't get any data from the url");
				}
				return null;
			} catch (Exception e) {
				return e;
			}
			
		}

		@Override
		protected Void doInBackground(Void... params) {
			mServerName.setText(text);
			mPieChart.setData(25);
			mPieChart2.setData(76);
			mPieChart3.setData(83);
			return null;
		}

		@Override
		protected void onPostExecute(Void no){
			mPieChart.setVisibility(View.VISIBLE);
			mPieChart2.setVisibility(View.VISIBLE);
			mPieChart3.setVisibility(View.VISIBLE);
		}
	}


}
