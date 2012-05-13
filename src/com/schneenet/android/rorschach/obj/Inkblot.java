package com.schneenet.android.rorschach.obj;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Inkblot
{
	
	private float mStartCenterX;
	private float mStartCenterY;
	private float mEndCenterX;
	private float mEndCenterY;
	private float mStartRadius;
	private float mEndRadius;
	private int mAnimLength;
	private int mAnimCounter;
	
	public Inkblot(float centerStartX, float centerStartY, float startRadius)
	{
		mStartCenterX = centerStartX;
		mStartCenterY = centerStartY;
		mStartRadius = startRadius;
	}
	
	public float getStartCenterX()
	{
		return mStartCenterX;
	}
	
	public float getStartCenterY()
	{
		return mStartCenterY;
	}
	
	public float getStartRadius()
	{
		return mStartRadius;
	}
	
	public void draw(Canvas c, Paint ink)
	{
		int cWidth = c.getWidth();
		int cHeight = c.getHeight();
		int cMidX = cWidth / 2;
		
		float animMultiplier = (float) mAnimCounter / (float) mAnimLength;
		float curCenterX = mStartCenterX + (mEndCenterX - mStartCenterX) * animMultiplier;
		float curCenterY = mStartCenterY + (mEndCenterY - mStartCenterY) * animMultiplier;
		float curRadius = mStartRadius + (mEndRadius - mStartRadius) * animMultiplier;
		
		float offsetCenterX = curCenterX * (float) cMidX;
		float drawCenterY = curCenterY * (float) cHeight;
		c.drawCircle(offsetCenterX, drawCenterY, curRadius, ink);
		c.drawCircle(cWidth - offsetCenterX, drawCenterY, curRadius, ink);
	}
	
	public void animateTo(float centerEndX, float centerEndY, float endRadius, int animLength)
	{
		mEndCenterX = centerEndX;
		mEndCenterY = centerEndY;
		mEndRadius = endRadius;
		mAnimCounter = 0;
		mAnimLength = animLength;
	}
	
	public boolean animate()
	{
		mAnimCounter++;
		if (mAnimLength == 0 || mAnimCounter < mAnimLength)
		{
			return true;
		}
		else
		{
			mStartCenterX = mEndCenterX;
			mStartCenterY = mEndCenterY;
			mStartRadius = mEndRadius;
			mAnimCounter = 0;
			return false;
		}
	}
}
