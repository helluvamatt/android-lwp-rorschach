package com.schneenet.android.rorschach;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

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
		
		private Handler mHandler;
		private boolean mVisible;
		private float mCenterX;
		private float mCenterY;
		private int mFrameRate = 25;
		private SharedPreferences mPrefs;
		
		private final Runnable mDrawFrame = new Runnable() {
            public void run() {
                drawFrame();
            }
        };
		
		RorschachEngine()
		{
			// Load Preferences
			mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
			mPrefs.registerOnSharedPreferenceChangeListener(this);
			onSharedPreferenceChanged(mPrefs, null);
		}
		
		@Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.removeCallbacks(mDrawFrame);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                drawFrame();
            } else {
                mHandler.removeCallbacks(mDrawFrame);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            // store the center of the surface, so we can draw the cube in the right spot
            mCenterX = width/2.0f;
            mCenterY = height/2.0f;
            drawFrame();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(mDrawFrame);
        }
        
        private void drawFrame()
        {
        	final SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    // TODO draw something
                    
                }
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }

            mHandler.removeCallbacks(mDrawFrame);
            if (mVisible) {
                mHandler.postDelayed(mDrawFrame, 1000 / mFrameRate);
            }
        }

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
		{
			// TODO Reload all preferences
			
		}
		
	}

}
