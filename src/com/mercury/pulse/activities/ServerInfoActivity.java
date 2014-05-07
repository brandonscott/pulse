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
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;
import com.pubnub.api.PubnubError;

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

	private Pubnub pubnub = new Pubnub("pub-c-18bc7bd1-2981-4cc4-9c4e-234d25519d36", "sub-c-5782df52-d147-11e3-93dd-02ee2ddab7fe");

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

		//updateData();
		pubnub();
	}

	public void pubnub() {
		try {
			pubnub.subscribe("pulses", new Callback() {

				@Override
				public void connectCallback(String channel, Object message) {
					System.out.println("SUBSCRIBE : CONNECT on channel:" + channel
							+ " : " + message.getClass() + " : "
							+ message.toString());
				}

				@Override
				public void disconnectCallback(String channel, Object message) {
					System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
							+ " : " + message.getClass() + " : "
							+ message.toString());
				}

				public void reconnectCallback(String channel, Object message) {
					System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
							+ " : " + message.getClass() + " : "
							+ message.toString());
				}

				@Override
				public void successCallback(String channel, Object message) {
					runOnUiThread(new Runnable() {
					     @Override
					     public void run() {
					    		mServerName.setText("dddsdsdds");
					    }
					});
				
					System.out.println("SUBSCRIBE : " + channel + " : "
							+ message.getClass() + " : " + message.toString());
				}

				@Override
				public void errorCallback(String channel, PubnubError error) {
					System.out.println("SUBSCRIBE : ERROR on channel " + channel
							+ " : " + error.toString());
				}
			}
					);
		} catch (PubnubException e) {
			System.out.println(e.toString());
		}
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
