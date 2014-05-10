package com.mercury.pulse.activities;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.pulse.R;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.mercury.pulse.helpers.PreferencesHandler;
import com.mercury.pulse.objects.JSONServiceHandler;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

public class GraphActivity extends Activity {

	//API URL to parse our JSON list of servers from
	private String url;
	//serverid defined from activity bundle
	private static int SERVERID;
	//JSON Pulse Node names
	//private static final String JSON_PULSES = "pulses";
	/*private static final String JSON_RAMUSAGE = "ram_usage";
	private static final String JSON_CPUUSAGE = "cpu_usage";
	private static final String JSON_HDDUSAGE = "disk_usage";*/
	private String JSONusage;
	private static final String JSON_TIMESTAMP = "timestamp";
	//week in 30 second segments = 20160
	private final static int WEEK = 20160;
	private java.text.DateFormat dateTimeFormatter;

	//private ArrayList<Pulse> pulses = new ArrayList<Pulse>();
	//create Handler for UI thread updating
	Handler mHandler = new Handler();
	//create a preferences handler
	private PreferencesHandler preferencesHandler = new PreferencesHandler();

	//pulses JSONArray
	JSONArray pulses = null;

	//Hashmap for GraphView
	ArrayList<HashMap<String, Integer>> pulseList;

	private Pubnub pubnub = new Pubnub("pub-c-18bc7bd1-2981-4cc4-9c4e-234d25519d36", "sub-c-5782df52-d147-11e3-93dd-02ee2ddab7fe");

	//define views
	private Spinner mSpinner;
	private GraphView mGraphView;
	private LinearLayout mLayout;

	private Intent intent;
	private Bundle bundle;

	private GraphViewSeries series;
	private GraphViewData[] data;

