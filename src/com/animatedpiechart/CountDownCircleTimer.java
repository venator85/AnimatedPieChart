package com.animatedpiechart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

public class CountDownCircleTimer extends TextView {

	public interface TimerListener {
		void onTick(CountDownCircleTimer timerView, long secondsLeft);
	}

	private class AngleEvaluator extends FloatEvaluator {
		@Override
		public Float evaluate(float fraction, Number startValue, Number endValue) {
			float num = super.evaluate(fraction, startValue, endValue);
			currentAngle = num;
			invalidate();
			return num;
		}
	}

	public static enum DominantMeasurement {
		DOMINANT_WIDTH, DOMINANT_HEIGHT
	}

	private class TickRunnable implements Runnable {
		private long remaining;

		public TickRunnable(long l) {
			super();
			this.remaining = l;
		}

		@Override
		public void run() {
			remaining--;
			Log.e("", "remaining " + remaining);
			setText(String.valueOf(remaining));
			if (timerListener != null) {
				timerListener.onTick(CountDownCircleTimer.this, remaining);
			}
			if (remaining > 0) {
				handler.postDelayed(this, 1000);
			}
		}
	};

	private Handler handler;

	private Paint paint;
	private Paint eraser;

	private float currentAngle;

	private float innerCircleRadiusPercentage;
	private int innerCircleRadius;

	private boolean shouldDraw;

	private RectF bounds;
	private Rect tempBounds;

	private DominantMeasurement dominantMeasurement = DominantMeasurement.DOMINANT_WIDTH;

	private ValueAnimator animator;
	private TimerListener timerListener;

	private Bitmap bitmap;
	private Canvas bitmapCanvas;

	public CountDownCircleTimer(Context context) {
		super(context);
		init();
	}

	public CountDownCircleTimer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CountDownCircleTimer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init() {
		handler = new Handler();

		bounds = new RectF();
		tempBounds = new Rect();

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		eraser = new Paint(Paint.ANTI_ALIAS_FLAG);
		eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

		setGravity(Gravity.CENTER);
	}

	public void setPaintColor(int color) {
		paint.setColor(color);
		invalidate();
	}

	public void setTimerListener(TimerListener timerListener) {
		this.timerListener = timerListener;
	}

	public TimerListener getTimerListener() {
		return timerListener;
	}

	public void setInnerCircleRadiusPercentage(float percentage) {
		this.innerCircleRadiusPercentage = percentage;
		requestLayout();
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		if (shouldDraw) {
			canvas.getClipBounds(tempBounds);
			bounds.set(tempBounds);

			bitmap.eraseColor(Color.TRANSPARENT);

			bitmapCanvas.drawArc(bounds, -90f, currentAngle, true, paint);
			bitmapCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, innerCircleRadius, eraser);

			canvas.drawBitmap(bitmap, 0, 0, null);
		}
		super.onDraw(canvas);
	}

	public void start(final long duration) {
		shouldDraw = true;
		animator = ValueAnimator.ofObject(new AngleEvaluator(), 360.0f, 0.0f);
		animator.setDuration(duration);
		animator.setInterpolator(new LinearInterpolator());
		animator.addListener(new AnimatorListenerAdapter() {
			Runnable r = new TickRunnable(duration / 1000);

			@Override
			public void onAnimationEnd(Animator animation) {
				handler.removeCallbacks(r);
			}

			@Override
			public void onAnimationStart(Animator animation) {
				handler.post(r);
			}
		});
		animator.start();
	}

	public void stop() {
		if (animator != null) {
			animator.cancel();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int newWidth;
		int newHeight;

		if (dominantMeasurement == DominantMeasurement.DOMINANT_WIDTH) {
			newWidth = getMeasuredWidth();
			newHeight = newWidth;
		} else {
			newHeight = getMeasuredHeight();
			newWidth = newHeight;
		}

		setMeasuredDimension(newWidth, newHeight);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (w != oldw || h != oldh) {
			bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			bitmapCanvas = new Canvas(bitmap);
			innerCircleRadius = (int) (w / 2 * innerCircleRadiusPercentage);
		}
	}

	public void setDominantMeasurement(DominantMeasurement dominantMeasurement) {
		this.dominantMeasurement = dominantMeasurement;
		requestLayout();
	}

	public DominantMeasurement getDominantMeasurement() {
		return dominantMeasurement;
	}
}