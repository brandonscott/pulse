package com.mercury.pulse.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pulse.R;
import com.mercury.pulse.helpers.PreferencesHandler;
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
	private static final String JSON_ONLINE = "online";
	//define a pulse object for the latest pulse
	private Pulse latestPulse;
	//define a server object to model the server
	private Server server;
	//define views
	private TextView mServerName, mOSName, mOSVersion, mUptime, mCPUUsage, mRAMUsage, mHDDUsage, mOnline;
	private PieChartView mPieChart;
	private SmallPieChartView mPieChart2, mPieChart3;
	private ProgressBar mProgressBar;
	//create a preferences handler
	private PreferencesHandler preferencesHandler = new PreferencesHandler();

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
		mOnline = (TextView) findViewById(R.id.serverinfoactivity_online);
		mPieChart = (PieChartView) findViewById(R.id.stats_piechart);
		mPieChart2 = (SmallPieChartView) findViewById(R.id.stats_piechart2);
		mPieChart3 = (SmallPieChartView) findViewById(R.id.stats_piechart3);
		mProgressBar = (ProgressBar) findViewById(R.id.serverinfoactivity_progressbar);

		pubnub();
	}

	public void pubnub() {
		try {
			pubnub.subscribe(new String[]{"pulses-"+SERVERID, "pulses-"+SERVERID+"-online", "test"}, new Callback() {

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
					System.out.println("SUBSCRIBE : " + channel + " : "
							+ message.getClass() + " : " + message.toString());
					if (channel.equals("pulses-"+SERVERID)) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try {
									try {
										//System.out.println("baaah");
										latestPulse = new Pulse(((JSONObject) message).getInt(JSON_ID), ((JSONObject) message).getInt(JSON_SERVERID), ((JSONObject) message).getInt(JSON_RAMUSAGE), ((JSONObject) message).getInt(JSON_CPUUSAGE), ((JSONObject) message).getInt(JSON_HDDUSAGE), ((JSONObject) message).getInt(JSON_UPTIME), ((JSONObject) message).getInt(JSON_TIMESTAMP));
										mUptime.setText("Uptime: " + latestPulse.getUptime());
										//System.out.println(latestPulse.getCPUUsage());
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
					} else if (channel.equals("pulses-"+SERVERID+"-online")) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try {
									try {
										JSONObject jsonObj = new JSONObject(message.toString());
										if (jsonObj.getInt("online") == 1) {
											mOnline.setText("Online");
											mOnline.setTextColor(Color.parseColor("#99CC00"));
										} else {
											mOnline.setText("Offline");
											mOnline.setTextColor(Color.parseColor("#FF4444"));
										}
									} catch (NumberFormatException e) {
										Log.e("GetPulse", "Pulse JSON nodes couldn't be parsed at integers");
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}

					
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
		boolean fail = false;

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
					mOnline.setVisibility(View.GONE);
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
				String jsonStr = jsonHandler.makeServiceCall(pulseURL, JSONServiceHandler.GET, preferencesHandler.loadPreference(getApplicationContext(), "username"), preferencesHandler.loadPreference(getApplicationContext(), "password"));
				Log.d("Latest Pulse: ", "> " + jsonStr);
				if (jsonStr != null) {
					try {
						JSONObject obj= new JSONObject(jsonStr);
						try {
							if (jsonStr.length() <= 4) {
								fail = false;
								return null;
							}
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
				String jsonStr = jsonHandler.makeServiceCall(serverURL, JSONServiceHandler.GET, preferencesHandler.loadPreference(getApplicationContext(), "username"), preferencesHandler.loadPreference(getApplicationContext(), "password"));
				Log.d("Server Info: ", "> " + jsonStr);
				if (jsonStr != null) {
					try {
						JSONObject obj= new JSONObject(jsonStr);
						try {
							server = new Server(obj.getInt(JSON_ID), obj.getString(JSON_SERVERNAME), obj.getString(JSON_OS_NAME), obj.getString(JSON_OS_VERSION), obj.getInt(JSON_ONLINE));
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
					mUptime.setText("Uptime: " + latestPulse.getUptime());
					if (server.isOnline() == 1) {
						mOnline.setText("Online");
						mOnline.setTextColor(Color.parseColor("#99CC00"));
					} else {
						mOnline.setText("Offline");
						mOnline.setTextColor(Color.parseColor("#FF4444"));
					}
					mPieChart.setData(latestPulse.getCPUUsage());
					mPieChart2.setData(latestPulse.getRAMUsage());
					mPieChart3.setData(latestPulse.getHDDUsage());
				}
			});
			return null;
		}

		@Override
		protected void onPostExecute(Void no) {
			runOnUiThread(new Runnable() {  
				@Override
				public void run() {
					if (fail == true) {
						mProgressBar.setVisibility(View.GONE);
						mServerName.setVisibility(View.VISIBLE);
						mServerName.setText("No Pulse data available!");
						mOSName.setVisibility(View.GONE);
						mOSVersion.setVisibility(View.GONE);
						mServerName.setVisibility(View.GONE);
						mUptime.setVisibility(View.GONE);
						mOnline.setVisibility(View.GONE);
						mPieChart.setVisibility(View.GONE);
						mPieChart2.setVisibility(View.GONE);
						mPieChart3.setVisibility(View.GONE);
					} else {
						mProgressBar.setVisibility(View.GONE);
						mServerName.setVisibility(View.VISIBLE);
						mOSName.setVisibility(View.VISIBLE);
						mOSVersion.setVisibility(View.VISIBLE);
						mServerName.setVisibility(View.VISIBLE);
						mCPUUsage.setVisibility(View.VISIBLE);
						mRAMUsage.setVisibility(View.VISIBLE);
						mHDDUsage.setVisibility(View.VISIBLE);
						mUptime.setVisibility(View.VISIBLE);
						mOnline.setVisibility(View.VISIBLE);
						mPieChart.setVisibility(View.VISIBLE);
						mPieChart2.setVisibility(View.VISIBLE);
						mPieChart3.setVisibility(View.VISIBLE);
					}
				}
			});
		}

		private int getScreenWidth() {
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			return (size.x);
		}

		private int getScreenHeight() {
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			return (size.y);
		}
	}

}
