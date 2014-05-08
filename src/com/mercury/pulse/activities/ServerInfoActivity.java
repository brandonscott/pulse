package com.mercury.pulse.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pulse.R;
import com.mercury.pulse.objects.JSONServiceHandler;
import com.mercury.pulse.objects.Pulse;
import com.mercury.pulse.objects.Server;
import com.mercury.pulse.views.PieChartView;
import com.mercury.pulse.views.SmallPieChartView;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

public class ServerInfoActivity extends Activity {

	//API URL to parse our JSON list of servers from
	private String pulseURL, serverURL;
	//serverid defined from activity bundle
	private static int SERVERID;
	//JSON Pulse Node names
	private static final String JSON_ID = "id";
	private static final String JSON_SERVERID = "server_id";
	private static final String JSON_RAMUSAGE = "ram_usage";
	private static final String JSON_CPUUSAGE = "cpu_usage";
	private static final String JSON_HDDUSAGE = "disk_usage";
	private static final String JSON_UPTIME = "uptime";
	private static final String JSON_TIMESTAMP = "timestamp";
	//JSON Server Node names
	private static final String JSON_SERVERNAME = "name";
	private static final String JSON_OS_NAME = "os_name";
	private static final String JSON_OS_VERSION = "os_version";
	//define a pulse object for the latest pulse
	private Pulse latestPulse;
	//define a server object to model the server
	private Server server;
	//define views
	private TextView mServerName, mOSName, mOSVersion, mUptime, mCPUUsage, mRAMUsage, mHDDUsage;
	private PieChartView mPieChart;
	private SmallPieChartView mPieChart2, mPieChart3;
	private ProgressBar mProgressBar;
	//create Handler for UI thread updating
	Handler mHandler = new Handler();

	private Pubnub pubnub = new Pubnub("pub-c-18bc7bd1-2981-4cc4-9c4e-234d25519d36", "sub-c-5782df52-d147-11e3-93dd-02ee2ddab7fe");

