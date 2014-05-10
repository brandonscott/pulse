package com.mercury.pulse.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferencesHandler {
	
	public void savePreference(Context context, String key, String data) {
		SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = mSharedPreferences.edit();
		editor.putString(key, data);
		editor.commit();
	}
	
	public void savePreference(Context context, String key, int data) {
		SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = mSharedPreferences.edit();
		editor.putInt(key, data);
		editor.commit();
	}
	
	public String loadPreference(Context context, String key) {
		SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return mSharedPreferences.getString(key, null);
	}
	
}
