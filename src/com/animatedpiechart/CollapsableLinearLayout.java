package com.animatedpiechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class CollapsableLinearLayout extends LinearLayout {

	/**
	 * @auther tjerk
	 */
	public class ExpandCollapseAnimation extends Animation {
		private View mAnimatedView;
		private int mEndHeight;
		private int mType;
		public final static int COLLAPSE = 1;
		public final static int EXPAND = 0;
		private LinearLayout.LayoutParams mLayoutParams;

		public ExpandCollapseAnimation(View view, int type) {
			mAnimatedView = view;
			
			mEndHeight = mAnimatedView.getMeasuredHeight();
			if (mEndHeight == 0) {
				// if animated view was initially gone, force measure it
				int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
				final int parentWidth = ((View) mAnimatedView.getParent()).getMeasuredWidth();
				int widthSpec = MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.AT_MOST);
				mAnimatedView.measure(widthSpec, heightSpec);
				mEndHeight = mAnimatedView.getMeasuredHeight();
			}
			
			mLayoutParams = ((LinearLayout.LayoutParams) view.getLayoutParams());
			mType = type;
			if (mType == EXPAND) {
				mLayoutParams.bottomMargin = -mEndHeight;
			} else {
				mLayoutParams.bottomMargin = 0;
			}
			mAnimatedView.setVisibility(View.VISIBLE);
		}

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			if (interpolatedTime < 1.0f) {
				final int currentHeight = (int) (mEndHeight * interpolatedTime);
				if (mType == EXPAND) {
					mLayoutParams.bottomMargin = -mEndHeight + currentHeight;
				} else {
					mLayoutParams.bottomMargin = -currentHeight;
				}
				mAnimatedView.requestLayout();
			} else {
				if (mType == EXPAND) {
					mLayoutParams.bottomMargin = 0;
				} else {
					mLayoutParams.bottomMargin = -mEndHeight;
					mAnimatedView.setVisibility(View.INVISIBLE);
				}
				mAnimatedView.requestLayout();
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		LayoutParams mLayoutParams = (LinearLayout.LayoutParams) getLayoutParams();
		if (mLayoutParams.bottomMargin < 0) {
			// if animating, clip the bottom portion of the view
			canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() + mLayoutParams.bottomMargin, Region.Op.REPLACE);
		}
		super.draw(canvas);
	}

	private Interpolator interpolator = new AccelerateDecelerateInterpolator();

	private boolean animating;

	private long duration = 3000;

	public CollapsableLinearLayout(Context context) {
		super(context);
		onInit();
	}

	public CollapsableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		onInit();
	}

	public CollapsableLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		onInit();
	}

	protected void onInit() {
		setWillNotDraw(false);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// consume all touch events when animating
		if (animating) {
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}

	public void setAnimationDuration(long duration) {
		this.duration = duration;
	}

	public long getAnimationDuration() {
		return duration;
	}

	public void setInterpolator(Interpolator interpolator) {
		this.interpolator = interpolator;
	}

	public Interpolator getInterpolator() {
		return interpolator;
	}

	public void expand() {
		animateHeight(ExpandCollapseAnimation.EXPAND);
	}

	public void collapse() {
		animateHeight(ExpandCollapseAnimation.COLLAPSE);
	}

	private void animateHeight(int type) {
		ExpandCollapseAnimation animation = new ExpandCollapseAnimation(this, type);
		animation.setDuration(duration);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				animating = true;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				animating = false;
			}
		});
		animation.setInterpolator(interpolator);
		startAnimation(animation);
	}

}
