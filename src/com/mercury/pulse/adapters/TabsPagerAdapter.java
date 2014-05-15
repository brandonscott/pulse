package com.mercury.pulse.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mercury.pulse.fragments.AboutUsFragment;
import com.mercury.pulse.fragments.AboutReferencesFragment;
import com.mercury.pulse.fragments.AboutPulseFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Top Rated fragment activity
			return new AboutPulseFragment();
		case 1:
			// Games fragment activity
			return new AboutUsFragment();
		case 2:
			// Movies fragment activity
			return new AboutReferencesFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}
