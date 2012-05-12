package com.schneenet.android.rorschach;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SettingsActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener
{

	private SharedPreferences mPrefs;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.settings_layout);
		// TODO Load UI and initialize it		
		
		// Load Preferences
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mPrefs.registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(mPrefs, null);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		// TODO Update UI
	}
	
	public static final String KEY_COLOR_BG = "key_Color_BG";
	public static final String KEY_COLOR_FH = "key_Color_FG";
	public static final String KEY_FRAME_RATE = "key_FrameRate";
	
}