	@Override
	/**
	 * override onCreate to provide out layout file and execute init()
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serverinfo); 

		init();
		new LoadServerInfo().execute();
	}

	private void init() {
		//get server id from the activity bundle
		Bundle bundle = getIntent().getExtras();
		SERVERID = bundle.getInt("SERVERID");

		//API URL to parse our JSON pulse data from
		pulseURL = "http://cadence-bu.cloudapp.net/servers/" + SERVERID + "/pulses/latest";
		//API URL to parse our JSON pulse data from
		serverURL = "http://cadence-bu.cloudapp.net/servers/" + SERVERID;

		//find views
		mServerName = (TextView) findViewById(R.id.serverinfoactivity_servername);
		mOSName = (TextView) findViewById(R.id.serverinfoactivity_windowsversion);
		mOSVersion = (TextView) findViewById(R.id.serverinfoactivity_servicepack);
		mUptime = (TextView) findViewById(R.id.serverinfoactivity_uptime);
		mCPUUsage = (TextView) findViewById(R.id.serverinfoactivity_CPUusage);
		mRAMUsage = (TextView) findViewById(R.id.serverinfoactivity_RAMusage);
		mHDDUsage = (TextView) findViewById(R.id.serverinfoactivity_HDDusage);
		mPieChart = (PieChartView) findViewById(R.id.stats_piechart);
		mPieChart2 = (SmallPieChartView) findViewById(R.id.stats_piechart2);
		mPieChart3 = (SmallPieChartView) findViewById(R.id.stats_piechart3);
		mProgressBar = (ProgressBar) findViewById(R.id.serverinfoactivity_progressbar);

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
				public void successCallback(String channel, final Object message) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								try {
									System.out.println("baaah");
									latestPulse = new Pulse(((JSONObject) message).getInt(JSON_ID), ((JSONObject) message).getInt(JSON_SERVERID), ((JSONObject) message).getInt(JSON_RAMUSAGE), ((JSONObject) message).getInt(JSON_CPUUSAGE), ((JSONObject) message).getInt(JSON_HDDUSAGE), ((JSONObject) message).getInt(JSON_UPTIME), ((JSONObject) message).getInt(JSON_TIMESTAMP));
									mUptime.setText("Uptime: " + latestPulse.getUptime());
									System.out.println(latestPulse.getCPUUsage());
									mPieChart.setData(latestPulse.getCPUUsage());
									mPieChart2.setData(latestPulse.getRAMUsage());
									mPieChart3.setData(latestPulse.getHDDUsage());
									
								} catch (NumberFormatException e) {
									Log.e("GetPulse", "Pulse JSON nodes couldn't be parsed at integers");
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
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

	public class LoadServerInfo extends AsyncTask<Void, Void, Void> {
		boolean success = true;

		@Override
		protected void onPreExecute() {
			runOnUiThread(new Runnable() {  
				@Override
				public void run() {
					//hide views until the JSON has been parsed
					mServerName.setVisibility(View.GONE);
					mOSName.setVisibility(View.GONE);
					mOSVersion.setVisibility(View.GONE);
					mUptime.setVisibility(View.GONE);
					mCPUUsage.setVisibility(View.GONE);
					mRAMUsage.setVisibility(View.GONE);
					mHDDUsage.setVisibility(View.GONE);
					mPieChart.setVisibility(View.GONE);
					mPieChart2.setVisibility(View.GONE);
					mPieChart3.setVisibility(View.GONE);
					mProgressBar.setVisibility(View.VISIBLE);
				}
			});
		}

		@Override
		protected Void doInBackground(Void... params) {
			//parse the latest Pulse update
			try {
				//creating service handler class instance
				JSONServiceHandler jsonHandler = new JSONServiceHandler();
				//making a request to url and getting response
				String jsonStr = jsonHandler.makeServiceCall(pulseURL, JSONServiceHandler.GET);
				Log.d("Latest Pulse: ", "> " + jsonStr);
				if (jsonStr != null) {
					try {
						JSONObject obj= new JSONObject(jsonStr);
						try {
							latestPulse = new Pulse(obj.getInt(JSON_ID), obj.getInt(JSON_SERVERID), obj.getInt(JSON_RAMUSAGE), obj.getInt(JSON_CPUUSAGE), obj.getInt(JSON_HDDUSAGE), obj.getInt(JSON_UPTIME), obj.getInt(JSON_TIMESTAMP));
						} catch (NumberFormatException e) {
							Log.e("GetPulse", "Pulse JSON nodes couldn't be parsed at integers");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.e("ServiceHandler", "Couldn't get any data from the url");
				}
			} catch (Exception e) {

			}

			//parse the server information
			try {
				//creating service handler class instance
				JSONServiceHandler jsonHandler = new JSONServiceHandler();
				//making a request to url and getting response
				String jsonStr = jsonHandler.makeServiceCall(serverURL, JSONServiceHandler.GET);
				Log.d("Server Info: ", "> " + jsonStr);
				if (jsonStr != null) {
					try {
						JSONObject obj= new JSONObject(jsonStr);
						try {
							server = new Server(obj.getInt(JSON_ID), obj.getString(JSON_SERVERNAME), obj.getString(JSON_OS_NAME), obj.getString(JSON_OS_VERSION));
						} catch (NumberFormatException e) {
							Log.e("GetPulse", "Pulse JSON nodes couldn't be parsed at integers");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.e("ServiceHandler", "Couldn't get any data from the url");
				}
			} catch (Exception e) {

			}

			runOnUiThread(new Runnable() {  
				@Override
				public void run() {
					mServerName.setText(server.getServerName());
					mOSName.setText(server.getServerWindowsVersion());
					mOSVersion.setText("OS Version: " + server.getServicePack());
					try {
						mUptime.setText("Uptime: " + latestPulse.getUptime());
						mPieChart.setData(latestPulse.getCPUUsage());
						mPieChart2.setData(latestPulse.getRAMUsage());
						mPieChart3.setData(latestPulse.getHDDUsage());
					} catch (NullPointerException e) {
						mServerName.setText("No pulse data available!");
						success = false;
					}
				}
			});
			return null;
		}

		@Override
		protected void onPostExecute(Void no) {
			runOnUiThread(new Runnable() {  
				@Override
				public void run() {
					mProgressBar.setVisibility(View.GONE);
					mServerName.setVisibility(View.VISIBLE);
					mOSName.setVisibility(View.VISIBLE);
					mOSVersion.setVisibility(View.VISIBLE);
					mServerName.setVisibility(View.VISIBLE);
					mUptime.setVisibility(View.VISIBLE);
					mPieChart.setVisibility(View.VISIBLE);
					mPieChart2.setVisibility(View.VISIBLE);
					mPieChart3.setVisibility(View.VISIBLE);
					
					if (success == false) {
						mOSName.setVisibility(View.GONE);
						mOSVersion.setVisibility(View.GONE);
						mUptime.setVisibility(View.GONE);
					}
				}
			});
		}
	}



}
