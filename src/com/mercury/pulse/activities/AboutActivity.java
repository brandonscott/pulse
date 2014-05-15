package com.mercury.pulse.activities;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.pulse.R;
import com.mercury.pulse.adapters.TabsPagerAdapter;

public class AboutActivity extends FragmentActivity implements
ActionBar.TabListener {

	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "About Pulse", "About Us", "References" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);		

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
	
	public void maxbuttonhandler(View view) {
		Intent intent = new Intent(Intent.ACTION_VIEW, 
			     Uri.parse("http://uk.linkedin.com/pub/max-rycroft/83/250/563"));
			startActivity(intent);
	}
	
	public void matbuttonhandler(View view) {
		Intent intent = new Intent(Intent.ACTION_VIEW, 
			     Uri.parse("http://uk.linkedin.com/pub/mateusz-salamon/97/375/888"));
			startActivity(intent);
	}
	
	public void nickbuttonhandler(View view) {
		Intent intent = new Intent(Intent.ACTION_VIEW, 
			     Uri.parse("http://uk.linkedin.com/pub/nicholas-tyrrell/95/58/765"));
			startActivity(intent);
	}
	
	public void brandonbuttonhandler(View view) {
		Intent intent = new Intent(Intent.ACTION_VIEW, 
			     Uri.parse("http://www.brandonscott.co.uk/"));
			startActivity(intent);
	}
	
	public void chrisbuttonhandler(View view) {
		Intent intent = new Intent(Intent.ACTION_VIEW, 
			     Uri.parse("http://uk.linkedin.com/pub/christopher-franklin/1b/702/734"));
			startActivity(intent);
	}
	
	public void nickabuttonhandler(View view) {
		Intent intent = new Intent(Intent.ACTION_VIEW, 
			     Uri.parse("http://uk.linkedin.com/in/nickalderson"));
			startActivity(intent);
	}
	
	public void jimmybuttonhandler(View view) {
		Intent intent = new Intent(Intent.ACTION_VIEW, 
			     Uri.parse("http://www.jamesbaldwin.co.uk"));
			startActivity(intent);
	}

}
