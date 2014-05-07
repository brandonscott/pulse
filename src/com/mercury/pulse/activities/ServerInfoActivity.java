package com.mercury.pulse.activities;

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
import com.mercury.pulse.objects.JSONServiceHandler;
import com.mercury.pulse.objects.Pulse;
import com.mercury.pulse.objects.Server;
import com.mercury.pulse.views.PieChartView;
import com.mercury.pulse.views.SmallPieChartView;

public class ServerInfoActivity extends Activity {
	
	//API URL to parse our JSON list of servers from
	private String url;
	//serverid defined from activity bundle
	private static int SERVERID;
	//JSON Node names
	private static final String JSON_ID = "id";
	private static final String JSON_SERVERID = "server_id";
	private static final String JSON_RAMUSAGE = "ram_usage";
	private static final String JSON_CPUUSAGE = "cpu_usage";
	private static final String JSON_HDDUSAGE = "disk_usage";
	private static final String JSON_UPTIME = "uptime";
	private static final String JSON_TIMESTAMP = "timestamp";
	//define a pulse object for the latest pulse
	private Pulse latestPulse;
	//define views
	private TextView mServerName, mWindowsVersion, mServicePack, mUptime;
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
		new GetPulse().execute();
	}
	
	private void init() {
		//get server id from the activity bundle
		Bundle bundle = getIntent().getExtras();
	    SERVERID = bundle.getInt("SERVERID");
		
		//API URL to parse our JSON server data from
		url = "http://cadence-bu.cloudapp.net/cadence/servers/" + SERVERID + "/pulses/latest";
		
		//find views
		mServerName = (TextView) findViewById(R.id.serverinfoactivity_servername);
		mWindowsVersion = (TextView) findViewById(R.id.serverinfoactivity_windowsversion);
		mServicePack = (TextView) findViewById(R.id.serverinfoactivity_servicepack);
		mUptime = (TextView) findViewById(R.id.serverinfoactivity_uptime);
		mPieChart = (PieChartView) findViewById(R.id.stats_piechart);
		mPieChart2 = (SmallPieChartView) findViewById(R.id.stats_piechart2);
		mPieChart3 = (SmallPieChartView) findViewById(R.id.stats_piechart3);
	}

	public class GetPulse extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute() {
			//hide views until the JSON has been parsed
			mServerName.setVisibility(View.GONE);
			mWindowsVersion.setVisibility(View.GONE);
			mServicePack.setVisibility(View.GONE);
			mUptime.setVisibility(View.GONE);
			mPieChart.setVisibility(View.GONE);
			mPieChart2.setVisibility(View.GONE);
			mPieChart3.setVisibility(View.GONE);						
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				// Creating service handler class instance
				JSONServiceHandler jsonHandler = new JSONServiceHandler();

				// Making a request to url and getting response
				String jsonStr = jsonHandler.makeServiceCall(url, JSONServiceHandler.GET);

				Log.d("Latest Pulse: ", "> " + jsonStr);

				if (jsonStr != null) {
					try {
						//JSONArray jsonArr = new JSONArray(jsonStr);

						//for(int i=0; i<jsonArr.length(); i++)
						//{
							JSONObject obj= new JSONObject(jsonStr);
							try {
								latestPulse = new Pulse(obj.getInt(JSON_ID), obj.getInt(JSON_SERVERID), obj.getInt(JSON_RAMUSAGE), obj.getInt(JSON_CPUUSAGE), obj.getInt(JSON_HDDUSAGE), obj.getInt(JSON_UPTIME), obj.getInt(JSON_TIMESTAMP));
							} catch (NumberFormatException e) {
								Log.e("GetPulse", "Pulse JSON nodes couldn't be parsed at integers");
							}
						//}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.e("ServiceHandler", "Couldn't get any data from the url");
				}
				//return null;
			} catch (Exception e) {
				//return e;
			}
			
			
			mServerName.setText("aaaa");
			mWindowsVersion.setText("blaaaaaag");
			mServicePack.setText("blaah");
			mUptime.setText("Uptime: " + latestPulse.getUptime());
			mPieChart.setData(latestPulse.getCPUUsage());
			mPieChart2.setData(latestPulse.getRAMUsage());
			mPieChart3.setData(latestPulse.getHDDUsage());
			return null;
		}

		@Override
		protected void onPostExecute(Void no){
			mServerName.setVisibility(View.VISIBLE);
			mWindowsVersion.setVisibility(View.VISIBLE);
			mServicePack.setVisibility(View.VISIBLE);
			mUptime.setVisibility(View.VISIBLE);
			mPieChart.setVisibility(View.VISIBLE);
			mPieChart2.setVisibility(View.VISIBLE);
			mPieChart3.setVisibility(View.VISIBLE);
		}
	}


}
