package com.mercury.pulse.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pulse.R;
import com.mercury.pulse.helpers.ConnectionHelper;
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

public class ServerInfoActivity extends Activity implements OnClickListener {

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
	private TextView mServerName, mOSName, mOSVersion, mUptime, mOnline;
	private PieChartView mPieChart;
	private SmallPieChartView mPieChart2, mPieChart3;
	private ProgressBar mProgressBar;
	//create a preferences handler
	private PreferencesHandler mPreferencesHandler = new PreferencesHandler();
	private ConnectionHelper connectionDetector = new ConnectionHelper(this);

	private Pubnub pubnub = new Pubnub("pub-c-18bc7bd1-2981-4cc4-9c4e-234d25519d36", "sub-c-5782df52-d147-11e3-93dd-02ee2ddab7fe");

	@Override
	/**
	 * override onCreate to provide out layout file and execute init()
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serverinfo); 

		if (connectionDetector.isConnected()) {
			init();
			new LoadServerInfo().execute();
		} else {
			finish();
			Toast.makeText(getApplicationContext(), "No network connection!", Toast.LENGTH_SHORT).show();
		}
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
		mOnline = (TextView) findViewById(R.id.serverinfoactivity_online);
		mPieChart = (PieChartView) findViewById(R.id.stats_piechart);
		mPieChart.setOnClickListener(this);
		mPieChart2 = (SmallPieChartView) findViewById(R.id.stats_piechart2);
		mPieChart2.setOnClickListener(this);
		mPieChart3 = (SmallPieChartView) findViewById(R.id.stats_piechart3);
		mPieChart3.setOnClickListener(this);
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
				String jsonStr = jsonHandler.makeServiceCall(pulseURL, JSONServiceHandler.GET, mPreferencesHandler.loadPreference(getApplicationContext(), "username"), mPreferencesHandler.loadPreference(getApplicationContext(), "password"));
				Log.d("Latest Pulse: ", "> " + jsonStr);

				if (jsonStr.length() > 3) { //3 is the character representation of '{ }' which is returned from blank json
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
					this.cancel(true); //send a terminate signal teh asynctask to try and stop it executing
					finish();
					runOnUiThread(new Runnable() { //toast message must be displayed on ui thread as this is an asynctask
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), "Problem! No Pulse data is available for this server...", Toast.LENGTH_LONG).show();
						}});
				}
			} catch (Exception e) {

			}

			//parse the server information
			try {
				//creating service handler class instance
				JSONServiceHandler jsonHandler = new JSONServiceHandler();
				//making a request to url and getting response
				String jsonStr = jsonHandler.makeServiceCall(serverURL, JSONServiceHandler.GET, mPreferencesHandler.loadPreference(getApplicationContext(), "username"), mPreferencesHandler.loadPreference(getApplicationContext(), "password"));
				Log.d("Server Info: ", "> " + jsonStr);
				if (jsonStr != null) {
					try {
						JSONObject obj= new JSONObject(jsonStr);
						try {
							server = new Server(obj.getInt(JSON_ID), obj.getString(JSON_SERVERNAME), obj.getString(JSON_OS_NAME), obj.getString(JSON_OS_VERSION), obj.getInt(JSON_ONLINE));
							if (server.getServerName().length() >= 2) {
								fail = true;
							}
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
			return null; 
		}

		@Override
		protected void onPostExecute(Void no) {
			if (server.getServerName().length() >= 1) {	
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
				mPieChart2.setSubtitle("RAM");
				mPieChart3.setData(latestPulse.getHDDUsage());
				mPieChart3.setSubtitle("HDD");

				mProgressBar.setVisibility(View.GONE);
				mServerName.setVisibility(View.VISIBLE);
				mOSName.setVisibility(View.VISIBLE);
				mOSVersion.setVisibility(View.VISIBLE);
				mServerName.setVisibility(View.VISIBLE);
				mUptime.setVisibility(View.VISIBLE);
				mOnline.setVisibility(View.VISIBLE);

				//resize circles to scale with screen width
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (getScreenWidth()*0.65), (int) (getScreenWidth()*0.65));
				layoutParams.addRule(RelativeLayout.BELOW, R.id.serverinfoactivity_servicepack);
				layoutParams.setMargins(10, 40, 0, 0);
				mPieChart.setLayoutParams(layoutParams);
				mPieChart.setVisibility(View.VISIBLE);
				layoutParams = new RelativeLayout.LayoutParams((int) (getScreenWidth()*0.30), (int) (getScreenWidth()*0.30));
				layoutParams.addRule(RelativeLayout.ALIGN_TOP, R.id.stats_piechart);
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				layoutParams.setMargins(0, 0, 10, 0);
				mPieChart2.setLayoutParams(layoutParams);
				mPieChart2.setVisibility(View.VISIBLE);
				layoutParams = new RelativeLayout.LayoutParams((int) (getScreenWidth()*0.30), (int) (getScreenWidth()*0.30));
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				layoutParams.addRule(RelativeLayout.BELOW, R.id.stats_piechart2);
				layoutParams.setMargins(0, 20, 10, 0);
				mPieChart3.setLayoutParams(layoutParams);
				mPieChart3.setVisibility(View.VISIBLE);
			} else {
				this.cancel(true);
				finish();
			}			
		}

		private int getScreenWidth() {
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			return (size.x);
		}
	}

	@Override
	public void onClick(View v) {
		Intent i = new Intent(this, GraphActivity.class);
		Bundle b = new Bundle();
		String title = null;
		switch (v.getId()) {
		case R.id.stats_piechart:
			title = "CPU";
			break;
		case R.id.stats_piechart2:
			title = "RAM";
			break;
		case R.id.stats_piechart3:
			title = "HDD";
			break;
		}
		b.putString("title", title);
		b.putInt("SERVERID", SERVERID);
		i.putExtras(b);
		startActivity(i);
	}

}
