package com.library.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.library.R;

/*
 *
 * This file 'SunRiseView.java' is part of Blinking Sun :
 *
 * Copyright (c) 2016 Stanly Moses.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * OR <http://www.gnu.org/licenses/gpl-3.0.txt>
 *
 */

public class SunRiseView extends View {

    private float mDegree;

    private boolean mBlink;

    private float mRiseOffset  = Integer.MIN_VALUE;
    private float mEyesXOffset = 0;
    private int mSunRadius;

    private ValueAnimator     mAnimator1;
    private ValueAnimator     mAnimator2;
    private ValueAnimator     mAnimator3;
    private Paint.FontMetrics mFontMetrics;
    private float             mMeasureTextWidth;
    private int               sunBackGround;
    private int sunBorderColor;
    private boolean           shouldAnimate;
    int lineWidth;

    int startLineX;

    int startLineY;

    public SunRiseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SunRiseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private Paint mPaint;

    private void init(Context context, AttributeSet attrs) {

        if (!isInEditMode()) {
            initSun(context, attrs); //whatever added functionality you are trying to add to Widget, call that inside this condition.
        }
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(sunBorderColor);
        mPaint.setStrokeWidth(dip2px(context, 3));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextSize(dip2px(context, 12));
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mFontMetrics = mPaint.getFontMetrics();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(dip2px(getContext(), 250), dip2px(getContext(), 250));
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(sunBackGround);

        mPaint.setStyle(Paint.Style.STROKE);

        lineWidth = getWidth() / 2;

        startLineX = (getWidth() - lineWidth) / 2;

        startLineY = getHeight() * 5 / 8;

        canvas.drawLine(startLineX, startLineY, startLineX + lineWidth, startLineY, mPaint);

        mSunRadius = lineWidth * 3 / 8;

        if (mRiseOffset == Integer.MIN_VALUE) {
            mRiseOffset = mSunRadius / 4 + dip2px(getContext(), 2.5f) / 2.0f;
            mEyesXOffset = 0;
        }

        canvas.drawCircle(getWidth() / 2, startLineY + mRiseOffset, mSunRadius, mPaint);
        canvas.save();

        canvas.translate(getWidth() / 2, startLineY + mRiseOffset);
        for (int i = 0; i < 8; i++) {
            canvas.rotate(i == 0 ? 0 + mDegree : 45);
            canvas.drawLine(-lineWidth / 2, 0, -lineWidth / 2 + lineWidth / 8 - dip2px(getContext(), 3) / 2 * 4, 0, mPaint);
        }
        canvas.restore();

        mPaint.setStyle(Paint.Style.FILL);

        if (!mBlink) {
            canvas.drawCircle(startLineX + lineWidth / 8 + mSunRadius / 2 + mEyesXOffset, startLineY - mSunRadius / 4 + mRiseOffset, dip2px(getContext(), 2.5f), mPaint);
            canvas.drawCircle(startLineX + lineWidth / 8 + mSunRadius + mEyesXOffset, startLineY - mSunRadius / 4 + mRiseOffset, dip2px(getContext(), 2.5f), mPaint);
        }

        mPaint.setColor(sunBackGround);


        canvas.drawRect(0, startLineY + dip2px(getContext(), 3) / 2, getWidth(), getHeight(), mPaint);

        mPaint.setColor(sunBorderColor);

        canvas.drawText("", (getWidth() - mMeasureTextWidth) / 2,
                startLineY + ((-mFontMetrics.ascent + mFontMetrics.descent) / 2 - mFontMetrics.descent) + dip2px(getContext(), 3) * 8, mPaint);

    }

    public void startBlinking() {
        shouldAnimate = true;

        animator1().start();
        animator2();
        animator3();
        attachListeners();
    }

    private void attachListeners() {
        mAnimator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (shouldAnimate) {
                    mAnimator2.start();
                }
            }
        });

        mAnimator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (shouldAnimate) {
                    mAnimator3.start();
                }
            }
        });

        mAnimator3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (shouldAnimate) {
                    mAnimator1.start();
                }
            }
        });
    }


    public void stopBlinking() {
        shouldAnimate = false;
    }

    private void animator3() {
        mAnimator3 = new ValueAnimator();
        mAnimator3.setInterpolator(new AccelerateInterpolator());
        mAnimator3.setFloatValues(1, 45);
        mAnimator3.setDuration(1000);
        mAnimator3.addUpdateListener(animation -> {
            mDegree = (float) animation.getAnimatedValue() * 2;
            final float fraction = animation.getAnimatedFraction();

            mBlink = !((fraction >= 0 && fraction < 0.2) || (fraction >= 0.35 && fraction <= 1));

            mEyesXOffset = 0;

            mRiseOffset = -(mSunRadius / 4 + dip2px(getContext(), 2.5f) / 2.0f) + (mSunRadius / 4 + dip2px(getContext(), 2.5f) / 2.0f) * 2 * fraction;

            invalidate();
        });
    }

    private ValueAnimator animator2() {
        mAnimator2 = new ValueAnimator();
        mAnimator2.setInterpolator(new LinearInterpolator());
        mAnimator2.setFloatValues(1, 45);
        mAnimator2.setDuration(3000);
        mAnimator2.addUpdateListener(animation -> {
            mDegree = (float) animation.getAnimatedValue();
            final float fraction = animation.getAnimatedFraction();

            mBlink = !((fraction >= 0 && fraction < 0.2) || (fraction >= 0.25 && fraction < 0.45) || (fraction >= 0.5 && fraction <= 1));

            if (fraction >= 0.5 && fraction <= 0.75) {
                mEyesXOffset = mSunRadius / 2.0f - (float) (mSunRadius / 2.0f * (fraction - 0.5) * 4);
            }

            mRiseOffset = -(mSunRadius / 4 + dip2px(getContext(), 2.5f) / 2.0f) * fraction;

            invalidate();
        });

        return mAnimator2;
    }

    private ValueAnimator animator1() {
        mAnimator1 = new ValueAnimator();
        mAnimator1.setInterpolator(new AccelerateInterpolator());
        mAnimator1.setFloatValues(1, 45);
        mAnimator1.setDuration(2000);
        mAnimator1.addUpdateListener(animation -> {
            mDegree = (float) animation.getAnimatedValue();
            final float fraction = animation.getAnimatedFraction();

            mBlink = !((fraction >= 0 && fraction < 0.2) || (fraction >= 0.25 && fraction < 0.45) || (fraction >= 0.5 && fraction <= 1));

            if (fraction >= 0.5 && fraction <= 0.75) {
                mEyesXOffset = (float) (mSunRadius / 2.0f * (fraction - 0.5) * 4);
            }

            mRiseOffset = (mSunRadius / 4 + dip2px(getContext(), 2.5f) / 2.0f) * (1 - fraction);

            invalidate();
        });
        mAnimator1.setStartDelay(1000);

        return mAnimator1;
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void initSun(Context context, AttributeSet attrs) {

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SunRiseView);
        try {
            sunBackGround = ta.getColor(R.styleable.SunRiseView_sun_view_backgound, Color.YELLOW);
            sunBorderColor = ta.getColor(R.styleable.SunRiseView_sun_border_color, Color.BLACK);
        } finally {
            ta.recycle();
        }
    }
}
