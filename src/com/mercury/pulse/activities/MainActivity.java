package com.mercury.pulse.activities;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
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
import com.mercury.pulse.objects.NavDrawerListItem;

public class MainActivity extends Activity implements OnItemClickListener {

	private ArrayList<NavDrawerListItem> mNavDrawerItems;
	private DrawerLayout mNavDrawer;
	private ListView mNavDrawerList;
	private ActionBar mActionBar;
	private ActionBarDrawerToggle mNavDrawerToggle;
	private Fragment mServerListFragment;
	private int mFrameLayout = R.id.mainactivity_framelayout;

	@Override
	/**
	 * override onCreate to provide out layout file and execute init()
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
	}

	/**
	 * performs initialisation activities such as configuring actionbar & navdrawer, and instantiating fragments
	 */
	private void init() {
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

		//populate navdrawer
		mNavDrawerItems = new ArrayList<NavDrawerListItem>();
		mNavDrawerItems.add(new NavDrawerListItem(R.drawable.ic_action_person, "Talbot Site"));
		mNavDrawerItems.add(new NavDrawerListItem(R.drawable.ic_action_person, "Lansdowne Site"));
		mNavDrawerItems.add(new NavDrawerListItem(R.drawable.ic_action_person, "Jurassic Site"));
		mNavDrawerItems.add(new NavDrawerListItem(R.drawable.ic_action_person, "Weymouth Site"));

		//set navdrawer adapter
		mNavDrawerList.setAdapter(new NavDrawerListAdapter(this, R.layout.activity_main_navdraweritem, mNavDrawerItems));
		mNavDrawerList.setOnItemClickListener(this);

		//configure fragment manager
		FragmentTransaction mFragMan = getFragmentManager().beginTransaction();
		mFragMan.replace(mFrameLayout, mServerListFragment); //set default fragment

		// Change the app icon to show/hide nav drawer on click
		mNavDrawerToggle = new ActionBarDrawerToggle(this, mNavDrawer, R.drawable.ic_drawer, R.string.main_navdrawer_open, R.string.main_navdrawer_close);
		mNavDrawer.setDrawerListener(mNavDrawerToggle);


		mFragMan.commit();
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
		if (arg2 == 0) {
			Toast.makeText(getApplicationContext(), "navdrawer item 1",
					Toast.LENGTH_LONG).show();
		} else if (arg2 == 1) {
			Toast.makeText(getApplicationContext(), "navdrawer item 2",
					Toast.LENGTH_LONG).show();
		} else {
			//fail
		}
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
			case R.id.action_settings:
				Toast.makeText(getApplicationContext(), "'settings' clicked",
						Toast.LENGTH_LONG).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}
	}

}
