package com.schneenet.android.rorschach;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import com.schneenet.android.rorschach.obj.Inkblot;

public class RorschachWallpaper extends WallpaperService
{

	@Override
	public Engine onCreateEngine()
	{
		// Return a new Engine implementation
		return new RorschachEngine();
	}

	protected Context getContext()
	{
		return this;
	}

	class RorschachEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener
	{

		private Handler mHandler = new Handler();
		private boolean mVisible;
		private int mFrameRate = SettingsActivity.DEFAULT_FRAME_RATE;
		private int mNumBlots = SettingsActivity.DEFAULT_NUM_BLOTS;
		private float mMovementFactor = SettingsActivity.DEFAULT_MOVEMENT_FACTOR;
		private float mMinRadius = (float) SettingsActivity.DEFAULT_BLOT_SIZE * MIN_RADIUS;
		private float mMaxRadius = (float) SettingsActivity.DEFAULT_BLOT_SIZE * MAX_RADIUS;
		private SharedPreferences mPrefs;
		private int mColorBg;
		private Paint mInkPaint;
		private ArrayList<Inkblot> mInkblots = new ArrayList<Inkblot>();
		private Random mRandom;

		private final Runnable mDrawFrame = new Runnable()
		{
			public void run()
			{
				drawFrame();
			}
		};

		public RorschachEngine()
		{
			// Prepare the RNG
			mRandom = new Random(System.currentTimeMillis());

			// Load Preferences
			mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
			mPrefs.registerOnSharedPreferenceChangeListener(this);
			onSharedPreferenceChanged(mPrefs, null);
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder)
		{
			super.onCreate(surfaceHolder);
		}

		@Override
		public void onDestroy()
		{
			super.onDestroy();
			mHandler.removeCallbacks(mDrawFrame);
		}

		@Override
		public void onVisibilityChanged(boolean visible)
		{
			mVisible = visible;
			if (visible)
			{
				populateInkblots();
				drawFrame();
			}
			else
			{
				mHandler.removeCallbacks(mDrawFrame);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height)
		{
			super.onSurfaceChanged(holder, format, width, height);
			drawFrame();
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder)
		{
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder)
		{
			super.onSurfaceDestroyed(holder);
			mVisible = false;
			mHandler.removeCallbacks(mDrawFrame);
		}

		private void populateInkblots()
		{
			mInkblots.clear();
			for (int i = 0; i < mNumBlots; i++)
			{
				// Get a start position
				float startX = mRandom.nextFloat();
				float startY = mRandom.nextFloat();
				
				// Get a start radius
				float startRadius = mMinRadius + (mMaxRadius - mMinRadius) * mRandom.nextFloat();
				
				// Actually add the inkblot
				Inkblot ib = new Inkblot(startX, startY, startRadius);
				animateInkblot(ib);
				mInkblots.add(ib);
			}
		}
		
		private void animateInkblot(Inkblot ib)
		{
			
			// Get start of relative animation
			float startX = ib.getStartCenterX();
			float startY = ib.getStartCenterY();
			float startRadius = ib.getStartRadius();
			
			// Calculate end center point based on mMovementFactor
			float endX = startX + ((mRandom.nextFloat() - startX) * mMovementFactor); 
			float endY = startY + ((mRandom.nextFloat() - startY) * mMovementFactor);
			
			// Calculate end radius value based on mMovementFactor
			float endRadius = startRadius + ((mMinRadius + (mMaxRadius - mMinRadius) * mRandom.nextFloat()) - startRadius) * mMovementFactor;
			
			// How long will the animation last?
			float animSeconds = MIN_ANIM_SECONDS + (MAX_ANIM_SECONDS - MIN_ANIM_SECONDS) * mRandom.nextFloat();
			int animLength = Math.round(animSeconds * (float) mFrameRate);
			
			// Start animation
			ib.animateTo(endX, endY, endRadius, animLength);
		}
		
		private void drawFrame()
		{
			final SurfaceHolder holder = getSurfaceHolder();
			Canvas c = null;
			try
			{
				c = holder.lockCanvas();
				if (c != null)
				{
					// Draw background
					c.drawColor(mColorBg);

					// Draw inkblots
					drawRorschach(c);
				}
			}
			finally
			{
				if (c != null)
					holder.unlockCanvasAndPost(c);
			}

			mHandler.removeCallbacks(mDrawFrame);
			if (mVisible)
			{
				mHandler.postDelayed(mDrawFrame, 1000 / mFrameRate);
			}
		}

		private void drawRorschach(Canvas c)
		{
			
			// Draw and animate each blot
			Iterator<Inkblot> iter = mInkblots.iterator();
			while (iter.hasNext())
			{
				Inkblot cur = iter.next();
				cur.draw(c, mInkPaint);
				if (!cur.animate())
				{
					animateInkblot(cur);
				}
			}

		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
		{
			// Reload all preferences
			mFrameRate = sharedPreferences.getInt(SettingsActivity.KEY_FRAME_RATE, SettingsActivity.DEFAULT_FRAME_RATE);
			mColorBg = sharedPreferences.getInt(SettingsActivity.KEY_COLOR_BG, SettingsActivity.DEFAULT_COLOR_BG);
			int fgColor = sharedPreferences.getInt(SettingsActivity.KEY_COLOR_FG, SettingsActivity.DEFAULT_COLOR_FG);
			mInkPaint = new Paint();
			mInkPaint.setStyle(Style.FILL);
			mInkPaint.setColor(fgColor);
			mInkPaint.setAntiAlias(sharedPreferences.getBoolean(SettingsActivity.KEY_ANTIALIASING, false));
			mNumBlots = sharedPreferences.getInt(SettingsActivity.KEY_NUM_BLOTS, SettingsActivity.DEFAULT_NUM_BLOTS);
			mMovementFactor = sharedPreferences.getFloat(SettingsActivity.KEY_MOVEMENT_FACTOR, SettingsActivity.DEFAULT_MOVEMENT_FACTOR);
			int blotSize = sharedPreferences.getInt(SettingsActivity.KEY_BLOT_SIZE, SettingsActivity.DEFAULT_BLOT_SIZE);
			mMinRadius = (float) blotSize * MIN_RADIUS;
			mMaxRadius = (float) blotSize * MAX_RADIUS;
			
		}

		private static final float MAX_RADIUS = 0.4f;
		private static final float MIN_RADIUS = 0.05f;

		private static final float MAX_ANIM_SECONDS = 30;
		private static final float MIN_ANIM_SECONDS = 2;

	}

}
