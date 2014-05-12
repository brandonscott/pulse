package com.mercury.pulse.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pulse.R;
import com.mercury.pulse.adapters.NavDrawerListAdapter;
import com.mercury.pulse.fragments.ServerListFragment;
import com.mercury.pulse.helpers.ConnectionHelper;
import com.mercury.pulse.helpers.PreferencesHandler;
import com.mercury.pulse.objects.JSONServiceHandler;
import com.mercury.pulse.objects.ServerGroup;

public class MainActivity extends Activity implements OnItemClickListener {

	private ArrayList<ServerGroup> mNavDrawerItems;
	private DrawerLayout mNavDrawer;
	private ListView mNavDrawerList;
	private ActionBar mActionBar;
	private ActionBarDrawerToggle mNavDrawerToggle;
	private Fragment mServerListFragment;
	private int mFrameLayout = R.id.mainactivity_framelayout;
	//API URL to parse our JSON list of servers from
	private static String serverGroupURL = "http://cadence-bu.cloudapp.net/servergroups";
	//JSON servergroup Node names
	private static final String JSON_SERVERGROUPID = "id";
	private static final String JSON_SITENAME= "name";
	//create a preferences handler
	private PreferencesHandler preferencesHandler = new PreferencesHandler();
	private ConnectionHelper connectionHelper = new ConnectionHelper(this);

	@Override
	/**
	 * override onCreate to provide out layout file and execute init()
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (connectionHelper.isConnected()) {
			init();
		} else {
			finish();
			Toast.makeText(getApplicationContext(), "No network connection! Please login again...", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * performs initialisation activities such as configuring actionbar & navdrawer, and instantiating fragments
	 */
	private void init() {
		//check for internet connection before starting
		if (isNetworkConnected() == false) {
			Toast.makeText(getApplicationContext(), "network not connected!", Toast.LENGTH_LONG).show();
		}
		//setup Actionbar
		mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setTitle("Pulse");

		//instantiate Fragments
		mServerListFragment = new ServerListFragment();

		//setup navdrawer
		mNavDrawer = (DrawerLayout)findViewById(R.id.main_navdrawer);
		mNavDrawerList = (ListView)findViewById(R.id.mainactivity_navdrawer);

		//instantiate navdrawer arraylist
		mNavDrawerItems = new ArrayList<ServerGroup>();
		
		new GetServerGroups().execute();

		//set navdrawer listener
		mNavDrawerList.setOnItemClickListener(this);

		//configure fragment manager
		FragmentTransaction mFragMan = getFragmentManager().beginTransaction();
		mFragMan.replace(mFrameLayout, mServerListFragment); //set default fragment

		// Change the app icon to show/hide nav drawer on click
		mNavDrawerToggle = new ActionBarDrawerToggle(this, mNavDrawer, R.drawable.ic_drawer, R.string.main_navdrawer_open, R.string.main_navdrawer_close);
		mNavDrawer.setDrawerListener(mNavDrawerToggle);

		mFragMan.commit();
	}

	private boolean isNetworkConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	/**
	 * handles on click events for the navdrawer.
	 * 
	 * @param arg0 NavDrawer listview object
	 * @param arg1 
	 * @param arg2 the id of the item clicked, corresponding to the index of the item in the mNavDrawerItems ArrayList
	 * @param arg3 arg2 as an alternative data type
	 * 
	 */
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (mNavDrawerItems.get(arg2).getServerGroupName().equals("Scan QR Code")) {
			Intent i = new Intent(getBaseContext(), QRCodeActivity.class);                      
			startActivity(i);
		}
		
		((ServerListFragment) mServerListFragment).setServerGroupID(mNavDrawerItems.get(arg2).getServerGroupID());
		mActionBar.setTitle(mNavDrawerItems.get(arg2).getServerGroupName());
		mNavDrawer.closeDrawers();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mNavDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mNavDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	/**
	 * inflates ActionBar menu
	 * 
	 * @param menu this particular view's menu
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	/**
	 * override the action buttons onclick event
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mNavDrawerToggle.onOptionsItemSelected(item)){
			return true;
		} else {
			switch (item.getItemId()) {
			case R.id.action_about:
				Intent i = new Intent(getBaseContext(), AboutActivity.class);                      
				startActivity(i);
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}
	}

	private class GetServerGroups extends AsyncTask<Void, Void, Exception> {
		int defaultServerGroupID;
		
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Exception doInBackground(Void... params) {
			//grab the default server group
			try {
				//creating service handler class instance
				JSONServiceHandler jsonHandler = new JSONServiceHandler();
				//build url
				String defaultServerGroupURL = "http://cadence-bu.cloudapp.net/users/" + preferencesHandler.loadPreference(getApplicationContext(), "userid") + "/servergroups/default";
				
				//making a request to url and getting response
				String jsonStr = jsonHandler.makeServiceCall(defaultServerGroupURL, JSONServiceHandler.GET, preferencesHandler.loadPreference(getApplicationContext(), "username"), preferencesHandler.loadPreference(getApplicationContext(), "password"));
				Log.d("Latest Pulse: ", "> " + jsonStr);
				if (jsonStr != null) {
					try {
						JSONObject obj = new JSONObject(jsonStr);
						try {
							defaultServerGroupID = obj.getInt(JSON_SERVERGROUPID);
						} catch (Exception e) {
							Log.e("GetServerGroups()", "failed to parse JSON");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.e("ServiceHandler", "Couldn't get any data from the url");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//grab a list of server groups to use in the nav drawer
			try {
				// Creating service handler class instance
				JSONServiceHandler jsonHandler = new JSONServiceHandler();

				// Making a request to url and getting response
				String jsonStr = jsonHandler.makeServiceCall(serverGroupURL, JSONServiceHandler.GET, preferencesHandler.loadPreference(getApplicationContext(), "username"), preferencesHandler.loadPreference(getApplicationContext(), "password"));

				Log.d("Response: ", "> " + jsonStr);

				if (jsonStr != null) {
					try {
						JSONArray jsonArr = new JSONArray(jsonStr);

						for(int i=0; i<jsonArr.length(); i++)
						{
							JSONObject obj=jsonArr.getJSONObject(i);
							try {
								mNavDrawerItems.add(new ServerGroup(obj.getInt(JSON_SERVERGROUPID), obj.getString(JSON_SITENAME), R.drawable.ic_action_person));
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
		protected void onPostExecute(Exception exception) {
			((ServerListFragment) mServerListFragment).setServerGroupID(mNavDrawerItems.get(defaultServerGroupID).getServerGroupID());
			mActionBar.setTitle(mNavDrawerItems.get(defaultServerGroupID).getServerGroupName());
			mNavDrawerList.setAdapter(new NavDrawerListAdapter(getApplicationContext(), R.layout.activity_main_navdraweritem, mNavDrawerItems));
			mNavDrawerItems.add(new ServerGroup(0, "Scan QR Code", R.drawable.ic_action_qr));
		}
	}

}
