package com.mercury.pulse.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pulse.R;
import com.mercury.pulse.activities.ServerInfoActivity;
import com.mercury.pulse.adapters.ServerListAdapter;
import com.mercury.pulse.helpers.PreferencesHandler;
import com.mercury.pulse.objects.JSONServiceHandler;
import com.mercury.pulse.objects.Server;


public class ServerListFragment extends Fragment implements OnItemClickListener {

	//API URL to parse our JSON list of servers from
	private static String url = "http://cadence-bu.cloudapp.net/servergroups/";
	//JSON Node names
	private static final String JSON_ID = "id";
	private static final String JSON_NAME = "name";
	private static final String JSON_OS_NAME = "os_name";
	//hashmap for ListView
	ArrayList<HashMap<String, String>> serverList;
	//create a preferences handler
	private PreferencesHandler preferencesHandler = new PreferencesHandler();

	private ProgressBar							mProgressBar;
	private TextView							mTextView;
	private GridView							mGridView;
	private ArrayList<Server>					mServerList;
	private ServerListAdapter					mServerListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		super.onCreateOptionsMenu(menu, menuInflater);
		//inf.inflate(R.menu.loanslist, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroupContainer, Bundle savedInstanceState) {
		super.onCreateView(layoutInflater, viewGroupContainer, savedInstanceState);
		View v = layoutInflater.inflate(R.layout.fragment_serverlist, viewGroupContainer, false);
		mProgressBar = (ProgressBar)v.findViewById(R.id.serverlist_progressbar);
		mTextView = (TextView)v.findViewById(R.id.serverlist_textview);
		mGridView = (GridView)v.findViewById(R.id.serverlist_gridview);
		if (getScreenWidth() >= 800) {
			mGridView.setNumColumns(3);
			Log.e("screenwidthbig", getScreenWidth()+"");
		} else {
			Log.e("screenwidthsmall", getScreenWidth()+"");
		}
		mGridView.setOnItemClickListener(this);
		return v;
	}
	
	private int getScreenWidth() {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return (size.x);
	}

	public void setServerGroupID(int serverGroupID) {
		url = "http://cadence-bu.cloudapp.net/servergroups/" + serverGroupID + "/servers";
		new GetServers().execute();
	}

	private class GetServers extends AsyncTask<Void, Void, Exception> {
		@Override
		protected void onPreExecute() {
			mProgressBar.setVisibility(ProgressBar.VISIBLE);
			mGridView.setVisibility(GridView.INVISIBLE);
			mTextView.setVisibility(TextView.INVISIBLE);
			mServerList = new ArrayList<Server>();
		}

		@Override
		protected Exception doInBackground(Void... params) {
			try {
				// Creating service handler class instance
				JSONServiceHandler jsonHandler = new JSONServiceHandler();

				// Making a request to url and getting response
				String jsonStr = jsonHandler.makeServiceCall(url, JSONServiceHandler.GET, preferencesHandler.loadPreference(getActivity(), "username"), preferencesHandler.loadPreference(getActivity(), "password"));

				Log.d("Response: ", "> " + jsonStr);

				if (jsonStr != null) {
					try {
						JSONArray jsonArr = new JSONArray(jsonStr);

						for(int i=0; i<jsonArr.length(); i++)
						{
							JSONObject obj=jsonArr.getJSONObject(i);
							try {
								Server newServer = new Server(Integer.parseInt(obj.getString(JSON_ID)), obj.getString(JSON_NAME), obj.getString(JSON_OS_NAME));
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
		protected void onPostExecute(Exception exception){
			mProgressBar.setVisibility(ProgressBar.INVISIBLE);
			if (exception != null) {
				Toast.makeText(getActivity().getBaseContext(), "Generic Exception Generated", Toast.LENGTH_LONG).show();
				Log.e("ServerListFragment", "Generic Exception generated");
				exception.printStackTrace();
			} else {
				if (mServerList == null || mServerList.size() < 1) {
					mTextView.setVisibility(View.VISIBLE);
					mTextView.setText("Server Group Empty!");
					Log.i("ServerListFragment", "Server ArrayList empty");
				} else {
					try {
						mServerListAdapter = new ServerListAdapter(getActivity(), mServerList);
						mGridView.setAdapter(mServerListAdapter);
						mGridView.setVisibility(GridView.VISIBLE);
					} catch (Exception e){
						Toast.makeText(getActivity().getBaseContext(), "Generic Exception Generated", Toast.LENGTH_LONG).show();
						Log.e("ServerListFragment", "Generic Exception generated");
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Bundle b = new Bundle();
		b.putInt("SERVERID", ((Server)arg1.getTag()).getServerID());
		Intent i = new Intent(getActivity(), ServerInfoActivity.class);
		i.putExtras(b);
		startActivity(i);
	}
}