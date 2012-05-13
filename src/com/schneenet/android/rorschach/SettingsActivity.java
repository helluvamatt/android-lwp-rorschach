package com.schneenet.android.rorschach;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener, SeekBar.OnSeekBarChangeListener
{

	private SharedPreferences mPrefs;

	private SeekBar mSeekBar_frameRate;
	private SeekBar mSeekBar_movementFactor;
	private SeekBar mSeekBar_blotSize;
	private SeekBar mSeekBar_numBlots;
	private CheckBox mCheckBox_antialiasing;
	private TextView mTextView_frameRate;
	private TextView mTextView_movementFactor;
	private TextView mTextView_blotSize;
	private TextView mTextView_numBlots;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.settings_layout);
		
		mTextView_frameRate = (TextView) findViewById(R.id.label_frame_rate);
		mTextView_movementFactor = (TextView) findViewById(R.id.label_movement_factor);
		mTextView_blotSize = (TextView) findViewById(R.id.label_blot_size);
		mTextView_numBlots = (TextView) findViewById(R.id.label_num_blots);
		mSeekBar_frameRate = (SeekBar) findViewById(R.id.field_frame_rate);
		mSeekBar_movementFactor = (SeekBar) findViewById(R.id.field_movement_factor);
		mSeekBar_blotSize = (SeekBar) findViewById(R.id.field_blot_size);
		mSeekBar_numBlots = (SeekBar) findViewById(R.id.field_num_blots);
		mCheckBox_antialiasing = (CheckBox) findViewById(R.id.field_antialiasing);
		
		mSeekBar_frameRate.setOnSeekBarChangeListener(this);
		mSeekBar_frameRate.setMax(MAX_FRAME_RATE - MIN_FRAME_RATE);
		mSeekBar_movementFactor.setOnSeekBarChangeListener(this);
		mSeekBar_movementFactor.setMax(1000);
		mSeekBar_blotSize.setOnSeekBarChangeListener(this);
		mSeekBar_blotSize.setMax(MAX_BLOT_SIZE - MIN_BLOT_SIZE);
		mSeekBar_numBlots.setOnSeekBarChangeListener(this);
		mSeekBar_numBlots.setMax(MAX_NUM_BLOTS - MIN_NUM_BLOTS);

		// Load Preferences
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mPrefs.registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(mPrefs, null);
	}

	public void onPause()
	{
		super.onPause();
		// Save values from fields to SharedPreferences
		
		Editor editor = mPrefs.edit();
		editor.putInt(KEY_FRAME_RATE, mSeekBar_frameRate.getProgress() + MIN_FRAME_RATE);
		editor.putFloat(KEY_MOVEMENT_FACTOR, (float) mSeekBar_movementFactor.getProgress() / 1000f);
		editor.putInt(KEY_BLOT_SIZE, mSeekBar_blotSize.getProgress() + MIN_BLOT_SIZE);
		editor.putInt(KEY_NUM_BLOTS, mSeekBar_numBlots.getProgress() + MIN_NUM_BLOTS);
		editor.putBoolean(KEY_ANTIALIASING, mCheckBox_antialiasing.isChecked());
		editor.commit();
		
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		// Update UI
		int mFrameRate = sharedPreferences.getInt(SettingsActivity.KEY_FRAME_RATE, SettingsActivity.DEFAULT_FRAME_RATE);
		mSeekBar_frameRate.setProgress(mFrameRate - MIN_FRAME_RATE);

		// TODO Color fields / color pickers
		// int mColorBg =
		// sharedPreferences.getInt(SettingsActivity.KEY_COLOR_BG,
		// SettingsActivity.DEFAULT_COLOR_BG);
		// int mColorFg =
		// sharedPreferences.getInt(SettingsActivity.KEY_COLOR_FG,
		// SettingsActivity.DEFAULT_COLOR_FG);

		int mNumBlots = sharedPreferences.getInt(SettingsActivity.KEY_NUM_BLOTS, SettingsActivity.DEFAULT_NUM_BLOTS);
		mSeekBar_numBlots.setProgress(mNumBlots - MIN_NUM_BLOTS);

		float mMovementFactor = sharedPreferences.getFloat(SettingsActivity.KEY_MOVEMENT_FACTOR, SettingsActivity.DEFAULT_MOVEMENT_FACTOR);
		mSeekBar_movementFactor.setProgress(Math.round(mMovementFactor * 1000));

		int mBlotSize = sharedPreferences.getInt(SettingsActivity.KEY_BLOT_SIZE, SettingsActivity.DEFAULT_BLOT_SIZE);
		mSeekBar_blotSize.setProgress(mBlotSize - MIN_BLOT_SIZE);
		
		boolean mAntialiasing = sharedPreferences.getBoolean(KEY_ANTIALIASING, true);
		mCheckBox_antialiasing.setChecked(mAntialiasing);
		
		
	}

	public static final String KEY_COLOR_BG = "key_Color_BG";
	public static final String KEY_COLOR_FG = "key_Color_FG";
	public static final String KEY_FRAME_RATE = "key_FrameRate";
	public static final String KEY_NUM_BLOTS = "key_NumBlots";
	public static final String KEY_MOVEMENT_FACTOR = "key_MovementFactor";
	public static final String KEY_BLOT_SIZE = "key_BlotSize";
	public static final String KEY_ANTIALIASING = "key_Antialiasing";

	public static final int DEFAULT_COLOR_BG = 0xFFFFFFFF;
	public static final int DEFAULT_COLOR_FG = 0xFF000000;

	public static final int DEFAULT_FRAME_RATE = 25;
	public static final int MAX_FRAME_RATE = 30;
	public static final int MIN_FRAME_RATE = 1;

	public static final int DEFAULT_NUM_BLOTS = 50;
	public static final int MAX_NUM_BLOTS = 100;
	public static final int MIN_NUM_BLOTS = 1;

	public static final float DEFAULT_MOVEMENT_FACTOR = 0.8f;
	public static final float MAX_MOVEMENT_FACTOR = 1.0f;
	public static final float MIN_MOVEMENT_FACTOR = 0.0f;

	public static final int DEFAULT_BLOT_SIZE = 160;
	public static final int MAX_BLOT_SIZE = 200;
	public static final int MIN_BLOT_SIZE = 100;

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		// Update labels
		mTextView_frameRate.setText(getResources().getString(R.string.label_frame_rate, mSeekBar_frameRate.getProgress() + MIN_FRAME_RATE));
		mTextView_movementFactor.setText(
				getResources().getString(
						R.string.label_movement_factor, 
						(float) mSeekBar_movementFactor.getProgress() / 1000f));
		mTextView_blotSize.setText(getResources().getString(R.string.label_blot_size, mSeekBar_blotSize.getProgress() + MIN_BLOT_SIZE));
		mTextView_numBlots.setText(getResources().getString(R.string.label_num_blots, mSeekBar_numBlots.getProgress() + MIN_NUM_BLOTS));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
	}

}