	double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}

	private void selectRange(int date) {
		//graphView.setViewPort(0, pulseList.size());
		//if less than an hour's data, no view port should be set
		//view port for hour
		if (date == 0) {
			//if pulse list is larger than an hour
			if (pulseList.size() > 120) {
			//set viewport at end of range over an hour
			mGraphView.setViewPort(pulseList.get(pulses.length()-1).get(JSON_TIMESTAMP)-3600,3600);
			}
			else {
				mGraphView.setViewPort(0, pulseList.get(pulses.length()-1).get(JSON_TIMESTAMP)-pulseList.get(0).get(JSON_TIMESTAMP));
			}
		}
		//view port for day
		else if (date == 1) {
			//if pulse list is larger than a day
			if (pulseList.size() > 2880) {
				//set viewport at end of range over a day
				mGraphView.setViewPort(pulseList.get(pulses.length()-1).get(JSON_TIMESTAMP)-86400,86400);
			}
			else {
				mGraphView.setViewPort(0, pulseList.get(pulses.length()-1).get(JSON_TIMESTAMP)-pulseList.get(0).get(JSON_TIMESTAMP));
			}

		}
		//view port for week
		else if (date == 2) {
			//if pulseList is greater than 1 week
			if (pulseList.size() > 20160) {
				//set graphView to end of week
				mGraphView.setViewPort(pulseList.get(pulses.length()-1).get(JSON_TIMESTAMP)-604800,604800);
			}
			else {
				mGraphView.setViewPort(0, pulseList.get(pulses.length()-1).get(JSON_TIMESTAMP)-pulseList.get(0).get(JSON_TIMESTAMP));
			}
		}

	}

	private void init() {
		intent = getIntent();
		bundle = intent.getExtras();
		SERVERID = bundle.getInt("SERVERID");

		//API URL to parse our JSON pulse data from
		url = "http://cadence-bu.cloudapp.net/servers/" + SERVERID + "/pulses/";

		mSpinner = (Spinner) findViewById(R.id.spinner);
		mLayout = (LinearLayout) findViewById(R.id.graph);
		mGraphView = new LineGraphView(this, bundle.getString("title")+" Usage");
		mGraphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
		mGraphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);

		Locale current = getResources().getConfiguration().locale;
		//dateTimeFormatter = DateFormat.getTimeFormat(this);
		dateTimeFormatter = new SimpleDateFormat("dd-MM HH:mm", current);
		pulseList = new ArrayList<HashMap<String, Integer>>();

		init();
		new GetStats().execute();

	}

	private void setSpinner() {
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.date_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mSpinner.setAdapter(adapter);
		mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos,
					long id) {
				selectRange(pos);
				view.invalidate();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void pubnub() {
		try {
			pubnub.subscribe("pulses-"+SERVERID, new Callback() {

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
									if (bundle.getString("title").equals("CPU")) {
										JSONusage = "cpu_usage";
									}
									else if (bundle.getString("title").equals("RAM")) {
										JSONusage = "ram_usage";
									}
									else if (bundle.getString("title").equals("HDD")) {
										JSONusage = "disk_usage";
									}
									int usage = ((JSONObject) message).getInt(JSONusage);
									int timestamp = ((JSONObject) message).getInt(JSON_TIMESTAMP);

									/*HashMap<String, Integer> pulse = new HashMap<String, Integer>();
									
									pulse.put(JSONusage, usage);
									pulse.put(JSON_TIMESTAMP, timestamp);
									
									pulseList.add(pulse);
									
									int num;
									if ((pulseList.get(pulses.length()-1).get(JSON_TIMESTAMP) - pulseList.get(0).get(JSON_TIMESTAMP))/30 < WEEK) {
										num = pulseList.size();
									}
									else {
										num = WEEK;
									}
									data = new GraphViewData[num];
									for (int i = 0; i<num; i++) {
										data[i] = new GraphViewData(pulseList.get(i).get(JSON_TIMESTAMP),pulseList.get(i).get(JSONusage));
									}
									series = new GraphViewSeries(data);
									
									series.resetData(data);*/
									Log.d("Pubnub", "> "+usage+" "+timestamp);
									GraphViewData newData = new GraphViewData(timestamp, usage);
									series.appendData(newData, false, WEEK);
									mGraphView.invalidate();

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
//		//data = new GraphViewData[WEEK];
//		//pulses = populatePulses(WEEK);
//		for (int i=0; i<WEEK; i++) {
//			//data[i] = new GraphViewData(i, pulses.get(i).getRAMUsage());
//			data[i] = new GraphViewData(i, Math.floor(Math.random()*100)+1);
//		}

	}

	private class GetStats extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			JSONServiceHandler sh = new JSONServiceHandler();

			String jsonStr = sh.makeServiceCall(url, JSONServiceHandler.GET, preferencesHandler.loadPreference(getApplicationContext(), "username"),  preferencesHandler.loadPreference(getApplicationContext(), "password"));

			Log.d("Response: ", "> "+ jsonStr);

			if (jsonStr != null) {
				try {
					pulses = new JSONArray(jsonStr);

					if (bundle.getString("title").equals("CPU")) {
						JSONusage = "cpu_usage";
					}
					else if (bundle.getString("title").equals("RAM")) {
						JSONusage = "ram_usage";
					}
					else if (bundle.getString("title").equals("HDD")) {
						JSONusage = "disk_usage";
					}
					for (int i = 0; i < pulses.length(); i++) {
						JSONObject p = pulses.getJSONObject(i);

						int usage = p.getInt(JSONusage);
						int timestamp = p.getInt(JSON_TIMESTAMP);

						HashMap<String, Integer> pulse = new HashMap<String, Integer>();

						pulse.put(JSONusage, usage);
						pulse.put(JSON_TIMESTAMP, timestamp);

						pulseList.add(pulse);

					}
					int num;
					if ((pulseList.get(pulses.length()-1).get(JSON_TIMESTAMP) - pulseList.get(0).get(JSON_TIMESTAMP))/30 < WEEK) {
						num = pulseList.size();
					}
					else {
						num = WEEK;
					}
					data = new GraphViewData[num];
					for (int i = 0; i<num; i++) {
						data[i] = new GraphViewData(pulseList.get(i).get(JSON_TIMESTAMP),pulseList.get(i).get(JSONusage));
					}
					series = new GraphViewSeries(data);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("JSONServiceHandler", "Could not find any data from the url");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			setSpinner();

			//set vertical label number to increments of 10
			mGraphView.getGraphViewStyle().setNumVerticalLabels(11);
			// add data
			mGraphView.addSeries(series);
			//change number to time
			mGraphView.setCustomLabelFormatter(new CustomLabelFormatter () {
				public String formatLabel(double value, boolean isValueX) {
					if(isValueX) {
						return dateTimeFormatter.format(new Date((long)value*1000));
					}
					return null; //let graphview generate Y-axis label for us
				}

			});
			mGraphView.setScrollable(true);
			mGraphView.setScalable(true);
			mGraphView.setManualYAxisBounds(100.0, 0.0);

			mLayout.addView(mGraphView);
			pubnub();
		}

	}

}